import { AdminId, Identity, Time, UserId } from "..";
import jwt from "jsonwebtoken";
import { invalidToken } from "../api/auth";
import { formatId, parseId } from ".";

export interface AuthConfig {
    readonly validSecs: number,
    readonly secret: string,
}

export function getIdentity(token: string | undefined, secret: string): Identity | undefined {
    if (token === undefined) {
        return undefined;
    }
    let decoded;
    try {
        decoded = jwt.verify(token, secret);
    } catch (e) {
        if (e instanceof jwt.JsonWebTokenError) {
            throw invalidToken();
        } else {
            throw e;
        }
    }
    
    if (decoded === undefined || typeof decoded === "string") {
        throw invalidToken();
    } else {
        const { userId, adminId } = decoded;
        if (typeof userId === "string") {
            return { type: "User", id: parseId(userId, invalidToken) as UserId };
        } else if (typeof adminId === "string") {
            return { type: "Admin", id: parseId(adminId, invalidToken) as AdminId };
        } else {
            throw invalidToken();
        }
    }
}

export function generateToken(identity: Identity, secret: string, expire: Time): string {
    const payload = identity.type === "User" ? { userId: formatId(identity.id) } : { adminId: formatId(identity.id) };
    return jwt.sign(
        { exp: Math.floor(expire.utc / 1000), ...payload },
        secret,
    );
}
