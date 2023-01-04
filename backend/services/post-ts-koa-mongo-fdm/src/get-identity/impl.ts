import { Deps } from "../common/utils";
import { Workflow } from ".";
import { checkUserName, UserId, UserName } from "../common";
import * as db from "../common/utils/db";
import { WithId } from "mongodb";
import { fromDB } from "../common/utils/error";
import * as runtypes from "runtypes";

export class WorkflowImpl extends Workflow {
    private static readonly userHasName = runtypes.Record({
        name: runtypes.String,
    });
    async getUserName(id: UserId): Promise<UserName> {
        const user: WithId<unknown> | null = await this.deps.mongoDb.collection(db.users).findOne({ _id: id });
        if (user === null) {
            throw new Error("Caller user not found, ID Hex: " + id.toHexString());
        }
        db.validate(WorkflowImpl.userHasName, db.users, user);
        checkUserName(user.name, fromDB({ collection: db.users, id, field: "name" }));
        return user.name;
    }

    constructor(private deps: Deps) { super(); }
}
