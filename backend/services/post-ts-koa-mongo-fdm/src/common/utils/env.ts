import { MongoClient } from "mongodb";
import { Deps } from ".";
import { BCryptEncryptor, initDB } from "./db";

function getStringEnv(name: string): string {
    const value = process.env[name];
    if (value === undefined) {
        throw new Error(`Environment variable \`${name}\` is not set`);
    }
    return value;
}
function getIntEnv(name: string, options?: { default?: number }): number {
    const value = process.env[name];
    if (value === undefined) {
        if (options?.default !== undefined) {
            return options.default;
        }
        throw new Error(`Environment variable \`${name}\` is not set`);
    }
    const numValue = +value;
    if (!Number.isSafeInteger(numValue)) {
        throw new Error(`Environment variable \`${name}\` is not an integer or too large`);
    }
    return numValue;
}

export interface Env {
    readonly listenHost: string,
    readonly listenPort: number,
    readonly deps: Deps,
}

export async function loadEnv(): Promise<Env> {
    const listenHost = getStringEnv("LISTEN_HOST");
    const listenPort = getIntEnv("LISTEN_PORT");
    const mongoUrl = getStringEnv("MONGO_URL");
    const tokenValidSecs = getIntEnv("TOKEN_VALID_SECS");
    const tokenSecret = getStringEnv("TOKEN_SECRET");
    const encryptionCost = getIntEnv("ENCRYPTION_COST", { default: 10 });
    
    const mongoClient = await MongoClient.connect(mongoUrl);
    const mongoDbClient = mongoClient.db();
    initDB(mongoDbClient);

    return {
        listenHost,
        listenPort,
        deps: {
            encryptor: new BCryptEncryptor(encryptionCost),
            mongoDb: mongoDbClient,
            authConfig: {
                validSecs: tokenValidSecs,
                secret: tokenSecret,
            },
            close(): Promise<void> {
                return mongoClient.close();
            }
        }
    };
}