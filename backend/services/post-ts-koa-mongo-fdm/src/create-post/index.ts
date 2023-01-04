import { PostContent, PostId, Title, UserId } from "../common";

export interface Command {
    readonly title: Title,
    readonly content: PostContent,
}

export interface Workflow {
    run(caller: UserId, input: Command): Promise<PostId>;

    readonly errors: {
        duplicateTitle(): Error;
    }
}
