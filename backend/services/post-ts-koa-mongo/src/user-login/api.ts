import { Context, Method, Route } from "../common/utils";
import * as runtypes from "runtypes";
import { Workflow } from ".";
import { checkUserName, Password } from "../common";
import { ResponseError } from "../common/utils/error";

export const route: Route = [Method.POST, "/login"];

const requestBody = runtypes.Record({
    userName: runtypes.String,
    password: runtypes.String,
});

export async function run(ctx: Context, workflow: Workflow): Promise<void> {
    const input = requestBody.check(ctx.getRequestBody());
    if (!checkUserName(input.userName)) {
        throw errors.userNameOrPasswordIncorrect();
    }
    const password = Password.new(input.password);
    if (password === undefined) {
        throw errors.userNameOrPasswordIncorrect();
    }
    const output = await workflow.run({
        userName: input.userName,
        password,
    });
    const expire = ctx.getTokenExpireTime();
    const token = ctx.generateUserToken(output, expire);
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
            }
        }
    )
};
