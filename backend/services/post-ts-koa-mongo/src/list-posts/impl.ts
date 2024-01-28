import { Deps } from "../common/utils";
import { Output, Query, Workflow } from ".";
import * as db from "../common/utils/db";
import { WithId, ObjectId } from "mongodb";
import { PostId, Time, Title, UserId, UserName } from "../common";
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

        const total = await this.deps.mongoDb
            .collection(db.posts)
            .countDocuments(filter);

        const pagePosts: WithId<unknown>[] = await this.deps.mongoDb
            .collection(db.posts)
            .find(filter)
            .sort({ creationTime: -1, _id: -1 })
            .skip((query.page - 1) * query.pageSize)
            .limit(query.pageSize)
            .toArray();
        if (pagePosts.length === 0) {
            return { total, posts: [] };
        }
        db.validateArray(WorkflowImpl.expectedPost, db.posts, pagePosts);
        const creatorIds = pagePosts.map(post => post.creator);
        const creators: WithId<unknown>[] = await this.deps.mongoDb.collection(db.users).find({ _id: { $in: creatorIds } }).toArray();
        db.validateArray(WorkflowImpl.userHasName, db.users, creators);

        const creatorMap = new Map(creators.map(creator => [creator._id.toHexString(), creator]));
        return {
            total,
            posts: pagePosts.map(post => {
                const creator = creatorMap.get(post.creator.toHexString());
                if (creator === undefined) {
                    throw new Error(`The creator of post ${post._id}, ${post.creator}, not found`);
                }

                return {
                    id: db.formatId(post._id) as PostId,
                    title: post.title as Title,
                    creator: {
                        id: db.formatId(post.creator) as UserId,
                        name: creator.name as UserName,
                    },
                    creationTime: { utc: post.creationTime } as Time,
                };
            })
        };
    }

    readonly errors = errors;
    constructor(private readonly deps: Deps) { }
}
