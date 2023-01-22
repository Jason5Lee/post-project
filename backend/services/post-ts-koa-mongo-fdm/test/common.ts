import { ErrorBody } from "../src/common/utils/error";

export class ExpectedError extends Error {
    constructor(public readonly err?: ErrorBody) { super(); }
}

export function expectInvalid(err: ErrorBody): Error {
    return new ExpectedError(err);
}
