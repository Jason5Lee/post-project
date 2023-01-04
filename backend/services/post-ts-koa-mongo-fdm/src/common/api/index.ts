import { ErrorBody, ResponseError } from "../utils/error";

export const CLIENT_BUG_MESSAGE = "Something went wrong. Looks like a bug of the client. Please report this issue to the client implementation.";

/**
 * Error transforms for the invalid request.
 * It creates a response of 422 Unprocessable Entity.
 */
export function invalidRequest(body: ErrorBody): Error {
    return new ResponseError(422, body);
}

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
                reason: "internal server error, trace id: " + id,
                message: "something went wrong",
            }
        }
    );
}

export const apiNotFound: ErrorBody = {
    error: {
        error: "API_NOT_FOUND",
        reason: "API not found. Please check API path and method.",
        message: CLIENT_BUG_MESSAGE,
    }
};
