import { Time, UserId, UserName } from "../common";

export interface Workflow {
    run(id: UserId): Promise<{ userName: UserName, creationTime: Time }>;

    readonly errors: {
        userNotFound(): Error;
    }
}
