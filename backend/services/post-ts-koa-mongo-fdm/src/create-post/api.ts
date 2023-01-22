import { checkTextPostContent, checkTitle, checkUrlPostContent, PostContent } from "../common";
import * as runtypes from "runtypes";
import { Command, Workflow } from ".";
import { Context, Method, Route, validateRequest } from "../common/utils";
import { onInvalidRespond, ResponseError } from "../common/utils/error";
import { CLIENT_BUG_MESSAGE } from "../common/api";

export const route: Route = [Method.PUT, "/post"];

const requestBody = runtypes.Record({
    title: runtypes.String,
    text: runtypes.String.optional(),
    url: runtypes.String.optional(),
});

export async function run(ctx: Context, workflow: Workflow) {
    const caller = ctx.getCallerIdentity();
    if (caller?.type !== "User") {
        throw errors.userOnly();
    }

    const req = validateRequest(requestBody, ctx.getRequestBody());
    checkTitle(req.title, onInvalidRespond({ status: 422 }));
    let content: PostContent;
    if (req.text !== undefined && req.url === undefined) {
        checkTextPostContent(req.text, onInvalidRespond({ status: 422 }));
        content = { type: "Text", content: req.text };
    } else if (req.text === undefined && req.url !== undefined) {
        checkUrlPostContent(req.url, onInvalidRespond({ status: 422 }));
        content = { type: "Url", content: req.url };
    } else {
        throw errors.textUrlExactOne();
    }

    const command: Command = {
        title: req.title,
        content,
    };
    const result = await workflow.run(caller.id, command);
    ctx.setResponseHeader("Location", "/post/" + result);
    ctx.setResponse(201, {
        postId: result,
    } satisfies {
        postId: string,
    });
    
}

export const errors = {
    duplicateTitle: function (): Error {
        return new ResponseError(
            409,
            {
                error: {
                    error: "DUPLICATE_TITLE",
                    reason: "The title is already used",
                    message: "The title is already used",
                }
            }
        );
    },
    textUrlExactOne: () => new ResponseError(
        422,
        {
            error: {
                error: "TEXT_URL_EXACT_ONE",
                reason: "Exactly one of `text` and `url` must be provided",
                message: CLIENT_BUG_MESSAGE,
            }
        }
    ),
    userOnly: () => new ResponseError(
        403,
        {
            error: {
                error: "USER_ONLY",
                reason: "Only users can create post",
                message: "Only users can create post",
            }
        }
    ),
};
errors satisfies Workflow["errors"];
