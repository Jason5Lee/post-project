import { ErrorBody } from "../utils/error";

export const empty: ErrorBody = {
    error: {
        error: "TEXT_POST_CONTENT_EMPTY",
        reason: "text post content is empty",
        message: "text post content is empty",
    }
};
export const tooLong: ErrorBody = {
    error: {
        error: "TEXT_POST_CONTENT_TOO_LONG",
        reason: "text post content is too long",
        message: "text post content is too long",
    }
};
