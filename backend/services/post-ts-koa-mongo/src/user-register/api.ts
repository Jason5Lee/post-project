import { Context, Method, Route } from "../common/utils";
import * as runtypes from "runtypes";
import { Workflow } from ".";
import { checkUserName, Password } from "../common";
import { ResponseError } from "../common/utils/error";
import { invalidUserName, invalidPassword } from "../common/api";

export const route: Route = [Method.POST, "/register"];

const requestBody = runtypes.Record({
    userName: runtypes.String,
    password: runtypes.String,
});

export async function run(ctx: Context, workflow: Workflow): Promise<void> {
    const input = requestBody.check(ctx.getRequestBody());
    if (!checkUserName(input.userName)) {
        throw new ResponseError(400, invalidUserName);
    }
    const password = Password.new(input.password);
    if (password === undefined) {
        throw new ResponseError(400, invalidPassword);
    }
    const output = await workflow.run({
        userName: input.userName,
        password,
    });
    ctx.setResponseHeader("Location", "/user/" + output);
    ctx.setResponse(
        201,
        {
            userId: output,
        }
    );
}

export const errors: Workflow["errors"] = {
    userNameAlreadyExists: () => new ResponseError(
        409,
        {
            error: {
                error: "USER_NAME_ALREADY_EXISTS",
                reason: "The user name already exists",
            }
        }
    ),
};

