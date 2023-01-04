import { ErrorBody } from "../utils/error";

export const negative: ErrorBody = {
    error: {
        error: "TIME_NEGATIVE",
        reason: "time is negative",
        message: "time is negative",
    }
};
export const notSafeInteger: ErrorBody = {
    error: {
        error: "TIME_NOT_SAFE_INTEGER",
        reason: "time is not a 64-bit-floating-point-safe integer",
        message: "time is not an integer or too large",
    }
};
