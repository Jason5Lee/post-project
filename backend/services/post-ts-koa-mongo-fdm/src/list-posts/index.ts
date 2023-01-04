import { PostId, Size, Time, Title, UserId, UserName } from "../common";

export type Condition = {
    readonly type: "Before",
    readonly time: Time,
} | {
    readonly type: "After",
    readonly time: Time,
}

export interface Query {
    readonly creator?: UserId | undefined,
    readonly condition?: Condition | undefined,
    readonly size: Size,
}

export interface Output {
    readonly posts: ReadonlyArray<{
        readonly id: PostId,
        readonly title: Title,
        readonly creator: {
            readonly id: UserId,
            readonly name: UserName,
        },
        creationTime: Time,
    }>,
}

export interface Workflow {
    run(query: Query): Promise<Output>;

    readonly errors: {
        creatorNotFound(): Error;
    },
}
