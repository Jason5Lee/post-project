const adminId = "LjuwMu-RrkI";
const adminPassword = "adminpass";
const service = "http://localhost:8432";

import { testAdmin } from "./test_admin";
import { testIdentity } from "./test_identity";
import { testPost } from "./test_post";
import { testUserRegister, testGetUser, testUserLogin } from "./test_user";

(async () => {
    const userIds = await testUserRegister(service);
    await testGetUser(service, userIds);
    const userTokens = await testUserLogin(service, userIds);
    
    const adminToken = await testAdmin(service, adminId, adminPassword);
    await testPost(service, userTokens, adminToken);
    await testIdentity(service, userTokens, adminId, adminToken);
})();