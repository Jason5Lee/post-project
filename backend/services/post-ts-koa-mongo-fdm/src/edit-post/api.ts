import { Context, Method, parseId, Route, validateRequest } from "../common/utils";
import { Workflow } from ".";
import { fromRequest, ResponseError } from "../common/utils/error";
import { checkTextPostContent, checkUrlPostContent, PostContent, PostId } from "../common";
import * as runtypes from "runtypes";
import { CLIENT_BUG_MESSAGE } from "../common/api";

export const route: Route = [Method.POST, "/post/:id"];

const requestStructure = runtypes.Record({
    text: runtypes.String.optional(),
    url: runtypes.String.optional(),
});

export async function run(ctx: Context, workflow: Workflow) {
    const postId = parseId(ctx.getRouteParam("id"), errors.postNotFound) as PostId;
    const req = validateRequest(requestStructure, ctx.getRequestBody());
    const caller = ctx.getIdentity();
    if (caller === undefined || caller.type !== "User") {
        throw errors.notCreator();
    }
    let newContent: PostContent;
    if (req.text !== undefined && req.url === undefined) {
        checkTextPostContent(req.text, fromRequest());
        newContent = { type: "Text", content: req.text };
    } else if (req.text === undefined && req.url !== undefined) {
        checkUrlPostContent(req.url, fromRequest());
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
                    reason: "post not found",
                    message: "the post does not exist",
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
                    reason: "the user is not the creator of the post",
                    message: "you are not allowed to perform this action",
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
                    reason: "the type of the post is different",
                    message: "you cannot change the post type",
                }
            }
        );
    },
    textUrlExactOne: () => new ResponseError(
        422,
        {
            error: {
                error: "TEXT_URL_EXACT_ONE",
                reason: "exact one of the text and the url field should exist",
                message: CLIENT_BUG_MESSAGE,
            }
        }
    ),
};
errors satisfies Workflow["errors"];
