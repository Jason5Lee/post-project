import { throwUnexpectedValue } from "../common/utils/error";
import { Identity, UserId, UserName } from "../common";

type IdentityInfo = {
    type: "User",
    id: UserId,
    name: UserName,
} | {
    type: "Admin",
}

export abstract class Workflow {
    async run(caller: Identity | undefined): Promise<IdentityInfo | undefined> {
        return caller === undefined ? undefined :
            caller.type === "Admin" ? { type: "Admin" } :
                caller.type === "User" ? { type: "User", id: caller.id, name: await this.getUserName(caller.id) } :
                    throwUnexpectedValue(caller);
    }

    abstract getUserName(id: UserId): Promise<UserName>;
}
