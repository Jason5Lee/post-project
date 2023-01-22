import { ResponseError } from "../utils/error";

export const CLIENT_BUG_MESSAGE = "Something went wrong. Looks like a bug of the client. Please report this issue to the client implementation.";

export function badRequest(reason: string): ResponseError {
    return new ResponseError(
        400,
        {
            error: {
                error: "BAD_REQUEST",
                reason,
                message: CLIENT_BUG_MESSAGE,
            }
        }
    );
}

export function internalServerError(id: string): ResponseError {
    return new ResponseError(
        500,
        {
            error: {
                error: "INTERNAL_SERVER_ERROR",
                reason: "An internal server error occurred, trace ID: " + id,
                message: "Something went wrong, please try again later.",
            }
        }
    );
}

export const apiNotFound = {
    status: 404,
    body: {
        error: {
            error: "API_NOT_FOUND",
            reason: "API not found. Please check API path and method.",
            message: CLIENT_BUG_MESSAGE,
        }
    },
};
