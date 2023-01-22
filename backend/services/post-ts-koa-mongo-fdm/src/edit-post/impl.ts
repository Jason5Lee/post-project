import { Deps, now } from "../common/utils";
import { Workflow } from ".";
import { PostId, UserId, PostContent } from "../common";
import * as db from "../common/utils/db";
import { Long, ObjectId, WithId } from "mongodb";
import { throwUnexpectedValue } from "../common/utils/error";
import { errors } from "./api";
import * as runtypes from "runtypes";

export class WorkflowImpl extends Workflow {
    private static readonly postHasCreator_TextOrUrl = runtypes.Record({
        creator: runtypes.InstanceOf(ObjectId),
        text: runtypes.String.optional(),
        url: runtypes.String.optional(),
    });

    async checkUserIsCreatorAndContentHasTheSameType(postId: PostId, userId: UserId, content: PostContent): Promise<void> {
        const postOid = db.tryParseId(postId);
        if (postOid === undefined) {
            throw this.errors.postNotFound();
        }
        const post: WithId<unknown> | null = await this.deps.mongoDb.collection(db.posts).findOne({ _id: postOid });
        if (post === null) {
            throw this.errors.postNotFound();
        }
        db.validate(WorkflowImpl.postHasCreator_TextOrUrl, db.posts, post);
        
        const userOid = db.tryParseId(userId);
        if (userOid === undefined) {
            throw this.errors.notCreator();
        }
        if (!post.creator.equals(userOid)) {
            throw this.errors.notCreator();
        }
        const sameType =
            content.type === "Text" ? post.text !== undefined :
                content.type === "Url" ? post.url !== undefined :
                    throwUnexpectedValue(content);
        if (!sameType) {
            throw this.errors.typeDiff();
        }
    }

    async updatePost(postId: PostId, newContent: PostContent): Promise<void> {
        const postOid = db.tryParseId(postId);
        if (postOid === undefined) {
            throw this.errors.postNotFound();
        }

        const lastModified = Long.fromNumber(now().utc);
        const updateSet: {
            lastModified: Long,
        } & ({
            text: string
        } | {
            url: string
        }) = newContent.type === "Text" ? { lastModified, text: newContent.content } :
                newContent.type === "Url" ? { lastModified, url: newContent.content } :
                    throwUnexpectedValue(newContent);
        await this.deps.mongoDb.collection(db.posts).updateOne({ _id: postOid }, { $set: updateSet });
    }

    readonly errors = errors;
    constructor(private deps: Deps) { super(); }
}
