import { Db as MongoDb } from "mongodb";
import * as runtypes from "runtypes";
import { RuntypeBase } from "runtypes/lib/runtype";

export const users = "users";
export const posts = "posts";
export const admins = "admins";

export function validate<A, ID>(schema: RuntypeBase<A>, collection: string, record: { _id: ID }): asserts record is (A & { _id: ID}) {
    try {
        schema.assert(record);
    } catch (e) {
        if (e instanceof runtypes.ValidationError) {
            throw new Error(`invalid record found in collection '${collection}' with ID '${record._id}': ${e.message}`);
        } else {
            throw e;
        }
    }
    record satisfies A;
}

export function validateArray<A, ID>(schema: RuntypeBase<A>, collection: string, records: { _id: ID }[]): asserts records is (A & { _id: ID})[] {
    for (const record of records) {
        validate(schema, collection, record);
        record satisfies A;
    }
}

export function initDB(db: MongoDb) {
    db.collection(users).createIndex({ name: 1 }, { unique: true });
    db.collection(posts).createIndex({ title: 1 }, { unique: true });
    db.collection(posts).createIndex({ creator: 1 });
    db.collection(posts).createIndex({ lastModified: 1 });
}
