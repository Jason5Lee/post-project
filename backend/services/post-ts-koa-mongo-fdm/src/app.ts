import Koa from "koa";
import bodyParser from "koa-bodyparser";
import { apiNotFound, badRequest, internalServerError } from "./common/api";
import { loadEnv } from "./common/utils/env";
import { ResponseError } from "./common/utils/error";
import * as uuid from "uuid";
import { addRoutes } from "./addRoutes";
import Router from "@koa/router";
import * as dotenv from "dotenv";

dotenv.config();

const app = new Koa();
app.on("error", (err, ctx, id) => {
    const prefix = id === undefined ? "" : "[" + id + "] ";
    console.error(prefix + (err.constructor.name) + ": " + err.message + err.stack + "\n");
});
app.use(async (ctx, next) => {
    try {
        await next();

        // https://github.com/koajs/router/issues/158#issuecomment-1233886752
        console.log(ctx);
        if (ctx.status === 404 && ctx.message === "Not Found") {
            ctx.body = apiNotFound;
        }
    } catch (e) {
        if (e instanceof ResponseError) {
            ctx.status = e.status;
            ctx.body = e.body;
            ctx.app.emit("error", e, ctx);
        } else if (e instanceof Error) {
            const id = uuid.v4();
            const resp = internalServerError(id.toString());
            ctx.status = resp.status;
            ctx.body = resp.body;
            ctx.app.emit("error", e, ctx, id);
        }
    }
});

app.use(bodyParser({
    onerror: (err) => {
        throw badRequest(err.message);
    },
}));

(async () => {
    const env = await loadEnv();
    const deps = env.deps;

    const router = new Router();
    addRoutes(router, deps);
    app.use(router.routes());
    process.on("SIGTERM", () => {
        console.log("SIGTERM received");
        deps.close().then(() => process.exit(0));
    });
    app.listen(env.listenPort, env.listenHost, () => {
        console.log(`Server listening on ${env.listenHost}:${env.listenPort}`);
    });
})();
