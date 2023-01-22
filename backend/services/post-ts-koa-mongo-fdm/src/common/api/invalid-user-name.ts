import { ErrorBody } from "../utils/error";

export const empty: ErrorBody = {
    error: {
        error: "USER_NAME_EMPTY",
        reason: "User name cannot be empty",
        message: "User name cannot be empty",
    }
};

export const tooShort: ErrorBody = {
    error: {
        error: "USER_NAME_TOO_SHORT",
        reason: "User name must be at least 3 characters",
        message: "User name must be at least 3 characters",
    }
};

export const tooLong: ErrorBody = {
    error: {
        error: "USER_NAME_TOO_LONG",
        reason: "User name must be at most 20 characters",
        message: "User name must be at most 20 characters",
    }
};

export const containsIllegalCharacter: ErrorBody = {
    error: {
        error: "USER_NAME_ILLEGAL",
        reason: "User name can only contain letters, numbers, and underscores",
        message: "User name can only contain letters, numbers, and underscores",
    }
};
