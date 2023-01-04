import { invalidRequest } from "./api";
import { Validation } from "./utils/error";
import { ObjectId } from "mongodb";

export type Id = ObjectId;

declare const __brand: unique symbol;

export class Time {
    constructor(public readonly utc: number, transform: Validation) {
        checkTime(utc, transform);
    }
}

export type Identity = {
    readonly type: "User",
    readonly id: UserId,
} | {
    readonly type: "Admin",
    readonly id: AdminId,
}

export type UserId = Id & { [__brand]: "UserId" };
export type AdminId = Id & { [__brand]: "AdminId" };

export type UserName = string & { [__brand]: "UserName" };
export interface User {
    readonly id: UserId,
    readonly name: UserName,
    readonly creation: Time,
}

export class Password {
    constructor(readonly plain: string) { checkPassword(plain); }
}

export type LengthLimit = number & { [__brand]: "LengthLimit" };
export type PostId = Id & { [__brand]: "PostId" };
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
export function checkTime(utc: number, transform: Validation) {
    if (utc < 0) {
        throw transform(invalidTime.negative, utc);
    }
    if (!Number.isSafeInteger(utc)) {
        throw transform(invalidTime.notSafeInteger, utc);
    }
}

import * as invalidUserName from "./api/invalid-user-name";
export function checkUserName(value: string, transform: Validation): asserts value is UserName{
    if (value.length == 0) {
        throw transform(invalidUserName.empty, value);
    }
    if (value.length < 3) {
        throw transform(invalidUserName.tooShort, value);
    }
    if (value.length > 20) {
        throw transform(invalidUserName.tooLong, value);
    }
    if (!/^[a-zA-Z0-9_-]*$/.test(value)) {
        throw transform(invalidUserName.containsIllegalCharacter, value);
    }
}

import * as invalidTitle from "./api/invalid-title";
export function checkTitle(value: string, transform: Validation): asserts value is Title {
    if (value.length == 0) {
        throw transform(invalidTitle.empty, value);
    }
    if (value.length < 3) {
        throw transform(invalidTitle.tooShort, value);
    }
    if (value.length > 171) {
        throw transform(invalidTitle.tooLong, value);
    }
}

import * as invalidTextPostContent from "./api/invalid-text-post-content";
export function checkTextPostContent(value: string, transform: Validation): asserts value is TextPostContent {
    if (value.length == 0) {
        throw transform(invalidTextPostContent.empty, value);
    }
    if (value.length > 65535) {
        throw transform(invalidTextPostContent.tooLong, value);
    }
}

import * as invalidUrlPostContent from "./api/invalid-url-post-content";
export function checkUrlPostContent(value: string, transform: Validation): asserts value is UrlPostContent {
    if (value.length == 0) {
        throw transform(invalidUrlPostContent.empty, value);
    }
    if (value.length > 65535) {
        throw transform(invalidUrlPostContent.tooLong, value);
    }
    try {
        new URL(value);
    } catch (e) {
        if (e instanceof TypeError) {
            throw transform(invalidUrlPostContent.invalid, value);
        }
        throw e;
    }
}

import * as invalidPassword from "./api/invalid-password";

export function checkPassword(plain: string) {
    if (plain.length == 0) {
        throw invalidRequest(invalidPassword.empty);
    }
    if (plain.length < 5) {
        throw invalidRequest(invalidPassword.tooShort);
    }
    if (plain.length > 72) { // Limitation of bcrypt
        throw invalidRequest(invalidPassword.tooLong);
    }
}

const DEFAULT_SIZE = 20;
const MAX_SIZE = 500;

import * as invalidSize from "./api/invalid-size";
export function checkSize(value: number | undefined): Size {
    if (value === undefined) {
        return DEFAULT_SIZE as Size;
    } else if (!(value > 0)) {
        throw invalidRequest(invalidSize.nonPositive);
    } else if (value > MAX_SIZE) {
        return MAX_SIZE as Size;
    }
    return value as Size;
}
