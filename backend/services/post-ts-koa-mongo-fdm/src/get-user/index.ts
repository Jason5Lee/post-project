import { Time, UserId, UserName } from "../common";

export type Query = UserId;

export interface Workflow {
    run(id: Query): Promise<{ userName: UserName, creationTime: Time }>;

    readonly errors: {
        userNotFound(): Error;
    }
}
