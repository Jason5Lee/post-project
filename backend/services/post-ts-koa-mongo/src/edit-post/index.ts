import { PostContent, PostId, UserId } from "../common";

export interface Command {
    id: PostId,
    newContent: PostContent,
}

export abstract class Workflow {
    async run(caller: UserId, input: Command): Promise<void> {
        await this.checkUserIsCreatorAndContentHasTheSameType(input.id, caller, input.newContent);
        await this.updatePost(input.id, input.newContent);
    }

    abstract checkUserIsCreatorAndContentHasTheSameType(postId: PostId, userId: UserId, content: PostContent): Promise<void>;
    abstract updatePost(postId: PostId, newContent: PostContent): Promise<void>;

    abstract readonly errors: {
        postNotFound(): Error;
        notCreator(): Error;
        typeDiff(): Error;
    }
}
