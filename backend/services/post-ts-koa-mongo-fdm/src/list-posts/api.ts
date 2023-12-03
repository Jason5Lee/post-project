import { Context, Method, Route } from "../common/utils";
import { Workflow } from ".";
import { onInvalidRespond, ResponseError } from "../common/utils/error";
import { checkPage, checkPageSize, newTime, UserId, PageSize } from "../common";

export const route: Route = [Method.GET, "/post"];

export async function run(ctx: Context, workflow: Workflow): Promise<void> {
    if (ctx.getQueryParam("search", { optional: true }) !== undefined) {
        throw errors.searchNotImplemented();
    }

    const page = +ctx.getQueryParam("page");
    const pageSize = +ctx.getQueryParam("pageSize");
    const creator = ctx.getQueryParam("creator", { optional: true }) as (UserId | undefined);

    checkPage(page, onInvalidRespond({ status: 422 }));
    checkPageSize(pageSize, 50 as PageSize, onInvalidRespond({ status: 422 }));

    const output = await workflow.run({
        page,
        pageSize,
        creator,
    });
    ctx.setResponse(
        200,
        {
            total: output.total,
            posts: output.posts.map(post => ({
                id: post.id,
                title: post.title,
                creatorId: post.creator.id,
                creatorName: post.creator.name,
                creationTime: post.creationTime.utc,
            })),
        } satisfies {
            total: number,
            posts: {
                id: string,
                title: string,
                creatorId: string,
                creatorName: string,
                creationTime: number,
            }[]
        },
    );
}

export const errors = {
    creatorNotFound: () => new ResponseError(
        404,
        {
            error: {
                error: "CREATOR_NOT_FOUND",
                reason: "The creator does not exist",
                message: "The creator does not exist",
            },
        }
    ),
    searchNotImplemented: () => new ResponseError(
        501,
        {
            error: {
                error: "SEARCH_NOT_IMPLEMENTED",
                reason: "search not implemented",
                message: "the search function is not implemented in this service",
            }
        }
    )
};
errors satisfies Workflow["errors"];
