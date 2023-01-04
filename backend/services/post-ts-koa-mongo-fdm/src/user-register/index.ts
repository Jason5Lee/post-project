import { Password, UserId, UserName } from "../common";

export interface Command {
    readonly userName: UserName,
    readonly password: Password,
}

export abstract class Workflow {
    async run(input: Command): Promise<UserId> {
        return await this.createUser(input.userName, input.password);
    }

    abstract createUser(userName: UserName, password: Password): Promise<UserId>;

    abstract readonly errors: {
        userNameAlreadyExists(): Error;
    }
}
