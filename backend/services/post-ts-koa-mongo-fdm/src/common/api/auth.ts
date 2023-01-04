import { CLIENT_BUG_MESSAGE } from ".";
import { ResponseError } from "../utils/error";

export function getToken(header: { authorization?: string | undefined }): string | undefined {
    if (header.authorization === undefined) {
        return undefined;
    }
    const match = header.authorization.match(/^Bearer (.+)$/);
    if (match === null || match.length < 2) {
        throw invalidAuthHeader();
    }
    return match[1];
}

function invalidAuthHeader(): ResponseError {
    return new ResponseError(
        401,
        {
            error: {
                error: "INVALID_AUTH_HEADER",
                reason: "authorization header is invalid",
                message: CLIENT_BUG_MESSAGE,
            }
        }
    );
}

export function invalidToken(): ResponseError {
    return new ResponseError(
        401,
        {
            error: {
                error: "INVALID_TOKEN",
                reason: "authorization token is invalid",
                message: CLIENT_BUG_MESSAGE,
            }
        }
    );
}

export function userOnly(): ResponseError {
    return new ResponseError(
        403,
        {
            error: {
                error: "USER_ONLY",
                reason: "only user can call this API",
                message: "Only users are allowed to perform this action",
            }
        }
    );
}
