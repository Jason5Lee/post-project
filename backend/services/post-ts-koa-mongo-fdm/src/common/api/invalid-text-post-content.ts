import { ErrorBody } from "../utils/error";

export const empty: ErrorBody = {
    error: {
        error: "TEXT_POST_CONTENT_EMPTY",
        reason: "Text post content cannot be empty",
        message: "Text post content cannot be empty",
    }
};
export const tooLong: ErrorBody = {
    error: {
        error: "TEXT_POST_CONTENT_TOO_LONG",
        reason: "text post content is too long",
        message: "text post content is too long",
    }
};
