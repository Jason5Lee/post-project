import { AdminId, Identity, Time, UserId } from "..";
import jwt from "jsonwebtoken";
import { invalidAuth } from "../api/auth";

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
            throw invalidAuth();
        } else {
            throw e;
        }
    }
    
    if (decoded === undefined || typeof decoded === "string") {
        throw invalidAuth();
    } else {
        const { userId, adminId } = decoded;
        if (typeof userId === "string") {
            return { type: "User", id: userId as UserId };
        } else if (typeof adminId === "string") {
            return { type: "Admin", id: adminId as AdminId };
        } else {
            throw invalidAuth();
        }
    }
}

export function generateToken(identity: Identity, secret: string, expire: Time): string {
    const payload = identity.type === "User" ? { userId: identity.id } : { adminId: identity.id };
    return jwt.sign(
        { exp: Math.floor(expire.utc / 1000), ...payload },
        secret,
    );
}
