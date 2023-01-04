import { Password, UserId, UserName } from "../common";

export interface Query {
    readonly userName: UserName,
    readonly password: Password,
}

export abstract class Workflow {
    async run(input: Query): Promise<UserId> {
        const [id, passwordVerifier] = await this.getUserIdAndPasswordVerifier(input.userName);
        if (!(await passwordVerifier(input.password))) {
            throw this.errors.userNameOrPasswordIncorrect();
        }
        return id;
    }

    abstract getUserIdAndPasswordVerifier(userName: UserName): Promise<[UserId, (password: Password) => Promise<boolean>]>;

    abstract readonly errors: {
        userNameOrPasswordIncorrect(): Error;
    }
}
