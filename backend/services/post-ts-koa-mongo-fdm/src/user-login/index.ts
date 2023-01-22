import { PasswordVerifier } from "../common/utils/password";
import { Password, UserId, UserName } from "../common";

export interface Query {
    readonly userName: UserName,
    readonly password: Password,
}

export abstract class Workflow {
    async run(input: Query): Promise<UserId> {
        const [id, verifier] = await this.getUserIdAndPasswordVerifier(input.userName);
        if (!(await input.password.verify(verifier))) {
            throw this.errors.userNameOrPasswordIncorrect();
        }
        return id;
    }

    abstract getUserIdAndPasswordVerifier(userName: UserName): Promise<[UserId, PasswordVerifier]>;

    abstract readonly errors: {
        userNameOrPasswordIncorrect(): Error;
    }
}
