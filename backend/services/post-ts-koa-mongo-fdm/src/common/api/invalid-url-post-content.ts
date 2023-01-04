import { ErrorBody } from "../utils/error";

export const empty: ErrorBody = {
    error: {
        error: "URL_POST_CONTENT_EMPTY",
        reason: "url is empty",
        message: "url is empty",
    }
};

export const tooLong: ErrorBody = {
    error: {
        error: "URL_POST_CONTENT_TOO_LONG",
        reason: "url is too long",
        message: "url is too long",
    }
};

export const invalid: ErrorBody = {
    error: {
        error: "URL_POST_CONTENT_INVALID",
        reason: "URL is invalid",
        message: "URL is invalid",
    }
};
