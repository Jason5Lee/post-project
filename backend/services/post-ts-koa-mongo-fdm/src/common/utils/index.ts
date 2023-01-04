import { Id, Identity, Time } from "..";
import Koa from "koa";
import { AuthConfig, getIdentity, generateToken } from "./auth";
import { getToken } from "../api/auth";
import * as runtypes from "runtypes";
import { badRequest } from "../api";
import Router from "@koa/router";
import { Db as MongoDb, ObjectId } from "mongodb";
import { Encryption } from "./encryption";
import { RuntypeBase } from "runtypes/lib/runtype";
import { throwUnexpectedValue } from "./error";

export function now(): Time {
    return { utc: Date.now() };
}

export class Context {
    constructor(
        readonly koaCtx: Koa.Context,
        public deps: Deps,
    ) {}

    getIdentity(): Identity | undefined {
        return getIdentity(getToken(this.koaCtx.header), this.deps.authConfig.secret);
    }

    getTokenExpireTime(): Time {
        return { utc: Date.now() + this.deps.authConfig.validSecs * 1000 };
    }

    generateToken(identity: Identity, expire: Time): string {
        return generateToken(identity, this.deps.authConfig.secret, expire);
    }

    getRequestBody(): unknown { return this.koaCtx.request.body; }

    setResponse(status: number, body: unknown): void {
        console.log("set response to", status, body);
        this.koaCtx.status = status;
        this.koaCtx.body = body;
    }

    getRouteParam(name: string): string {
        const value = this.koaCtx.params[name];
        if (value === undefined) {
            throw badRequest(`Missing route parameter "${name}"`);
        }
        return value;
    }

    getQueryParam(name: string): string 
    getQueryParam(name: string, options: { optional: true }): string | undefined
    getQueryParam(name: string, options?: { optional: true }): string | undefined {
        const value = this.koaCtx.query[name];
        if (value === undefined) {
            if (options?.optional) {
                return undefined;
            } else {
                throw badRequest(`Missing query parameter "${name}"`);
            }
        }
        if (typeof value === "object") {
            throw badRequest(`Query parameter "${name}" should only appear once`);
        }

        return value;
    }
}

export interface Deps {
    readonly mongoDb: MongoDb,
    readonly authConfig: AuthConfig,
    readonly encryption: Encryption,

    close(): Promise<void>;
}

export enum Method {
    GET = "GET",
    POST = "POST",
    PUT = "PUT",
    DELETE = "DELETE",
}

export type Route = [Method, string];

export function validateRequest<A>(structure: RuntypeBase<A>, request: unknown): A {
    try {
        return structure.check(request);
    } catch (e) {
        if (e instanceof runtypes.ValidationError) {
            throw badRequest(e.message);
        } else {
            throw e;
        }
    }
}

interface ApiPackage<Workflow> {
    readonly route: Route,
    readonly run: (ctx: Context, workflow: Workflow) => Promise<void>;
}
interface ImplPackage<Workflow> {
    readonly WorkflowImpl: {
        new(deps: Deps): Workflow;
    }
}

export function addRoute<Workflow>(router: Router, deps: Deps, api: ApiPackage<Workflow>, impl: ImplPackage<Workflow>) {
    const [method, path] = api.route;
    const workflow= new impl.WorkflowImpl(deps);
    const runApi = api.run;
    const apiFunc = async (koaCtx: Koa.Context, next: Koa.Next) => {
        const ctx = new Context(
            koaCtx,
            deps,
        );
        await runApi(ctx, workflow);
        await next();
    };
    switch (method) {
        case Method.GET:
            router.get(path, apiFunc);
            break;
        case Method.POST:
            router.post(path, apiFunc);
            break;
        case Method.PUT:
            router.put(path, apiFunc);
            break;
        case Method.DELETE:
            router.delete(path, apiFunc);
            break;
        default:
            throwUnexpectedValue(method);
    }
}

export function parseId(id: string, error: () => Error): Id {
    const idBuf = Buffer.from(id, "base64url");
    if (idBuf.toString("base64url") !== id || !ObjectId.isValid(idBuf)) {
        throw error();
    }
    return new ObjectId(idBuf);
}

export function formatId(id: Id): string {
    return id.id.toString("base64url");
}
