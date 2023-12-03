import "dotenv/config";
import * as process from "process";
import { testIdentity } from "./test_identity";
import { testPost } from "./test_post";
import { testUserRegister, testGetUser, testUserLogin } from "./test_user";

interface Config {
    serviceUrl: string;
    adminToken: string;
}

function getEnvVariable(): Config {
    const service_url = process.env.SERVICE_URL;
    const admin_token = process.env.ADMIN_TOKEN;
    if (service_url === undefined) {
        throw new Error("Environment variable SERVICE_URL is not set");
    }
    if (admin_token === undefined) {
        throw new Error("Environment variable ADMIN_TOKEN is not set");
    }
    return {
        serviceUrl: service_url,
        adminToken: admin_token
    };
}

(async () => {
    const config = getEnvVariable();
    const userIds = await testUserRegister(config.serviceUrl);
    await testGetUser(config.serviceUrl, userIds);
    const userTokens = await testUserLogin(config.serviceUrl, userIds);
    await testPost(config.serviceUrl, userTokens, config.adminToken);
    await testIdentity(config.serviceUrl, userTokens, config.adminToken);
})();