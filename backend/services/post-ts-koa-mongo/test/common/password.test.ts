import assert from "assert";
import { Password } from "../../src/common";
import { PasswordVerifier } from "../../src/common/utils/password";

describe("Password validation", () => {
    it("should fail if it is empty", async () => {
        assert.equal(Password.new(""), undefined);
    });

    it("should fail if it is too short", async () => {
        assert.equal(Password.new("a"), undefined);
    });

    it("should fail if it is too long", async () => {
        assert.equal(Password.new("a".repeat(73)), undefined);
    });

    it("should create a password if it is proper", async () => {
        class MockVerifier implements PasswordVerifier {
            verify(plain: string): Promise<boolean> {
                return Promise.resolve(plain === "iF@35p");
            }
        }
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        assert.equal(await Password.new("iF@35p")!.verify(new MockVerifier()), true);
    });
});
