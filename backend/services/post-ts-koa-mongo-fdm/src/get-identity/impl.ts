import { Deps } from "../common/utils";
import { Workflow } from ".";
import { UserId, UserName } from "../common";
import * as db from "../common/utils/db";
import { WithId } from "mongodb";
import * as runtypes from "runtypes";
import { invalidAuth } from "../common/api/auth";

export class WorkflowImpl extends Workflow {
    private static readonly userHasName = runtypes.Record({
        name: runtypes.String,
    });
    async getUserName(id: UserId): Promise<UserName> {
        const oid = db.tryParseId(id);
        if (oid === undefined) {
            throw invalidAuth();
        }
        const user: WithId<unknown> | null = await this.deps.mongoDb.collection(db.users).findOne({ _id: oid });
        if (user === null) {
            throw invalidAuth();
        }
        db.validate(WorkflowImpl.userHasName, db.users, user);
        return user.name as UserName;
    }

    constructor(private deps: Deps) { super(); }
}
