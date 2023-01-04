import { Context, formatId, Method, Route } from "../common/utils";
import { Workflow } from ".";
import { throwUnexpectedValue } from "../common/utils/error";

export const route: Route = [Method.GET, "/identity"];

export async function run(ctx: Context, workflow: Workflow) {
    const caller = ctx.getIdentity();
    const result = await workflow.run(caller);
    ctx.setResponse(
        200,
        (
            result === undefined ? {} :
                result.type === "User" ? {
                    user: {
                        name: result.name,
                        id: formatId(result.id),
                    }
                } :
                    result.type === "Admin" ? {
                        admin: {
                            id: formatId(result.id),
                        }
                    } : throwUnexpectedValue(result)
        ) satisfies {
            user?: {
                name: string,
                id: string,
            },
            admin?: {
                id: string,
            },
        }
    );
}
