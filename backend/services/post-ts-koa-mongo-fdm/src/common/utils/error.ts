export interface ErrorBody {
    readonly error: {
        readonly error: string,
        readonly reason: string,
    }
}

export class ResponseError extends Error {
    constructor(public readonly status: number, public readonly body: ErrorBody) {
        super();
    }
}

export function throwUnexpectedValue(value: never): never {
    throw new Error(`Unexpected value \`${JSON.stringify(value)}\``);
}
