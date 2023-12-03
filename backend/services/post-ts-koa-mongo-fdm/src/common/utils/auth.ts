import { Identity, Time, UserId } from "..";
import jwt from "jsonwebtoken";
import { Token, invalidAuth } from "../api/auth";

export interface AuthConfig {
    readonly validSecs: number,
    readonly secret: string,
}

export function getIdentity(token: Token | undefined, secret: string, adminToken: string): Identity | undefined {
    if (token === undefined) {
        return undefined;
    }
    if (token.type === "Admin") {
        if (token.token !== adminToken) {
            throw invalidAuth();
        }
        return { type: "Admin" };
    }
    let decoded;
    try {
        decoded = jwt.verify(token.token, secret);
    } catch (e) {
        if (e instanceof jwt.JsonWebTokenError) {
            throw invalidAuth();
        } else {
            throw e;
        }
    }
    
    if (decoded === undefined || typeof decoded === "string") {
        throw invalidAuth();
    } else {
        const { userId } = decoded;
        if (typeof userId === "string") {
            return { type: "User", id: userId as UserId };
        }

        throw invalidAuth();
    }
}

export function generateToken(user: UserId, secret: string, expire: Time): string {
    return jwt.sign(
        { exp: Math.floor(expire.utc / 1000), userId: user },
        secret,
    );
}
