import { ErrorBody } from "../utils/error";

export const invalid: ErrorBody = {
    error: {
        error: "INVALID_SIZE",
        reason: "Size must be a safe positive integer",
        message: "Size must be a safe positive integer",
    },
};
