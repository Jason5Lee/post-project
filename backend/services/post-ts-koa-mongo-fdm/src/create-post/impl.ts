import { Deps, now } from "../common/utils";
import { Command, Workflow } from ".";
import { PostId, UserId } from "../common";
import * as db from "../common/utils/db";
import { Long, MongoError, ObjectId } from "mongodb";
import { errors } from "./api";

export class WorkflowImpl implements Workflow {
    async run(caller: UserId, input: Command): Promise<PostId> {
        let contentObj: { text?: string, url?: string };
        switch (input.content.type) {
            case "Text":
                contentObj = { text: input.content.content };
                break;
            case "Url":
                contentObj = { url: input.content.content };
                break;
        }
        try {
            const post = await this.deps.mongoDb.collection(db.posts).insertOne({
                creator: caller,
                creationTime: Long.fromNumber(now().utc),
                title: input.title,
                ...contentObj,
            } satisfies {
                creator: ObjectId,
                creationTime: Long,
                title: string,
                text?: string,
                url?: string,
            });
            return post.insertedId as PostId;
        } catch (e) {
            if (e instanceof MongoError) {
                if (e.code === 11000) {
                    throw this.errors.duplicateTitle();
                }
            }
            throw e;
        }
    }

    readonly errors = errors;
    constructor(private deps: Deps) { }
}
