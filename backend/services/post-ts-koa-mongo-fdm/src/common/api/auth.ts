import { CLIENT_BUG_MESSAGE } from ".";
import { ResponseError } from "../utils/error";

export function getToken(header: { authorization?: string | undefined }): string | undefined {
    if (header.authorization === undefined) {
        return undefined;
    }
    const match = header.authorization.match(/^Bearer (.+)$/);
    if (match === null || match.length < 2) {
        throw invalidAuth();
    }
    return match[1];
}

export function invalidAuth(): ResponseError {
    return new ResponseError(
        401,
        {
            error: {
                error: "INVALID_AUTH",
                reason: "The authorization is invalid",
                // When this happens, the client should remove the token instead of showing this message.
                message: CLIENT_BUG_MESSAGE,
            }
        }
    );
}
