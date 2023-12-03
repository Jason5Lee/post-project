import { ErrorBody } from "../utils/error";

export const invalidPageSize: ErrorBody = {
    error: {
        error: "INVALID_PAGE_SIZE",
        reason: "page size is invalid",
        message: "page size is invalid",
    },
};

export const pageSizeTooLarge: ErrorBody = {
    error: {
        error: "PAGE_SIZE_TOO_LARGE",
        reason: "page size is too large",
        message: "page size is too large",
    },
};
