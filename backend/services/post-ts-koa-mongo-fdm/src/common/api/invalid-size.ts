import { ErrorBody } from "../utils/error";

export const nonPositive: ErrorBody = {
    error: {
        error: "SIZE_NON_POSITIVE",
        reason: "size should be a positive number",
        message: "size should be a positive number",
    },
};
