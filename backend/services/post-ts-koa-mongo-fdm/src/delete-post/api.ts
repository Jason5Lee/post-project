import { Context, Method, Route } from "../common/utils";
import { Workflow } from ".";
import { ResponseError } from "../common/utils/error";
import { PostId } from "../common";

export const route: Route = [Method.DELETE, "/post/:id"];

export async function run(ctx: Context, workflow: Workflow) {
    const id = ctx.getRouteParam("id") as PostId;
    const caller = ctx.getCallerIdentity();
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
                reason: "Only the creator or an admin can delete a post.",
                message: "Only the creator or an admin can delete a post.",
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
