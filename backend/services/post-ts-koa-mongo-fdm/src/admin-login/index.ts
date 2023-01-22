import { PasswordVerifier } from "../common/utils/password";
import { AdminId, Password } from "../common";

export interface Query {
    readonly id: AdminId,
    readonly password: Password,
}

export abstract class Workflow {
    async run(input: Query): Promise<AdminId> {
        const verifier = await this.getPasswordVerifier(input.id);
        if (!(await input.password.verify(verifier))) {
            throw this.errors.idOrPasswordIncorrect();
        }
        return input.id;
    }

    abstract getPasswordVerifier(id: AdminId): Promise<PasswordVerifier>;

    abstract readonly errors: {
        idOrPasswordIncorrect(): Error;
    }
}
