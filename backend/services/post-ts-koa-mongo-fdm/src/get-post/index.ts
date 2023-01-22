import { PostContent, PostId, Time, Title, UserId, UserName } from "../common";

export interface Post {
    readonly creator: {
        readonly name: UserName,
        readonly id: UserId,
    },
    readonly creationTime: Time,
    readonly lastModified?: Time | undefined,
    readonly title: Title,
    readonly content: PostContent,
}

export interface Workflow {
    run(id: PostId): Promise<Post>;

    readonly errors: {
        postNotFound(): Error;
    }
}
