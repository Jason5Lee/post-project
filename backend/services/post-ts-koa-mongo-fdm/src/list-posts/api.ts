import { Context, Method, Route } from "../common/utils";
import { Condition, Workflow } from ".";
import { onInvalidRespond, ResponseError } from "../common/utils/error";
import { checkSize, newTime, UserId } from "../common";
import { CLIENT_BUG_MESSAGE } from "../common/api";

export const route: Route = [Method.GET, "/post"];

export async function run(ctx: Context, workflow: Workflow): Promise<void> {
    const beforeParam = ctx.getQueryParam("before", { optional: true });
    const afterParam = ctx.getQueryParam("after", { optional: true });
    const sizeParam = ctx.getQueryParam("size", { optional: true });
    const creatorParam = ctx.getQueryParam("creator", { optional: true });

    let condition: Condition | undefined = undefined;
    if (beforeParam !== undefined) {
        if (afterParam !== undefined) {
            throw errors.bothBeforeAfter();
        }
        const time = newTime(+beforeParam, onInvalidRespond({ status: 422, prefix: "BEFORE_" }));
        condition = { type: "Before", time };
    } else {
        if (afterParam !== undefined) {
            const time = newTime(+afterParam, onInvalidRespond({ status: 422, prefix: "AFTER_" }));
            condition = { type: "After", time };
        }
    }
    const size = checkSize(sizeParam === undefined ? undefined : +sizeParam, onInvalidRespond({ status: 422 }));
    const creator = creatorParam === undefined ? undefined : creatorParam as UserId;

    const output = await workflow.run({
        condition,
        size,
        creator,
    });
    ctx.setResponse(
        200,
        {
            posts: output.posts.map(post => ({
                id: post.id,
                title: post.title,
                creatorId: post.creator.id,
                creatorName: post.creator.name,
                creationTime: post.creationTime.utc,
            })),
        } satisfies {
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
    bothBeforeAfter: () => new ResponseError(
        400,
        {
            error: {
                error: "BOTH_BEFORE_AFTER",
                reason: "only one of before and after should present",
                message: CLIENT_BUG_MESSAGE,
            }
        }
    )
};
errors satisfies Workflow["errors"];
