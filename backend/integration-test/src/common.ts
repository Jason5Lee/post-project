export interface ErrorResponse {
    error: {
        error: string,
    }
}

export interface UserToken {
    userId: string,
    token: string,
}

export function assert(condition: boolean) {
    if (!condition) {
        throw new Error("Assertion Failed");
    }
}
