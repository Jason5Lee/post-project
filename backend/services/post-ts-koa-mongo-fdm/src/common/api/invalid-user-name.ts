import { ErrorBody } from "../utils/error";

export const empty: ErrorBody = {
    error: {
        error: "USER_NAME_EMPTY",
        reason: "user name is empty",
        message: "user name is empty",
    }
};

export const tooShort: ErrorBody = {
    error: {
        error: "USER_NAME_TOO_SHORT",
        reason: "user name is too short",
        message: "user name is too short",
    }
};

export const tooLong: ErrorBody = {
    error: {
        error: "USER_NAME_TOO_LONG",
        reason: "user name is too long",
        message: "user name is too long",
    }
};

export const containsIllegalCharacter: ErrorBody = {
    error: {
        error: "USER_NAME_ILLEGAL",
        reason: "user name contains illegal character",
        message: "user name contains illegal character",
    }
};
