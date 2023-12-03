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
}

export type UserId = string & { [__brand]: "UserId" };

export type UserName = string & { [__brand]: "UserName" };

import { PasswordEncryptor, PasswordVerifier } from "./utils/password";
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

export type Page = number & { [__brand]: "Page" };
export type PageSize = number & { [__brand]: "PageSize" };

import * as invalidTime from "./api/invalid-time";
export function newTime(utc: number, invalidErr: InvalidError): Time {
    if (!Number.isSafeInteger(utc) || utc < 0) {
        throw invalidErr(invalidTime.invalid);
    }
    return { utc } as Time;
}

import * as invalidUserName from "./api/invalid-user-name";
export function checkUserName(value: string, invalidErr: InvalidError): asserts value is UserName{
    if (value.length == 0) {
        throw invalidErr(invalidUserName.empty);
    }
    if (value.length < 3) {
        throw invalidErr(invalidUserName.tooShort);
    }
    if (value.length > 20) {
        throw invalidErr(invalidUserName.tooLong);
    }
    if (!/^[a-zA-Z0-9_-]*$/.test(value)) {
        throw invalidErr(invalidUserName.containsIllegalCharacter);
    }
}

import * as invalidTitle from "./api/invalid-title";
export function checkTitle(value: string, invalidErr: InvalidError): asserts value is Title {
    if (value.length == 0) {
        throw invalidErr(invalidTitle.empty);
    }
    if (value.length < 3) {
        throw invalidErr(invalidTitle.tooShort);
    }
    if (value.length > 171) {
        throw invalidErr(invalidTitle.tooLong);
    }
}

import * as invalidTextPostContent from "./api/invalid-text-post-content";
export function checkTextPostContent(value: string, invalidErr: InvalidError): asserts value is TextPostContent {
    if (value.length == 0) {
        throw invalidErr(invalidTextPostContent.empty);
    }
    if (value.length > 65535) {
        throw invalidErr(invalidTextPostContent.tooLong);
    }
}

import * as invalidUrlPostContent from "./api/invalid-url-post-content";
export function checkUrlPostContent(value: string, invalidErr: InvalidError): asserts value is UrlPostContent {
    if (value.length == 0) {
        throw invalidErr(invalidUrlPostContent.empty);
    }
    if (value.length > 65535) {
        throw invalidErr(invalidUrlPostContent.tooLong);
    }
    try {
        new URL(value);
    } catch (e) {
        if (e instanceof TypeError) {
            throw invalidErr(invalidUrlPostContent.invalid);
        }
        throw e;
    }
}

import * as invalidPassword from "./api/invalid-password";

export function checkPassword(plain: string, invalidErr: InvalidError) {
    if (plain.length == 0) {
        throw invalidErr(invalidPassword.empty);
    }
    if (plain.length < 5) {
        throw invalidErr(invalidPassword.tooShort);
    }
    if (plain.length > 72) { // Limitation of bcrypt
        throw invalidErr(invalidPassword.tooLong);
    }
}

import * as invalidPage from "./api/invalid-page";
export function checkPage(value: number, invalidErr: InvalidError): asserts value is Page {
    if (!(value > 0) || !Number.isSafeInteger(value)) {
        throw invalidErr(invalidPage.invalidPage);
    }
}

import * as invalidPageSize from "./api/invalid-page-size";
export function checkPageSize(value: number, maximumPageSize: PageSize, invalidErr: InvalidError): asserts value is PageSize {
    if (!(value > 0) || !Number.isSafeInteger(value)) {
        throw invalidErr(invalidPageSize.invalidPageSize);
    }
    if (value > maximumPageSize) {
        throw invalidErr(invalidPageSize.pageSizeTooLarge);
    }
}
