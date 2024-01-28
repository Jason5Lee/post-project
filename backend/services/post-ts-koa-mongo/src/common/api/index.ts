import { ErrorBody, ResponseError } from "../utils/error";

export function badRequest(reason: string): ResponseError {
    return new ResponseError(
        400,
        {
            error: {
                error: "BAD_REQUEST",
                reason,
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
        }
    },
};

export const invalidUserName: ErrorBody = {
    error: {
        error: "INVALID_USER_NAME",
        reason: "The user name is invalid",
    }
};

export const invalidTitle: ErrorBody = {
    error: {
        error: "INVALID_TITLE",
        reason: "The title is invalid",
    }
};

export const invalidTextPostContent: ErrorBody = {
    error: {
        error: "INVALID_TEXT_POST_CONTENT",
        reason: "The content of the text post is invalid",
    }
};

export const invalidUrlPostContent: ErrorBody = {
    error: {
        error: "INVALID_URL_POST_CONTENT",
        reason: "The content of the URL post is invalid",
    }
};

export const invalidPassword: ErrorBody = {
    error: {
        error: "INVALID_PASSWORD",
        reason: "The password is invalid",
    }
};

export const invalidTime: ErrorBody = {
    error: {
        error: "INVALID_TIMESTAMP",
        reason: "The timestamp is invalid",
    }
};

export const invalidPage: ErrorBody = {
    error: {
        error: "INVALID_PAGE",
        reason: "The page number is invalid",
    },
};

export const invalidPageSize: ErrorBody = {
    error: {
        error: "INVALID_PAGE_SIZE",
        reason: "The page size is invalid",
    },
};
