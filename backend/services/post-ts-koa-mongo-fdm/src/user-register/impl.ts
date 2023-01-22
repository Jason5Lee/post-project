import { UserName, Password, UserId } from "../common";
import { Deps, now } from "../common/utils";
import { Workflow } from ".";
import * as db from "../common/utils/db";
import { Long, MongoError } from "mongodb";
import { errors } from "./api";

export class WorkflowImpl extends Workflow {
    async createUser(userName: UserName, password: Password): Promise<UserId> {
        try {
            const rec = await this.deps.mongoDb.collection(db.users).insertOne({
                name: userName,
                encryptedPassword: await password.encrypt(this.deps.encryptor),
                creationTime: Long.fromNumber(now().utc),
            } satisfies {
                name: string,
                encryptedPassword: string,
                creationTime: Long,
            });
            return db.formatId(rec.insertedId) as UserId;
        } catch (e) {
            if (e instanceof MongoError) {
                if (e.code === 11000) {
                    throw this.errors.userNameAlreadyExists();
                }
            }
            throw e;
        }
    }

    readonly errors = errors;
    constructor(private readonly deps: Deps) { super(); }
}
