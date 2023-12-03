import axios from "axios";
import { assert, UserToken } from "./common";

type Post = {
    postId: string,
    title: string,
    text?: string,
    url?: string,
    creatorId: string,
    creatorName: string,
    creationTime: number,
}

async function testListPosts(base: string, user1Posts: Post[], user2Posts: Post[], allPosts: Post[]) {
    for (let pageSize = 1; pageSize <= 10; pageSize += 3) {
        for (let page = 1, pageOffset = 0; pageOffset < allPosts.length; ++page, pageOffset += pageSize) {
            const pagePostsResp = await axios.get(base + "/post?page=" + page + "&pageSize=" + pageSize);
            assert(pagePostsResp.status === 200);
            assert(pagePostsResp.data.total === allPosts.length);
            const pagePosts = pagePostsResp.data.posts;
            const expectSize = Math.min(pageSize, allPosts.length - pageOffset);
            assert(pagePosts.length === expectSize);
            for (let j = 0; j < expectSize; j++) {
                const pagePost = pagePosts[j];
                const expectPost = allPosts[allPosts.length - pageOffset - j - 1];
                assert(pagePost.id === expectPost.postId);
                assert(pagePost.title === expectPost.title);
                assert(pagePost.creatorId === expectPost.creatorId);
                assert(pagePost.creatorName === expectPost.creatorName);
                assert(pagePost.creationTime === expectPost.creationTime);
            }
        }    
    }

    for (let pageSize = 1; pageSize <= 10; pageSize += 3) {
        for (let page = 1, pageOffset = 0; pageOffset < user1Posts.length; ++page, pageOffset += pageSize) {
            const pagePostsResp = await axios.get(base + "/post?page=" + page + "&pageSize=" + pageSize + "&creator=" + user1Posts[0].creatorId);
            assert(pagePostsResp.status === 200);
            assert(pagePostsResp.data.total === user1Posts.length);
            const pagePosts = pagePostsResp.data.posts;
            const expectSize = Math.min(pageSize, user1Posts.length - pageOffset);
            assert(pagePosts.length === expectSize);
            for (let j = 0; j < expectSize; j++) {
                const postBefore = pagePosts[j];
                const expectPost = user1Posts[user1Posts.length - pageOffset - j - 1];
                assert(postBefore.id === expectPost.postId);
                assert(postBefore.title === expectPost.title);
                assert(postBefore.creatorId === expectPost.creatorId);
                assert(postBefore.creatorName === expectPost.creatorName);
                assert(postBefore.creationTime === expectPost.creationTime);
            }
        }    
    }

    for (let pageSize = 1; pageSize <= 10; pageSize += 3) {
        for (let page = 1, pageOffset = 0; pageOffset < user2Posts.length; ++page, pageOffset += pageSize) {
            const pagePostsResp = await axios.get(base + "/post?page=" + page + "&pageSize=" + pageSize + "&creator=" + user2Posts[0].creatorId);
            assert(pagePostsResp.status === 200);
            assert(pagePostsResp.data.total === user2Posts.length);
            const pagePosts = pagePostsResp.data.posts;
            const expectSize = Math.min(pageSize, user2Posts.length - pageOffset);
            assert(pagePosts.length === expectSize);
            for (let j = 0; j < expectSize; j++) {
                const postBefore = pagePosts[j];
                const expectPost = user2Posts[user2Posts.length - pageOffset - j - 1];
                assert(postBefore.id === expectPost.postId);
                assert(postBefore.title === expectPost.title);
                assert(postBefore.creatorId === expectPost.creatorId);
                assert(postBefore.creatorName === expectPost.creatorName);
                assert(postBefore.creationTime === expectPost.creationTime);
            }
        }    
    }
}

async function testEditPost(base: string, postId: string, user1Token: UserToken, user2Token: UserToken) {
    const editUser1PostByUser2 = await axios.patch(
        base + "/post/" + postId,
        { text: "New Post Content" },
        { headers: { "Authorization": "Bearer " + user2Token.token }, validateStatus: () => true }
    );
    assert(editUser1PostByUser2.status === 403);
    assert(editUser1PostByUser2.data.error.error === "NOT_CREATOR");

    const editUser1PostToUrl = await axios.patch(
        base + "/post/" + postId,
        { url: "https://www.newurl.com/" },
        { headers: { "Authorization": "Bearer " + user1Token.token }, validateStatus: () => true }
    );
    assert(editUser1PostToUrl.status === 400);
    assert(editUser1PostToUrl.data.error.error === "TYPE_DIFF");

    const editUser1Post = await axios.patch(
        base + "/post/" + postId,
        { text: "New Post Content" },
        { headers: { "Authorization": "Bearer " + user1Token.token } }
    );
    assert(editUser1Post.status === 204);

    const checkEdit = await axios.get(base + "/post/" + postId);
    assert(checkEdit.status === 200);
    assert(checkEdit.data.text === "New Post Content");
    assert(checkEdit.data.lastModified !== undefined);
}

