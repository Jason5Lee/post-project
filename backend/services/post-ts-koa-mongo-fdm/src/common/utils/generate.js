const fs = require("fs/promises");
(async () => {
    const files = await fs.readdir("./src");
    let imports = "";
    let addRoutes = "";
    for (const file of files) {
        if (file === "common") {
            continue;
        }
        const stats = await fs.lstat("./src/" + file);
        if (stats.isDirectory()) {
            const apiName = file.replace("-", "_") + "Api";
            const implName = file.replace("-", "_") + "Impl";
            imports += `import * as ${apiName} from "./${file}/api";
import * as ${implName} from "./${file}/impl";

`;
            addRoutes += `    addRoute(router, deps, ${apiName}, ${implName});\n`;
        }
        await fs.writeFile("./src/addRoutes.ts", `import Router from "@koa/router";
import { addRoute, Deps } from "./common/utils";

` + imports + "export function addRoutes(router: Router, deps: Deps) {\n" + addRoutes + "}\n");
    }
})();
