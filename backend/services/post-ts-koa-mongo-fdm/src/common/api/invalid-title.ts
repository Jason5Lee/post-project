import { ErrorBody } from "../utils/error";

export const empty: ErrorBody = {
    error: {
        error: "TITLE_EMPTY",
        reason: "title is empty",
        message: "title is empty",
    }
};

export const tooShort: ErrorBody = {
    error: {
        error: "TITLE_TOO_SHORT",
        reason: "title is too short",
        message: "title is too short",
    }
};

export const tooLong: ErrorBody = {
    error: {
        error: "TITLE_TOO_LONG",
        reason: "title is too long",
        message: "title is too long",
    }
};
