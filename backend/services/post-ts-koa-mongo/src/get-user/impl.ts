import { UserId, UserName, Time } from "../common";
import { Deps } from "../common/utils";
import { Workflow } from ".";
import { WithId } from "mongodb";
import * as db from "../common/utils/db";
import { errors } from "./api";
import * as runtypes from "runtypes";

export class WorkflowImpl implements Workflow {
    private static readonly expectedUser = runtypes.Record({
        name: runtypes.String,
        creationTime: runtypes.Number,
    });
    async run(id: UserId): Promise<{ userName: UserName, creationTime: Time }> {
        const oid = db.tryParseId(id);
        if (oid === undefined) {
            throw errors.userNotFound();
        }
        const user: WithId<unknown> | null = await this.deps.mongoDb.collection(db.users).findOne({ _id: oid });
        if (user === null) {
            throw this.errors.userNotFound();
        }
        db.validate(WorkflowImpl.expectedUser, db.users, user);
        return {
            userName: user.name as UserName,
            creationTime: { utc: user.creationTime } as Time,
        };
    }

    readonly errors = errors;
    constructor(private readonly deps: Deps) { }
}
