import Router from "@koa/router";
import { addRoute, Deps } from "./common/utils";

import * as admin_loginApi from "./admin-login/api";
import * as admin_loginImpl from "./admin-login/impl";

import * as create_postApi from "./create-post/api";
import * as create_postImpl from "./create-post/impl";

import * as delete_postApi from "./delete-post/api";
import * as delete_postImpl from "./delete-post/impl";

import * as edit_postApi from "./edit-post/api";
import * as edit_postImpl from "./edit-post/impl";

import * as get_identityApi from "./get-identity/api";
import * as get_identityImpl from "./get-identity/impl";

import * as get_postApi from "./get-post/api";
import * as get_postImpl from "./get-post/impl";

import * as get_userApi from "./get-user/api";
import * as get_userImpl from "./get-user/impl";

import * as list_postsApi from "./list-posts/api";
import * as list_postsImpl from "./list-posts/impl";

import * as user_loginApi from "./user-login/api";
import * as user_loginImpl from "./user-login/impl";

import * as user_registerApi from "./user-register/api";
import * as user_registerImpl from "./user-register/impl";

export function addRoutes(router: Router, deps: Deps) {
    addRoute(router, deps, admin_loginApi, admin_loginImpl.WorkflowImpl);
    addRoute(router, deps, create_postApi, create_postImpl.WorkflowImpl);
    addRoute(router, deps, delete_postApi, delete_postImpl.WorkflowImpl);
    addRoute(router, deps, edit_postApi, edit_postImpl.WorkflowImpl);
    addRoute(router, deps, get_identityApi, get_identityImpl.WorkflowImpl);
    addRoute(router, deps, get_postApi, get_postImpl.WorkflowImpl);
    addRoute(router, deps, get_userApi, get_userImpl.WorkflowImpl);
    addRoute(router, deps, list_postsApi, list_postsImpl.WorkflowImpl);
    addRoute(router, deps, user_loginApi, user_loginImpl.WorkflowImpl);
    addRoute(router, deps, user_registerApi, user_registerImpl.WorkflowImpl);
}
