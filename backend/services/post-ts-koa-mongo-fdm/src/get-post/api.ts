import { PostId } from "../common";
import { Workflow } from ".";
import { Context, Method, Route } from "../common/utils";
import { ResponseError } from "../common/utils/error";

export const route: Route = [Method.GET, "/post/:id"];

export async function run(ctx: Context, workflow: Workflow) {
    const id = ctx.getRouteParam("id") as PostId;
    const result = await workflow.run(id);
    ctx.setResponse(200, {
        creatorId: result.creator.id,
        creatorName: result.creator.name,
        creationTime: result.creationTime.utc,
        title: result.title,
        text: (result.content.type === "Text" ? result.content.content : undefined),
        url: (result.content.type === "Url" ? result.content.content : undefined),
        lastModified: result.lastModified?.utc,
    } satisfies {
        creatorId: string,
        creatorName: string,
        creationTime: number,
        title: string,
        text?: string,
        url?: string,
        lastModified?: number,
    });
}

export const errors: Workflow["errors"] = {
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
