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

export type InvalidError = (body: ErrorBody) => Error;

export function onInvalidRespond(options: { status: number, prefix?: string }) : InvalidError {
    const { status, prefix } = options;
    if (prefix) {
        return (body) => new ResponseError(status, { error: { ...body.error, error: prefix + body.error.error } });
    }
    return (body) => new ResponseError(status, body);
}

export function onInvalid(err: (body: ErrorBody) => Error) : InvalidError {
    return err;
}

export function throwUnexpectedValue(value: never): never {
    throw new Error(`Unexpected value \`${JSON.stringify(value)}\``);
}