async function testCreateGetAPost(base: string, counter: number, postType: "text" | "url", headers: Partial<unknown>, creatorId: string, creatorName: string, userPosts: Post[], allPosts: Post[]) {
    const body = postType == "text" ? { title: "Title " + counter, text: "Post " + counter } : { title: "Title " + counter, url: "https://url.test/" + counter };
    const post = await axios.post(base + "/post", body, { headers: headers });
    assert(post.status === 201);
    const postId = post.data.postId;
    assert(typeof postId === "string");
    assert(post.headers["location"] === "/post/" + postId);

    const getPost = await axios.get(base + "/post/" + postId);
    assert(getPost.status === 200);
    assert(getPost.data.creatorId === creatorId);
    assert(getPost.data.creatorName === creatorName);
    assert(getPost.data.title === body.title);
    assert(getPost.data.text === body.text);
    assert(getPost.data.url === body.url);
    assert(getPost.data.lastModified === undefined);
    const creationTime = getPost.data.creationTime;
    assert(typeof creationTime === "number");

    const postInfo = {
        postId: postId,
        title: body.title,
        text: body.text,
        url: body.url,
        creatorId: creatorId,
        creatorName: creatorName,
        creationTime: creationTime,
    };
    userPosts.push(postInfo);
    allPosts.push(postInfo);
}

async function testDeletePostByCreator(base: string, user1PostId: string, users: [UserToken, UserToken]): Promise<void> {
    const deleteByUser2 = await axios.delete(base + "/post/" + user1PostId, { headers: { "Authorization": "Bearer " + users[1].token }, validateStatus: () => true });
    assert(deleteByUser2.status === 403);
    assert(deleteByUser2.data.error.error == "NOT_CREATOR_ADMIN");

    const deleteByUser1 = await axios.delete(base + "/post/" + user1PostId, { headers: { "Authorization": "Bearer " + users[0].token } });
    assert(deleteByUser1.status === 204);

    const getDeletedPost = await axios.get(base + "/post/" + user1PostId, { validateStatus: () => true });
    assert(getDeletedPost.status === 404);
}

async function testDeletePostByAdmin(base: string, postId: string, admin_token: string): Promise<void> {
    const deleteByAdmin = await axios.delete(base + "/post/" + postId, { headers: { "Authorization": "Admin " + admin_token } });
    assert(deleteByAdmin.status === 204);

    const getDeletedPost = await axios.get(base + "/post/" + postId, { validateStatus: () => true });
    assert(getDeletedPost.status === 404);
}
export async function testPost(base: string, users: [UserToken, UserToken], adminToken: string): Promise<void> {
    const no_auth = await axios.post(base + "/post", { title: "title", text: "text" }, { validateStatus: () => true });
    assert(no_auth.status === 403);
    assert(no_auth.data.error.error === "USER_ONLY");

    const user1Header = { "Authorization": "Bearer " + users[0].token };
    const user2Header = { "Authorization": "Bearer " + users[1].token };

    const user1Posts: Post[] = [];
    const user2Posts: Post[] = [];
    const allPosts: Post[] = [];
    let counter = 0;

    for (let i = 0; i < 10; i++) {
        await testCreateGetAPost(base, counter++, "text", user1Header, users[0].userId, "user1", user1Posts, allPosts);
        await testCreateGetAPost(base, counter++, "url", user1Header, users[0].userId, "user1", user1Posts, allPosts);
        await testCreateGetAPost(base, counter++, "text", user2Header, users[1].userId, "user2", user2Posts, allPosts);
        await testCreateGetAPost(base, counter++, "url", user2Header, users[1].userId, "user2", user2Posts, allPosts);
    }
    const duplicated = await axios.post(base + "/post", { title: allPosts[0].title, text: "text" }, { headers: user1Header, validateStatus: () => true });
    assert(duplicated.status === 409);
    assert(duplicated.data.error.error === "DUPLICATE_TITLE");

    await testListPosts(base, user1Posts, user2Posts, allPosts);

    let testPostIndex = 0;
    while (testPostIndex < user1Posts.length) {
        const user1Post = user1Posts[testPostIndex];
        if (user1Post.text !== undefined) {
            await testEditPost(base, user1Post.postId, users[0], users[1]);
            break;
        }
        ++testPostIndex;
    }
    if (testPostIndex === user1Posts.length) {
        assert(false);
    }
    await testDeletePostByCreator(base, user1Posts[0].postId, users);
    await testDeletePostByAdmin(base, user2Posts[0].postId, adminToken);
}
