import axios from "axios";
import { assert, UserToken } from "./common";

export async function testUserRegister(base: string): Promise<[string, string]> {
    const user1 = await axios.post(`${base}/register`, { userName: "user1", password: "password1" });
    assert(user1.status === 201);
    assert(typeof user1.data.userId === "string");
    assert(user1.headers["location"] === "/user/" + user1.data.userId);

    const existUser = await axios.post(`${base}/register`, { userName: "user1", password: "password1" }, { validateStatus: () => true });
    assert(existUser.status === 409);
    assert(existUser.data.error.error === "USER_NAME_ALREADY_EXISTS");

    const user2 = await axios.post(`${base}/register`, { userName: "user2", password: "password2" });
    assert(user2.status === 201);
    assert(typeof user2.data.userId === "string");
    assert(user2.headers["location"] === "/user/" + user2.data.userId);

    return [user1.data.userId, user2.data.userId];
}

export async function testGetUser(base: string, user_id: [string, string]) {
    const user1 = await axios.get(`${base}/user/${user_id[0]}`);
    assert(user1.status === 200);
    assert(user1.data.userName === "user1");

    const user2 = await axios.get(`${base}/user/${user_id[1]}`);
    assert(user2.status === 200);
    assert(user2.data.userName === "user2");
}

export async function testUserLogin(base: string, user_id: [string, string]): Promise<[UserToken, UserToken]> {
    const user1 = await axios.post(`${base}/login`, { userName: "user1", password: "password1" });
    assert(user1.status === 200);
    assert(typeof user1.data.token === "string");
    assert(user1.data.id === user_id[0]);

    const user2 = await axios.post(`${base}/login`, { userName: "user2", password: "password2" });
    assert(user2.status === 200);
    assert(typeof user2.data.token === "string");
    assert(user2.data.id === user_id[1]);

    const wrongPassword = await axios.post(`${base}/login`, { userName: "user1", password: "password2" }, { validateStatus: () => true });
    assert(wrongPassword.status === 403);
    assert(wrongPassword.data.error.error === "USER_NAME_OR_PASSWORD_INCORRECT");

    const wrongUserName = await axios.post(`${base}/login`, { userName: "user3", password: "password2" }, { validateStatus: () => true });
    assert(wrongUserName.status === 403);
    assert(wrongUserName.data.error.error === "USER_NAME_OR_PASSWORD_INCORRECT");

    return [{
        userId: user_id[0],
        token: user1.data.token,
    }, {
        userId: user_id[1],
        token: user2.data.token,
    }];
}
