export interface ErrorBody {
    readonly error: {
        readonly error: string,
        readonly reason: string,
        readonly message: string,
    }
}

export class ResponseError extends Error {
    constructor(public readonly status: number, public readonly body: ErrorBody) {
        super();
    }
}

export type InvalidError = (body: ErrorBody, value: unknown) => Error;

export function onInvalidRespond(options: { status: number, prefix?: string }) : InvalidError {
    const { status, prefix } = options;
    if (prefix) {
        return (body) => new ResponseError(status, { error: { ...body.error, error: prefix + body.error.error } });
    }
    return (body) => new ResponseError(status, body);
}

export function onInvalid(err: (body: ErrorBody, value: unknown) => Error) : InvalidError {
    return err;
}

export function onInvalidHandleInDB({ collection, id, field }: { collection: string, id: unknown, field: string }) : InvalidError {
    return (body, value) => Error(`Invalid value \`${JSON.stringify(value)}\` in collection \`${collection}\`, ID \`${JSON.stringify(id)}\`, field \`${field}\`, error ${body.error.error}: ${body.error.reason}`);
}

export function assertValid(body: ErrorBody, value: unknown): Error {
    return new Error(`Assertion failed. Invalid value \`${JSON.stringify(value)}\`, ${body.error.error}: ${body.error.reason}`);
}

export function throwUnexpectedValue(value: never): never {
    throw new Error(`Unexpected value \`${JSON.stringify(value)}\``);
}
