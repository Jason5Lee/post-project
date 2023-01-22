import { checkTextPostContent, checkUrlPostContent, checkTitle, checkUserName, PostContent, PostId, UserId, newTime } from "../common";
import { Deps } from "../common/utils";
import { Post, Workflow } from ".";
import * as db from "../common/utils/db";
import { ObjectId, WithId } from "mongodb";
import { onInvalidHandleInDB } from "../common/utils/error";
import { errors } from "./api";
import * as runtypes from "runtypes";

export class WorkflowImpl implements Workflow {
    private static readonly expectedPost = runtypes.Record({
        creator: runtypes.InstanceOf(ObjectId),
        creationTime: runtypes.Number, // NodeJS will convert the small BSON Long to a number.
        lastModified: runtypes.Number.optional(),
        title: runtypes.String,
        text: runtypes.String.optional(),
        url: runtypes.String.optional(),
    });
    private static readonly userHasName = runtypes.Record({
        name: runtypes.String,
    });
    async run(id: PostId): Promise<Post> {
        const oid = db.tryParseId(id);
        if (oid === undefined) {
            throw errors.postNotFound();
        }
        const post: WithId<unknown> | null = await this.deps.mongoDb.collection(db.posts).findOne({ _id: oid });
        if (post === null) {
            throw this.errors.postNotFound();
        }
        db.validate(WorkflowImpl.expectedPost, db.posts, post);
        checkTitle(post.title, onInvalidHandleInDB({ collection: db.posts, id: oid, field: "title" }));
        let content: PostContent;
        if (post.text !== undefined && post.url === undefined) {
            checkTextPostContent(post.text, onInvalidHandleInDB({ collection: db.posts, id: oid, field: "text" }));
            content = { type: "Text", content: post.text };
        } else if (post.text === undefined && post.url !== undefined) {
            checkUrlPostContent(post.url, onInvalidHandleInDB({ collection: db.posts, id: oid, field: "url" }));
            content = { type: "Url", content: post.url };
        } else {
            throw new Error(`Invalid value in collection \`${db.posts}\`, ID \`${JSON.stringify(id)}\`, exactly only one of \`text\` and \`url\` must exist`);
        }

        const creator: WithId<unknown> | null = await this.deps.mongoDb.collection(db.users).findOne({ _id: post.creator });
        if (creator === null) {
            throw new Error(`The creator of post ${id}, ${post.creator}, not found`);
        }
        db.validate(WorkflowImpl.userHasName, db.users, creator);
        checkUserName(creator.name, onInvalidHandleInDB({ collection: db.users, id: creator._id, field: "name" }));
        return {
            creator: {
                id: db.formatId(creator._id) as UserId,
                name: creator.name,
            },
            title: post.title,
            content,
            creationTime: newTime(post.creationTime, onInvalidHandleInDB({ collection: db.posts, id, field: "creationTime" })),
            lastModified: post.lastModified !== undefined ? newTime(post.lastModified, onInvalidHandleInDB({ collection: db.posts, id, field: "lastModified" })) : undefined,
        };
    }

    readonly errors = errors;
    constructor(private readonly deps: Deps) { }
}
