import { AdminId } from "../common";
import { Workflow } from ".";
import { Deps } from "../common/utils";
import { WithId } from "mongodb";
import * as db from "../common/utils/db";
import { errors } from "./api";
import * as runtypes from "runtypes";
import { PasswordVerifier } from "../common/utils/password";

export class WorkflowImpl extends Workflow {
    private static readonly adminHasPassword = runtypes.Record({
        encryptedPassword: runtypes.String,
    });
    async getPasswordVerifier(id: AdminId): Promise<PasswordVerifier> {
        const oid = db.tryParseId(id);
        if (oid === undefined) {
            throw errors.idOrPasswordIncorrect();
        }
        const rec: WithId<unknown> | null = await this.deps.mongoDb.collection(db.admins).findOne({ _id: oid });
        if (rec === null) {
            throw this.errors.idOrPasswordIncorrect();
        }
        db.validate(WorkflowImpl.adminHasPassword, db.admins, rec);

        return new db.BCryptVerifier(rec.encryptedPassword);
    }

    readonly errors = errors;
    constructor(private deps: Deps) { super(); }
}
