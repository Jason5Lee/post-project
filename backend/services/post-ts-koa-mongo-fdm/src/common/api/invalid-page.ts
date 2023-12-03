import { ErrorBody } from "../utils/error";

export const invalidPage: ErrorBody = {
    error: {
        error: "INVALID_PAGE",
        reason: "page is invalid",
        message: "page is invalid",
    },
};
