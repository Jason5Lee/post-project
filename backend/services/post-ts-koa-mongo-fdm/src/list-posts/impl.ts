import { Deps } from "../common/utils";
import { Output, Query, Workflow } from ".";
import * as db from "../common/utils/db";
import { Long, WithId, ObjectId } from "mongodb";
import { onInvalidHandleInDB, throwUnexpectedValue } from "../common/utils/error";
import { checkTitle, checkUserName, newTime, PostId, UserId } from "../common";
import { errors } from "./api";
import * as runtypes from "runtypes";

export class WorkflowImpl implements Workflow {
    private static readonly expectedPost = runtypes.Record({
        title: runtypes.String,
        creator: runtypes.InstanceOf(ObjectId),
        creationTime: runtypes.Number,
    });
    private static readonly userHasName = runtypes.Record({
        name: runtypes.String,
    });
    async run(query: Query): Promise<Output> {
        let filter: object = {};
        if (query.creator !== undefined) {
            const creatorOid = db.tryParseId(query.creator);
            if (creatorOid === undefined) {
                throw this.errors.creatorNotFound();
            }
            const user = await this.deps.mongoDb.collection(db.users).findOne({ _id: creatorOid });
            if (user === null) {
                throw this.errors.creatorNotFound();
            }
            filter = { creator: creatorOid };
        }
        let timeSort: 1 | -1 = -1;
        if (query.condition !== undefined) {
            if (query.condition.type === "Before") {
                filter = { ...filter, creationTime: { $lt: Long.fromNumber(query.condition.time.utc) } };
            } else if (query.condition.type === "After") {
                filter = { ...filter, creationTime: { $gt: Long.fromNumber(query.condition.time.utc) } };
                timeSort = 1;
            } else {
                throwUnexpectedValue(query.condition);
            }
        }

        const posts: WithId<unknown>[] = await this.deps.mongoDb.collection(db.posts).find(filter).sort({ creationTime: timeSort }).limit(query.size).toArray();
        if (posts.length === 0) {
            return { posts: [] };
        }
        db.validateArray(WorkflowImpl.expectedPost, db.posts, posts);
        if (timeSort === 1) {
            posts.reverse();
        }
        const creatorIds = posts.map(post => post.creator);
        const creators: WithId<unknown>[] = await this.deps.mongoDb.collection(db.users).find({ _id: { $in: creatorIds } }).toArray();
        db.validateArray(WorkflowImpl.userHasName, db.users, creators);
        const creatorMap = new Map(creators.map(creator => [creator._id.toHexString(), creator]));
        return {
            posts: posts.map(post => {
                const creator = creatorMap.get(post.creator.toHexString());
                if (creator === undefined) {
                    throw new Error(`The creator of post ${post._id}, ${post.creator}, not found`);
                }
                checkTitle(post.title, onInvalidHandleInDB({ collection: db.posts, id: post._id, field: "title" }));
                checkUserName(creator.name, onInvalidHandleInDB({ collection: db.users, id: creator._id, field: "name" }));
                return {
                    id: db.formatId(post._id) as PostId,
                    title: post.title,
                    creator: {
                        id: db.formatId(post.creator) as UserId,
                        name: creator.name,
                    },
                    creationTime: newTime(post.creationTime, onInvalidHandleInDB({ collection: db.posts, id: post._id, field: "creationTime" })),
                };
            })
        };
    }

    readonly errors = errors;
    constructor(private readonly deps: Deps) { }
}