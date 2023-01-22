import { Db as MongoDb, ObjectId } from "mongodb";
import * as runtypes from "runtypes";
import { RuntypeBase } from "runtypes/lib/runtype";
import { PasswordEncryptor, PasswordVerifier } from "./password";
import bcrypt from "bcrypt";

export const users = "users";
export const posts = "posts";
export const admins = "admins";

export function validate<A, ID>(schema: RuntypeBase<A>, collection: string, record: { _id: ID }): asserts record is (A & { _id: ID}) {
    try {
        schema.assert(record);
    } catch (e) {
        if (e instanceof runtypes.ValidationError) {
            throw new Error(`invalid record found in collection \`${collection}\` with ID \`${record._id}\`: ${e.message}`);
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

export async function initDB(db: MongoDb): Promise<void> {
    await db.collection(users).createIndex({ name: 1 }, { unique: true });
    await db.collection(posts).createIndex({ title: 1 }, { unique: true });
    await db.collection(posts).createIndex({ creator: 1 });
    await db.collection(posts).createIndex({ lastModified: 1 });
}

export function tryParseId(id: string): ObjectId | undefined {
    const idBuf = Buffer.from(id, "base64url");
    if (idBuf.toString("base64url") !== id || !ObjectId.isValid(idBuf)) {
        return undefined;
    }
    return new ObjectId(idBuf);
}

export function formatId(id: ObjectId): string {
    return id.id.toString("base64url");
}

export class BCryptEncryptor implements PasswordEncryptor {
    constructor(private readonly cost: number) {}

    encrypt(plain: string): Promise<string> {
        return new Promise((resolve, reject) => {
            bcrypt.hash(plain, this.cost, (err, hash) => {
                if (err) {
                    reject(err);
                } else {
                    resolve(hash);
                }
            });
        });
    }
}

export class BCryptVerifier implements PasswordVerifier {
    constructor(private readonly encrypted: string) {}
    verify(plain: string): Promise<boolean> {
        return new Promise((resolve, reject) => {
            bcrypt.compare(plain, this.encrypted, (err, res) => {
                if (err) {
                    reject(err);
                } else {
                    resolve(res);
                }
            });
        });
    }
}
