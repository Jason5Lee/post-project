import { throwUnexpectedValue } from "../common/utils/error";
import { AdminId, Identity, UserId, UserName } from "../common";

type IdentityInfo = {
    type: "User",
    id: UserId,
    name: UserName,
} | {
    type: "Admin",
    id: AdminId,
}

export abstract class Workflow {
    async run(caller: Identity | undefined): Promise<IdentityInfo | undefined> {
        return caller === undefined ? undefined :
            caller.type === "Admin" ? { type: "Admin", id: caller.id } :
                caller.type === "User" ? { type: "User", id: caller.id, name: await this.getUserName(caller.id) } :
                    throwUnexpectedValue(caller);
    }

    abstract getUserName(id: UserId): Promise<UserName>;
}
