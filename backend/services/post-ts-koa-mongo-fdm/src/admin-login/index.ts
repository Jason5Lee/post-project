import { AdminId, Password } from "../common";

export interface Query {
    readonly id: AdminId,
    readonly password: Password,
}

export abstract class Workflow {
    async run(input: Query): Promise<AdminId> {
        const verifier = await this.getPasswordVerifier(input.id);
        if (!(await verifier(input.password))) {
            throw this.errors.idOrPasswordIncorrect();
        }
        return input.id;
    }

    abstract getPasswordVerifier(id: AdminId): Promise<(password: Password) => Promise<boolean>>;

    abstract readonly errors: {
        idOrPasswordIncorrect(): Error;
    }
}
