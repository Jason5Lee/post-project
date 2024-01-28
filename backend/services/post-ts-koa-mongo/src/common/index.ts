declare const __brand: unique symbol;

export interface Time {
    readonly utc: number,
    [__brand]: "Time",
}

export type Identity = {
    readonly type: "User",
    readonly id: UserId,
} | {
    readonly type: "Admin",
}

export type UserId = string & { [__brand]: "UserId" };

export type UserName = string & { [__brand]: "UserName" };

import { PasswordEncryptor, PasswordVerifier } from "./utils/password";
export class Password {
    private constructor(private readonly plain: string) { }
    static new(plain: string): Password | undefined { return checkPassword(plain) ? new Password(plain) : undefined; }
    encrypt(encryptor: PasswordEncryptor): Promise<string> { return encryptor.encrypt(this.plain); }
    verify(verifier: PasswordVerifier): Promise<boolean> { return verifier.verify(this.plain); }
}

export type LengthLimit = number & { [__brand]: "LengthLimit" };
export type PostId = string & { [__brand]: "PostId" };
export type Title = string & { [__brand]: "Title" };

export type TextPostContent = string & { [__brand]: "TextPostContent" };
export type UrlPostContent = string & { [__brand]: "UrlPostContent" };
export type PostContent = {
    readonly type: "Text",
    readonly content: TextPostContent,
} | {
    readonly type: "Url",
    readonly content: UrlPostContent,
};

export type Page = number & { [__brand]: "Page" };
export type PageSize = number & { [__brand]: "PageSize" };

export function newTime(utc: number): Time | undefined {
    if (!Number.isSafeInteger(utc) || utc <= 0) {
        return undefined;
    }
    return { utc } as Time;
}

export function checkUserName(value: string): value is UserName {
    return value.length >= 3 && value.length <= 20 && /^[a-zA-Z0-9_-]*$/.test(value);
}

export function checkTitle(value: string): value is Title {
    return value.length > 0 && value.length <= 171;
}

export function checkTextPostContent(value: string): value is TextPostContent {
    return value.length <= 65535;
}

export function checkUrlPostContent(value: string): value is UrlPostContent {
    if (value.length == 0 || value.length > 65535) {
        return false;
    }

    try {
        new URL(value);
    } catch (e) {
        if (e instanceof TypeError) {
            return false;
        }
        throw e;
    }
    return true;
}

export function checkPassword(plain: string): boolean {
    return plain.length >= 5 && plain.length <= 72; // Limitation of bcrypt
}

export function checkPage(value: number): value is Page {
    return value > 0 && Number.isSafeInteger(value);
}

export function checkPageSize(value: number): value is PageSize {
    return value > 0 && value <= 50;
}
