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
        const post: WithId<unknown> | null = await this.deps.mongoDb.collection(db.posts).findOne({ _id: postId });
        if (post === null) {
            throw this.errors.postNotFound();
        }
        db.validate(WorkflowImpl.postHasCreator, db.posts, post);
        return post.creator as UserId;
    }
    async deletePost(post: PostId): Promise<void> {
        await this.deps.mongoDb.collection(db.posts).deleteOne({ _id: post });
    }

    constructor(private deps: Deps) { super(); }
    readonly errors = errors;
}
