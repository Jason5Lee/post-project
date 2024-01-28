import { PostContent, PostId, UserId, TextPostContent, UrlPostContent, UserName, Title, Time } from "../common";
import { Deps } from "../common/utils";
import { Post, Workflow } from ".";
import * as db from "../common/utils/db";
import { ObjectId, WithId } from "mongodb";
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
        let content: PostContent;
        if (post.text !== undefined && post.url === undefined) {
            content = { type: "Text", content: post.text as TextPostContent };
        } else if (post.text === undefined && post.url !== undefined) {
            content = { type: "Url", content: post.url as UrlPostContent };
        } else {
            throw new Error(`Invalid value in collection \`${db.posts}\`, ID \`${JSON.stringify(id)}\`, exactly only one of \`text\` and \`url\` must exist`);
        }

        const creator: WithId<unknown> | null = await this.deps.mongoDb.collection(db.users).findOne({ _id: post.creator });
        if (creator === null) {
            throw new Error(`The creator of post ${id}, ${post.creator}, not found`);
        }
        db.validate(WorkflowImpl.userHasName, db.users, creator);
        return {
            creator: {
                id: db.formatId(creator._id) as UserId,
                name: creator.name as UserName,
            },
            title: post.title as Title,
            content,
            creationTime: { utc: post.creationTime } as Time,
            lastModified: post.lastModified !== undefined ? { utc: post.lastModified } as Time : undefined,
        };
    }

    readonly errors = errors;
    constructor(private readonly deps: Deps) { }
}
