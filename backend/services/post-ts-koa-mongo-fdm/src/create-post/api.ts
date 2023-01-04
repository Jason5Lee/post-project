import { checkTextPostContent, checkTitle, checkUrlPostContent, PostContent } from "../common";
import { userOnly } from "../common/api/auth";
import * as runtypes from "runtypes";
import { Command, Workflow } from ".";
import { Context, formatId, Method, Route, validateRequest } from "../common/utils";
import { fromRequest, ResponseError } from "../common/utils/error";
import { invalidRequest, CLIENT_BUG_MESSAGE } from "../common/api";

export const route: Route = [Method.PUT, "/post"];

const requestBody = runtypes.Record({
    title: runtypes.String,
    text: runtypes.String.optional(),
    url: runtypes.String.optional(),
});

export async function run(ctx: Context, workflow: Workflow) {
    const caller = ctx.getIdentity();
    if (caller?.type !== "User") {
        throw userOnly();
    }

    const req = validateRequest(requestBody, ctx.getRequestBody());
    checkTitle(req.title, fromRequest());
    let content: PostContent;
    if (req.text !== undefined && req.url === undefined) {
        checkTextPostContent(req.text, invalidRequest);
        content = { type: "Text", content: req.text };
    } else if (req.text === undefined && req.url !== undefined) {
        checkUrlPostContent(req.url, invalidRequest);
        content = { type: "Url", content: req.url };
    } else {
        throw errors.textUrlExactOne();
    }

    const command: Command = {
        title: req.title,
        content,
    };
    const result = await workflow.run(caller.id, command);
    ctx.setResponse(201, {
        postId: formatId(result),
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
                    reason: "The title is already used by another post",
                    message: "The title is already used by another post",
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
