import { checkTextPostContent, checkUrlPostContent, checkTitle, checkUserName, PostContent, PostId, UserId, Time } from "../common";
import { Deps } from "../common/utils";
import { PostInfoForPage, Workflow } from ".";
import * as db from "../common/utils/db";
import { ObjectId, WithId } from "mongodb";
import { fromDB } from "../common/utils/error";
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
    async run(id: PostId): Promise<PostInfoForPage> {
        const post: WithId<unknown> | null = await this.deps.mongoDb.collection(db.posts).findOne({ _id: id });
        if (post === null) {
            throw this.errors.postNotFound();
        }
        db.validate(WorkflowImpl.expectedPost, db.posts, post);
        checkTitle(post.title, fromDB({ collection: db.posts, id, field: "title" }));
        let content: PostContent;
        if (post.text !== undefined && post.url === undefined) {
            checkTextPostContent(post.text, fromDB({ collection: db.posts, id, field: "text" }));
            content = { type: "Text", content: post.text };
        } else if (post.text === undefined && post.url !== undefined) {
            checkUrlPostContent(post.url, fromDB({ collection: db.posts, id, field: "url" }));
            content = { type: "Url", content: post.url };
        } else {
            throw fromDB({ collection: db.posts, id, field: "text,url" })({
                error: {
                    error: "TEXT_URL_EXACT_ONE",
                    reason: "exact one of the text and the url field should exist",
                    message: "",
                }
            });
        }

        const creator: WithId<unknown> | null = await this.deps.mongoDb.collection(db.users).findOne({ _id: post.creator });
        if (creator === null) {
            throw new Error(`The creator of post ${id}, ${post.creator}, not found`);
        }
        db.validate(WorkflowImpl.userHasName, db.users, creator);
        checkUserName(creator.name, fromDB({ collection: db.users, id: creator._id, field: "name" }));
        return {
            creator: {
                id: creator._id as UserId,
                name: creator.name,
            },
            title: post.title,
            content,
            creationTime: new Time(post.creationTime, fromDB({ collection: db.posts, id, field: "creationTime" })),
            lastModified: post.lastModified !== undefined ? new Time(post.lastModified, fromDB({ collection: db.posts, id, field: "lastModified" })) : undefined,
        };
    }

    readonly errors = errors;
    constructor(private readonly deps: Deps) { }
}
