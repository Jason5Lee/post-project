import { AdminId, PostId, UserId } from "../../src/common";
import { Workflow } from "../../src/delete-post";
import { ExpectedError, id1, id2 } from "../common";

describe("Delete post workflow", () => {
    it("should fail if the caller is a user but not the creator", async () => {
        class MockWorkflow extends Workflow {
            getPostCreator(): Promise<UserId> {
                return Promise.resolve(id1 as UserId);
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
            await workflow.run({ type: "User", id: id2 as UserId }, id1 as PostId);
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
                return Promise.resolve(id1 as UserId);
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
        await workflow.run({ type: "User", id: id1 as UserId }, id1 as PostId);
    });

    it("should success if the caller is the admin", async () => {
        class MockWorkflow extends Workflow {
            getPostCreator(): Promise<UserId> {
                return Promise.resolve(id1 as UserId);
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
        await workflow.run({ type: "Admin", id: id2 as AdminId }, id1 as PostId);
    });
});
