import { ObjectId } from "mongodb";
import { ResponseError } from "../src/common/utils/error";

export class ExpectedError extends Error { }
export const id1: ObjectId = ObjectId.createFromHexString("000000000000000000000001");
export const id2: ObjectId = ObjectId.createFromHexString("000000000000000000000002");
