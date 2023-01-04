import axios from "axios";
import { assert, UserToken } from "./common";

export async function testIdentity(base: string, users: [UserToken, UserToken], adminId: string, adminToken: string): Promise<void> {
    const guestResp = await axios.get(`${base}/identity`);
    assert(guestResp.status === 200);
    assert(guestResp.data.user === undefined);
    assert(guestResp.data.admin === undefined);

    const user1Resp = await axios.get(`${base}/identity`, { headers: { Authorization: `Bearer ${users[0].token}` } });
    assert(user1Resp.status === 200);
    assert(user1Resp.data.user.id === users[0].userId);
    assert(user1Resp.data.user.name === "user1");
    assert(user1Resp.data.admin === undefined);

    const user2Resp = await axios.get(`${base}/identity`, { headers: { Authorization: `Bearer ${users[1].token}` } });
    assert(user2Resp.status === 200);
    assert(user2Resp.data.user.id === users[1].userId);
    assert(user2Resp.data.user.name === "user2");
    assert(user2Resp.data.admin === undefined);

    const adminResp = await axios.get(`${base}/identity`, { headers: { Authorization: `Bearer ${adminToken}` } });
    assert(adminResp.status === 200);
    assert(adminResp.data.user === undefined);
    assert(adminResp.data.admin.id === adminId);
}
