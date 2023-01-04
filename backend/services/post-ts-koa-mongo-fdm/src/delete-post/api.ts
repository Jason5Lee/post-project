import { Context, Method, parseId, Route } from "../common/utils";
import { Workflow } from ".";
import { ResponseError } from "../common/utils/error";
import { PostId } from "../common";

export const route: Route = [Method.DELETE, "/post/:id"];

export async function run(ctx: Context, workflow: Workflow) {
    const id = parseId(ctx.getRouteParam("id"), errors.postNotFound) as PostId;
    const caller = ctx.getIdentity();
    if (caller === undefined) {
        throw errors.notCreatorAdmin();
    }
    await workflow.run(caller, id);
    ctx.setResponse(204, undefined);
}

export const errors: Workflow["errors"] = {
    notCreatorAdmin: () => new ResponseError(
        403,
        {
            error: {
                error: "NOT_CREATOR_ADMIN",
                reason: "The user is not the creator of the post, nor an admin",
                message: "You are not allowed to perform this action",
            }
        }
    ),
    postNotFound: () => new ResponseError(
        404,
        {
            error: {
                error: "POST_NOT_FOUND",
                reason: "The post does not exist",
                message: "The post does not exist",
            }
        }
    ),
};
