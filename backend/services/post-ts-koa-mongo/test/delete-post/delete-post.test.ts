import { PostId, UserId } from "../../src/common";
import { Workflow } from "../../src/delete-post";
import { ExpectedError } from "../common";

describe("Delete post workflow", () => {
    it("should fail if the caller is a user but not the creator", async () => {
        class MockWorkflow extends Workflow {
            getPostCreator(): Promise<UserId> {
                return Promise.resolve("0" as UserId);
            }
            deletePost(): Promise<void> {
                throw new Error("Post should not be deleted by the user who is not the creator");
            }
            errors = {
                notCreatorAdmin: () => new ExpectedError(),
                postNotFound: () => new Error("postNotFound should not be called"),
            };
        }
        const workflow = new MockWorkflow();
        try {
            await workflow.run({ type: "User", id: "1" as UserId }, "0" as PostId);
            throw new Error("Expected error not thrown");
        } catch (e) {
            if (!(e instanceof ExpectedError)) {
                throw e;
            }
        }
    });

    it("should success if the caller is the creator", async () => {
        class MockWorkflow extends Workflow {
            getPostCreator(): Promise<UserId> {
                return Promise.resolve("1" as UserId);
            }
            deletePost(): Promise<void> {
                return Promise.resolve();
            }
            errors = {
                notCreatorAdmin: () => new Error("notCreatorAdmin should not be called"),
                postNotFound: () => new Error("postNotFound should not be called"),
            };
        }
        const workflow = new MockWorkflow();
        await workflow.run({ type: "User", id: "1" as UserId }, "2" as PostId);
    });

    it("should success if the caller is the admin", async () => {
        class MockWorkflow extends Workflow {
            getPostCreator(): Promise<UserId> {
                return Promise.resolve("0" as UserId);
            }
            deletePost(): Promise<void> {
                return Promise.resolve();
            }
            errors = {
                notCreatorAdmin: () => new Error("notCreatorAdmin should not be called"),
                postNotFound: () => new Error("postNotFound should not be called"),
            };
        }
        const workflow = new MockWorkflow();
        await workflow.run({ type: "Admin" }, "2" as PostId);
    });
});
