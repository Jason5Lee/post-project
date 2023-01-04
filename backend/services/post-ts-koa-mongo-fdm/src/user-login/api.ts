import { Context, Method, Route } from "../common/utils";
import * as runtypes from "runtypes";
import { Workflow } from ".";
import { checkUserName, Password } from "../common";
import { fromRequest, ResponseError } from "../common/utils/error";

export const route: Route = [Method.POST, "/login"];

const requestBody = runtypes.Record({
    userName: runtypes.String,
    password: runtypes.String,
});

export async function run(ctx: Context, workflow: Workflow): Promise<void> {
    const input = requestBody.check(ctx.getRequestBody());
    checkUserName(input.userName, fromRequest());
    const output = await workflow.run({
        userName: input.userName,
        password: new Password(input.password),
    });
    const expire = ctx.getTokenExpireTime();
    const token = ctx.generateToken({ type: "User", id: output }, expire);
    ctx.setResponse(
        200,
        {
            expire: expire.utc,
            token,
        } satisfies {
            token: string,
            expire: number,
        },
    );
}

export const errors: Workflow["errors"] = {
    userNameOrPasswordIncorrect: () => new ResponseError(
        403,
        {
            error: {
                error: "USER_NAME_OR_PASSWORD_INCORRECT",
                reason: "user name or password incorrect",
                message: "the user name or password is incorrect",
            }
        }
    )
};
