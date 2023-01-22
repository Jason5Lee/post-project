import { ErrorBody } from "../utils/error";

export const invalid: ErrorBody = {
    error: {
        error: "INVALID_TIME",
        reason: "Time must be a safe non-negative integer",
        message: "Time must be a safe non-negative integer",
    }
};
