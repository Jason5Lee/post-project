import assert from "assert";
import { Password } from "../../src/common";
import { ResponseError } from "../../src/common/utils/error";
import * as invalidPassword from "../../src/common/api/invalid-password";

describe("Password validation", () => {
    it("should fail if it is empty", async () => {
        try {
            new Password("");
            throw new Error("Expected error not thrown");
        } catch (e) {
            if (!(e instanceof ResponseError)) {
                throw e;
            }
            assert.deepEqual(e.body, invalidPassword.empty);
        }
    });

    it("should fail if it is too short", async () => {
        try {
            new Password("a");
            throw new Error("Expected error not thrown");
        } catch (e) {
            if (!(e instanceof ResponseError)) {
                throw e;
            }
            assert.deepEqual(e.body, invalidPassword.tooShort);
        }
    });

    it("should fail if it is too long", async () => {
        try {
            new Password("a".repeat(73));
            throw new Error("Expected error not thrown");
        } catch (e) {
            if (!(e instanceof ResponseError)) {
                throw e;
            }
            assert.deepEqual(e.body, invalidPassword.tooLong);
        }
    });

    it("should create a password if it is proper", async () => {
        const password = new Password("iF@35p");
        assert.equal(password.plain, "iF@35p");
    });
});
