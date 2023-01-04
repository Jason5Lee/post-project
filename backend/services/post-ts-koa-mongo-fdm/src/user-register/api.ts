import { Context, formatId, Method, Route } from "../common/utils";
import * as runtypes from "runtypes";
import { Workflow } from ".";
import { checkUserName, Password } from "../common";
import { fromRequest, ResponseError } from "../common/utils/error";

export const route: Route = [Method.POST, "/register"];

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
    ctx.setResponse(
        201,
        {
            userId: formatId(output),
        }
    );
}

export const errors: Workflow["errors"] = {
    userNameAlreadyExists: () => new ResponseError(
        409,
        {
            error: {
                error: "USER_NAME_ALREADY_EXISTS",
                reason: "user name already exists",
                message: "the user name already exists",
            }
        }
    ),
};

