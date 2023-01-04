import axios from "axios";
import { assert } from "./common";

export async function testAdmin(base: string, adminId: string, adminPassword: string): Promise<string> {
    const admin = await axios.post(`${base}/admin/login`, { id: adminId, password: adminPassword });
    assert(admin.status === 200);
    const adminToken = admin.data.token;
    assert(typeof adminToken === "string");
    return adminToken;
}
