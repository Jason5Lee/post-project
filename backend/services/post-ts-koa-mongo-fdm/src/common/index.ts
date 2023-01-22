import { InvalidError } from "./utils/error";

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
    readonly id: AdminId,
}

export type UserId = string & { [__brand]: "UserId" };
export type AdminId = string & { [__brand]: "AdminId" };

export type UserName = string & { [__brand]: "UserName" };

export class Password {
    constructor(private readonly plain: string, invalidErr: InvalidError) { checkPassword(plain, invalidErr); }
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

export type Size = number & { [__brand]: "Size" };

import * as invalidTime from "./api/invalid-time";
export function newTime(utc: number, invalidErr: InvalidError): Time {
    if (!Number.isSafeInteger(utc) || utc < 0) {
        throw invalidErr(invalidTime.invalid, utc);
    }
    return { utc } as Time;
}

import * as invalidUserName from "./api/invalid-user-name";
export function checkUserName(value: string, invalidErr: InvalidError): asserts value is UserName{
    if (value.length == 0) {
        throw invalidErr(invalidUserName.empty, value);
    }
    if (value.length < 3) {
        throw invalidErr(invalidUserName.tooShort, value);
    }
    if (value.length > 20) {
        throw invalidErr(invalidUserName.tooLong, value);
    }
    if (!/^[a-zA-Z0-9_-]*$/.test(value)) {
        throw invalidErr(invalidUserName.containsIllegalCharacter, value);
    }
}

import * as invalidTitle from "./api/invalid-title";
export function checkTitle(value: string, invalidErr: InvalidError): asserts value is Title {
    if (value.length == 0) {
        throw invalidErr(invalidTitle.empty, value);
    }
    if (value.length < 3) {
        throw invalidErr(invalidTitle.tooShort, value);
    }
    if (value.length > 171) {
        throw invalidErr(invalidTitle.tooLong, value);
    }
}

import * as invalidTextPostContent from "./api/invalid-text-post-content";
export function checkTextPostContent(value: string, invalidErr: InvalidError): asserts value is TextPostContent {
    if (value.length == 0) {
        throw invalidErr(invalidTextPostContent.empty, value);
    }
    if (value.length > 65535) {
        throw invalidErr(invalidTextPostContent.tooLong, value);
    }
}

import * as invalidUrlPostContent from "./api/invalid-url-post-content";
export function checkUrlPostContent(value: string, invalidErr: InvalidError): asserts value is UrlPostContent {
    if (value.length == 0) {
        throw invalidErr(invalidUrlPostContent.empty, value);
    }
    if (value.length > 65535) {
        throw invalidErr(invalidUrlPostContent.tooLong, value);
    }
    try {
        new URL(value);
    } catch (e) {
        if (e instanceof TypeError) {
            throw invalidErr(invalidUrlPostContent.invalid, value);
        }
        throw e;
    }
}

import * as invalidPassword from "./api/invalid-password";

const hidden = "<hidden>";
export function checkPassword(plain: string, invalidErr: InvalidError) {
    if (plain.length == 0) {
        throw invalidErr(invalidPassword.empty, hidden);
    }
    if (plain.length < 5) {
        throw invalidErr(invalidPassword.tooShort, hidden);
    }
    if (plain.length > 72) { // Limitation of bcrypt
        throw invalidErr(invalidPassword.tooLong, hidden);
    }
}

const DEFAULT_SIZE = 20;
const MAX_SIZE = 500;

import * as invalidSize from "./api/invalid-size";
import { PasswordEncryptor, PasswordVerifier } from "./utils/password";
export function checkSize(value: number | undefined, invalidErr: InvalidError): Size {
    if (value === undefined) {
        return DEFAULT_SIZE as Size;
    } else if (!(value > 0) || !Number.isInteger(value)) {
        throw invalidErr(invalidSize.invalid, value);
    } else if (value > MAX_SIZE) {
        return MAX_SIZE as Size;
    }
    return value as Size;
}
