import * as validationApi from "../api";

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

export type Validation = (body: ErrorBody, value?: unknown) => Error;

export function fromRequest(options?: { prefix?: string }) : Validation {
    if (options) {
        const { prefix } = options;
        if (prefix) {
            return (body) => validationApi.invalidRequest({ error: { ...body.error, error: prefix + body.error.error } });
        }
    }
    return validationApi.invalidRequest;
}

export function fromDB({ collection, id, field }: { collection: string, id: unknown, field: string }) : Validation {
    return (body, value) => Error(`Invalid value ${JSON.stringify(value)} in collection "${collection}", ID ${JSON.stringify(id)}, field "${field}", error "${body.error.error}": ${body.error.reason}`);
}

export function throwUnexpectedValue(value: never): never {
    throw new Error(`Unexpected value ${JSON.stringify(value)}`);
}