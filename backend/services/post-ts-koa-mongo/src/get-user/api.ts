import { Context, Method, Route } from "../common/utils";
import { Workflow } from ".";
import { ResponseError } from "../common/utils/error";
import { UserId } from "../common";

export const route: Route = [Method.GET, "/user/:id"];

export async function run(ctx: Context, workflow: Workflow): Promise<void> {
    const id = ctx.getRouteParam("id") as UserId;
    const user = await workflow.run(id);
    ctx.setResponse(
        200,
        {
            userName: user.userName,
            creationTime: user.creationTime.utc,
        } satisfies {
            userName: string,
            creationTime: number,
        }
    );
}

export const errors: Workflow["errors"] = {
    userNotFound: () => new ResponseError(
        404,
        {
            error: {
                error: "USER_NOT_FOUND",
                reason: "The user does not exist",
            }
        }
    ),
};
