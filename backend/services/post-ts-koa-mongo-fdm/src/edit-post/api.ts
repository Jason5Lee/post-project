import { Context, Method, Route, validateRequest } from "../common/utils";
import { Workflow } from ".";
import { ResponseError } from "../common/utils/error";
import { checkTextPostContent, checkUrlPostContent, PostContent, PostId } from "../common";
import * as runtypes from "runtypes";
import { invalidTextPostContent, invalidUrlPostContent } from "../common/api";

export const route: Route = [Method.PATCH, "/post/:id"];

const requestStructure = runtypes.Record({
    text: runtypes.String.optional(),
    url: runtypes.String.optional(),
});

export async function run(ctx: Context, workflow: Workflow) {
    const caller = ctx.getCallerIdentity();
    if (caller === undefined || caller.type !== "User") {
        throw errors.notCreator();
    }
    const postId = ctx.getRouteParam("id") as PostId;
    const req = validateRequest(requestStructure, ctx.getRequestBody());
    let newContent: PostContent;
    if (req.text !== undefined && req.url === undefined) {
        if (!checkTextPostContent(req.text)) {
            throw new ResponseError(400, invalidTextPostContent);
        }
        newContent = { type: "Text", content: req.text };
    } else if (req.text === undefined && req.url !== undefined) {
        if (!checkUrlPostContent(req.url)) {
            throw new ResponseError(400, invalidUrlPostContent);
        }
        newContent = { type: "Url", content: req.url };
    } else {
        throw errors.textUrlExactOne();
    }
    await workflow.run(caller.id, {
        id: postId,
        newContent,
    });
    ctx.setResponse(204, undefined);
}

export const errors = {
    postNotFound: function (): Error {
        return new ResponseError(
            404,
            {
                error: {
                    error: "POST_NOT_FOUND",
                    reason: "The post does not exist",
                }
            }
        );
    },
    notCreator: function (): Error {
        return new ResponseError(
            403,
            {
                error: {
                    error: "NOT_CREATOR",
                    reason: "You are not the creator of the post",
                }
            }
        );
    },
    typeDiff: function (): Error {
        return new ResponseError(
            400,
            {
                error: {
                    error: "TYPE_DIFF",
                    reason: "The type of the post cannot be changed",
                }
            }
        );
    },
    textUrlExactOne: () => new ResponseError(
        400,
        {
            error: {
                error: "TEXT_URL_EXACT_ONE",
                reason: "Exactly one of `text` and `url` must be provided",
            }
        }
    ),
};
errors satisfies Workflow["errors"];
