import { Context, formatId, Method, parseId, Route } from "../common/utils";
import { Condition, Workflow } from ".";
import { fromRequest, ResponseError } from "../common/utils/error";
import { checkSize, Time, UserId } from "../common";
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
        const time = new Time(+beforeParam, fromRequest({ prefix: "BEFORE_" }));
        condition = { type: "Before", time };
    } else {
        if (afterParam !== undefined) {
            const time = new Time(+afterParam, fromRequest({ prefix: "AFTER_" }));
            condition = { type: "After", time };
        }
    }
    const size = checkSize(sizeParam === undefined ? undefined : +sizeParam);
    const creator = creatorParam === undefined ? undefined : parseId(creatorParam, errors.creatorNotFound) as UserId;

    const output = await workflow.run({
        condition,
        size,
        creator,
    });
    ctx.setResponse(
        200,
        {
            posts: output.posts.map(post => ({
                id: formatId(post.id),
                title: post.title,
                creatorId: formatId(post.creator.id),
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
                reason: "creator not found",
                message: "the creator does not exist",
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
