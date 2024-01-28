import { Identity, PostId, UserId } from "../common";

export type Command = PostId;

export abstract class Workflow {
    async run(caller: Identity, input: Command): Promise<void> {
        const auth = caller.type === "Admin" || (caller.id satisfies UserId) === (await this.getPostCreator(input));
        if (!auth) {
            throw this.errors.notCreatorAdmin();
        }
        await this.deletePost(input);
    }

    abstract getPostCreator(post: PostId): Promise<UserId>;
    abstract deletePost(post: PostId): Promise<void>;

    abstract readonly errors: {
        notCreatorAdmin(): Error;
        postNotFound(): Error;
    }
}