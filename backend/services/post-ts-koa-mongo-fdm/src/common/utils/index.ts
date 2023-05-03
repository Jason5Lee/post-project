import { Identity, newTime, Time } from "..";
import Koa from "koa";
import { AuthConfig, getIdentity, generateToken } from "./auth";
import { getToken } from "../api/auth";
import * as runtypes from "runtypes";
import { badRequest } from "../api";
import Router from "@koa/router";
import { Db as MongoDb } from "mongodb";
import { RuntypeBase } from "runtypes/lib/runtype";
import { throwUnexpectedValue } from "./error";
import { PasswordEncryptor } from "./password";

export function now(): Time {
    return { utc: Date.now() } as Time;
}

export class Context {
    constructor(
        readonly koaCtx: Koa.Context,
        public deps: Deps,
    ) {}

    getCallerIdentity(): Identity | undefined {
        return getIdentity(getToken(this.koaCtx.header), this.deps.authConfig.secret);
    }

    getTokenExpireTime(): Time {
        return newTime(
            Date.now() + this.deps.authConfig.validSecs * 1000,
            () => new Error("The expiration time for the token is invalid, which is likely due to an overly prolonged validity period.")
        );
    }

    generateToken(identity: Identity, expire: Time): string {
        return generateToken(identity, this.deps.authConfig.secret, expire);
    }

    getRequestBody(): unknown { return this.koaCtx.request.body; }

    setResponse(status: number, body: unknown): void {
        this.koaCtx.status = status;
        this.koaCtx.body = body;
    }

    setResponseHeader(field: string, val: string | string[]) {
        this.koaCtx.set(field, val);
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
                throw badRequest(`Missing query parameter \`${name}\``);
            }
        }
        if (typeof value === "object") {
            throw badRequest(`Query parameter \`${name}\` should only appear once`);
        }

        return value;
    }
}

export interface Deps {
    readonly mongoDb: MongoDb,
    readonly authConfig: AuthConfig,
    readonly encryptor: PasswordEncryptor,

    close(): Promise<void>;
}

export enum Method {
    GET = "GET",
    POST = "POST",
    DELETE = "DELETE",
    PATCH = "PATCH",
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

export function addRoute<Workflow>(router: Router, deps: Deps, api: ApiPackage<Workflow>, workflowImpl: { new(deps: Deps): Workflow}) {
    const [method, path] = api.route;
    const workflow= new workflowImpl(deps);
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
        case Method.DELETE:
            router.delete(path, apiFunc);
            break;
        case Method.PATCH:
            router.patch(path, apiFunc);
            break;
        default:
            throwUnexpectedValue(method);
    }
}
