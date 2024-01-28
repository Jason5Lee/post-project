import { ResponseError } from "../utils/error";

export type Token = {
    type: "Admin" | "User",
    token: string,
}

const USER_PREFIX = "Bearer ";
const ADMIN_PREFIX = "Admin ";
export function getToken(header: { authorization?: string | undefined }): Token | undefined {
    if (header.authorization === undefined) {
        return undefined;
    }

    const authorization = header.authorization;
    if (authorization.startsWith(USER_PREFIX)) {
        return {
            type: "User",
            token: authorization.substring(USER_PREFIX.length),
        };
    }
    if (authorization.startsWith(ADMIN_PREFIX)) {
        return {
            type: "Admin",
            token: authorization.substring(ADMIN_PREFIX.length),
        };
    }

    throw invalidAuth();
}

export function invalidAuth(): ResponseError {
    return new ResponseError(
        401,
        {
            error: {
                error: "INVALID_AUTH",
                reason: "The authorization is invalid",
            }
        }
    );
}
