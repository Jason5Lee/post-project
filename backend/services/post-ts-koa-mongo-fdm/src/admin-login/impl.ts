import { AdminId, Password } from "../common";
import { Workflow } from ".";
import { Deps } from "../common/utils";
import { WithId } from "mongodb";
import * as db from "../common/utils/db";
import { errors } from "./api";
import * as runtypes from "runtypes";

export class WorkflowImpl extends Workflow {
    private static readonly adminHasPassword = runtypes.Record({
        encryptedPassword: runtypes.String,
    });
    async getPasswordVerifier(id: AdminId): Promise<(password: Password) => Promise<boolean>> {
        const rec: WithId<unknown> | null = await this.deps.mongoDb.collection(db.admins).findOne({ _id: id });
        if (rec === null) {
            throw this.errors.idOrPasswordIncorrect();
        }
        db.validate(WorkflowImpl.adminHasPassword, db.admins, rec);

        return this.deps.encryption.getValidator(rec.encryptedPassword);
    }

    readonly errors = errors;
    constructor(private deps: Deps) { super(); }
}
