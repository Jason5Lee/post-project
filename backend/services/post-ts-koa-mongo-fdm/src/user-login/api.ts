import { Context, Method, Route } from "../common/utils";
import * as runtypes from "runtypes";
import { Workflow } from ".";
import { checkUserName, Password } from "../common";
import { onInvalid, ResponseError } from "../common/utils/error";

export const route: Route = [Method.POST, "/login"];

const requestBody = runtypes.Record({
    userName: runtypes.String,
    password: runtypes.String,
});

export async function run(ctx: Context, workflow: Workflow): Promise<void> {
    const input = requestBody.check(ctx.getRequestBody());
    checkUserName(input.userName, onInvalid(errors.userNameOrPasswordIncorrect));
    const output = await workflow.run({
        userName: input.userName,
        password: new Password(input.password, onInvalid(errors.userNameOrPasswordIncorrect)),
    });
    const expire = ctx.getTokenExpireTime();
    const token = ctx.generateToken({ type: "User", id: output }, expire);
    ctx.setResponse(
        200,
        {
            id: output,
            expire: expire.utc,
            token,
        } satisfies {
            id: string,
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
                reason: "The user name or password is incorrect",
                message: "The user name or password is incorrect",
            }
        }
    )
};
