import { ErrorBody } from "../utils/error";

export const nonPositiveInteger: ErrorBody = {
    error: {
        error: "SIZE_NON_POSITIVE_INTEGER",
        reason: "size should be a positive integer",
        message: "size should be a positive integer",
    },
};
