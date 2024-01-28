import { PostId, UserId } from "../common";
import { Deps } from "../common/utils";
import { Workflow } from ".";
import { ObjectId, WithId } from "mongodb";
import * as db from "../common/utils/db";
import * as runtypes from "runtypes";
import { errors } from "./api";

export class WorkflowImpl extends Workflow {
    private static readonly postHasCreator = runtypes.Record({
        creator: runtypes.InstanceOf(ObjectId),
    });
    async getPostCreator(postId: PostId): Promise<UserId> {
        const oid = db.tryParseId(postId);
        if (oid === undefined) {
            throw errors.postNotFound();
        }
        const post: WithId<unknown> | null = await this.deps.mongoDb.collection(db.posts).findOne({ _id: oid });
        if (post === null) {
            throw this.errors.postNotFound();
        }
        db.validate(WorkflowImpl.postHasCreator, db.posts, post);
        return db.formatId(post.creator) as UserId;
    }
    async deletePost(postId: PostId): Promise<void> {
        const oid = db.tryParseId(postId);
        if (oid === undefined) {
            throw errors.postNotFound();
        }
        await this.deps.mongoDb.collection(db.posts).deleteOne({ _id: oid });
    }

    constructor(private deps: Deps) { super(); }
    readonly errors = errors;
}
