import { UserName, UserId } from "../common";
import { Deps } from "../common/utils";
import { Workflow } from ".";
import { WithId } from "mongodb";
import * as db from "../common/utils/db";
import { errors } from "./api";
import * as runtypes from "runtypes";
import { PasswordVerifier } from "../common/utils/password";

export class WorkflowImpl extends Workflow {
    private static readonly userHasPassword = runtypes.Record({
        encryptedPassword: runtypes.String,
    });
    async getUserIdAndPasswordVerifier(userName: UserName): Promise<[UserId, PasswordVerifier]> {
        const rec: WithId<unknown> | null = await this.deps.mongoDb.collection(db.users).findOne({ name: userName } satisfies { name: string });
        if (rec === null) {
            throw this.errors.userNameOrPasswordIncorrect();
        }
        db.validate(WorkflowImpl.userHasPassword, db.users, rec);
        return [db.formatId(rec._id) as UserId, new db.BCryptVerifier(rec.encryptedPassword)];
    }

    readonly errors = errors;
    constructor(private readonly deps: Deps) { super(); }
}
