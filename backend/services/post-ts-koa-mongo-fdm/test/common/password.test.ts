import assert from "assert";
import { Password } from "../../src/common";
import { onInvalid } from "../../src/common/utils/error";
import * as invalidPassword from "../../src/common/api/invalid-password";
import { ExpectedError, expectInvalid } from "../common";
import { PasswordVerifier } from "../../src/common/utils/password";

describe("Password validation", () => {
    it("should fail if it is empty", async () => {
        try {
            new Password("", expectInvalid);
            throw new Error("Expected error not thrown");
        } catch (e) {
            if (!(e instanceof ExpectedError)) {
                throw e;
            }
            assert.deepEqual(e.err, invalidPassword.empty);
        }
    });

    it("should fail if it is too short", async () => {
        try {
            new Password("a", expectInvalid);
            throw new Error("Expected error not thrown");
        } catch (e) {
            if (!(e instanceof ExpectedError)) {
                throw e;
            }
            assert.deepEqual(e.err, invalidPassword.tooShort);
        }
    });

    it("should fail if it is too long", async () => {
        try {
            new Password("a".repeat(73), expectInvalid);
            throw new Error("Expected error not thrown");
        } catch (e) {
            if (!(e instanceof ExpectedError)) {
                throw e;
            }
            assert.deepEqual(e.err, invalidPassword.tooLong);
        }
    });

    it("should create a password if it is proper", async () => {
        const password = new Password("iF@35p", onInvalid(() => new Error("should not be an error")));
        class MockVerifier implements PasswordVerifier {
            verify(plain: string): Promise<boolean> {
                return Promise.resolve(plain === "iF@35p");
            }
        }
        assert.equal(await password.verify(new MockVerifier()), true);
    });
});
