import { PostId, Page, PageSize, Time, Title, UserId, UserName } from "../common";

export interface Query {
    readonly creator?: UserId | undefined,
    readonly page: Page,
    readonly pageSize: PageSize,
}

export interface Output {
    readonly total: number,
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
