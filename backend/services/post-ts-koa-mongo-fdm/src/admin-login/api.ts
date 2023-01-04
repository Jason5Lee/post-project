import { Context, Method, parseId, Route, validateRequest } from "../common/utils";
import * as runtypes from "runtypes";
import { AdminId, Password } from "../common";
import { Query, Workflow } from ".";
import { ResponseError } from "../common/utils/error";

export const route: Route = [Method.POST, "/admin/login"];

const requestBody = runtypes.Record({
    id: runtypes.String,
    password: runtypes.String,
});

export async function run(ctx: Context, workflow: Workflow) {
    const req = validateRequest(requestBody, ctx.getRequestBody());
    const query: Query = {
        id: parseId(req.id, errors.idOrPasswordIncorrect) as AdminId,
        password: new Password(req.password),
    };
    const result = await workflow.run(query);
    const expire = ctx.getTokenExpireTime();
    const token: string = ctx.generateToken({ type: "Admin", id: result }, expire);
    ctx.setResponse(200, { 
        token: token,
        expire: expire.utc, 
    } satisfies {
        token: string,
        expire: number,
    });
}

export const errors: Workflow["errors"] = {
    idOrPasswordIncorrect: () => new ResponseError(
        400,
        {
            error: {
                error: "ID_OR_PASSWORD_INCORRECT",
                reason: "The admin ID does not exist, or the password is incorrect",
                message: "Admin ID or password incorrect",
            }
        }
    )
};
