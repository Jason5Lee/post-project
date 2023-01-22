import { ErrorBody } from "../utils/error";

export const empty: ErrorBody = {
    error: {
        error: "PASSWORD_EMPTY",
        reason: "Password cannot be empty",
        message: "Password cannot be empty",
    }
};

export const tooShort: ErrorBody = {
    error: {
        error: "PASSWORD_TOO_SHORT",
        reason: "password is too short",
        message: "password is too short",
    },
};

export const tooLong: ErrorBody = {
    error: {
        error: "PASSWORD_TOO_LONG",
        reason: "password is too long",
        message: "password is too long",
    },
};
