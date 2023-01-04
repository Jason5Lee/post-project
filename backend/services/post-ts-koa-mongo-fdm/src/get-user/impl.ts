import { UserId, UserName, Time, checkUserName } from "../common";
import { Deps } from "../common/utils";
import { Workflow } from ".";
import { WithId } from "mongodb";
import * as db from "../common/utils/db";
import { fromDB } from "../common/utils/error";
import { errors } from "./api";
import * as runtypes from "runtypes";

export class WorkflowImpl implements Workflow {
    private static readonly expectedUser = runtypes.Record({
        name: runtypes.String,
        creationTime: runtypes.Number,
    });
    async run(id: UserId): Promise<{ userName: UserName, creationTime: Time }> {
        const user: WithId<unknown> | null = await this.deps.mongoDb.collection(db.users).findOne({ _id: id });
        console.log(JSON.stringify(user));
        if (user === null) {
            throw this.errors.userNotFound();
        }
        db.validate(WorkflowImpl.expectedUser, db.users, user);
        checkUserName(user.name, fromDB({ collection: db.users, id, field: "name" }));
        return {
            userName: user.name,
            creationTime: new Time(user.creationTime, fromDB({ collection: db.users, id, field: "creationTime" })),
        };
    }

    readonly errors = errors;
    constructor(private readonly deps: Deps) { }
}
