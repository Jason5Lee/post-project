import { UserName, UserId, Password } from "../common";
import { Deps } from "../common/utils";
import { Workflow } from ".";
import { WithId } from "mongodb";
import * as db from "../common/utils/db";
import { errors } from "./api";
import * as runtypes from "runtypes";

export class WorkflowImpl extends Workflow {
    private static readonly userHasPassword = runtypes.Record({
        encryptedPassword: runtypes.String,
    });
    async getUserIdAndPasswordVerifier(userName: UserName): Promise<[UserId, (password: Password) => Promise<boolean>]> {
        const rec: WithId<unknown> | null = await this.deps.mongoDb.collection(db.users).findOne({ name: userName } satisfies { name: string });
        if (rec === null) {
            throw this.errors.userNameOrPasswordIncorrect();
        }
        db.validate(WorkflowImpl.userHasPassword, db.users, rec);
        return [rec._id as UserId, this.deps.encryption.getValidator(rec.encryptedPassword)];
    }

    readonly errors = errors;
    constructor(private readonly deps: Deps) { super(); }
}
