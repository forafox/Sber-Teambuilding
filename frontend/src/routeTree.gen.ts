/* eslint-disable */

// @ts-nocheck

// noinspection JSUnusedGlobalSymbols

// This file was automatically generated by TanStack Router.
// You should NOT make any changes in this file as it will be overwritten.
// Additionally, you should also exclude this file from your linter and/or formatter to prevent it from being checked or modified.

// Import Routes

import { Route as rootRoute } from "./routes/__root"
import { Route as SignUpImport } from "./routes/sign-up"
import { Route as SignInImport } from "./routes/sign-in"
import { Route as AuthenticatedRouteImport } from "./routes/_authenticated/route"
import { Route as IndexImport } from "./routes/index"
import { Route as AuthenticatedTokenImport } from "./routes/_authenticated/token"
import { Route as AuthenticatedHomeImport } from "./routes/_authenticated/home"
import { Route as AuthenticatedTemplatesIndexImport } from "./routes/_authenticated/templates/index"
import { Route as AuthenticatedTemplatesIdImport } from "./routes/_authenticated/templates/$id"
import { Route as AuthenticatedEventsNewImport } from "./routes/_authenticated/events/new"
import { Route as AuthenticatedEventsEventIdRouteImport } from "./routes/_authenticated/events/$eventId/route"
import { Route as AuthenticatedEventsEventIdExpensesImport } from "./routes/_authenticated/events/$eventId/expenses"
import { Route as AuthenticatedEventsEventIdChatImport } from "./routes/_authenticated/events/$eventId/chat"
import { Route as AuthenticatedEventsEventIdTasksIndexImport } from "./routes/_authenticated/events/$eventId/tasks.index"
import { Route as AuthenticatedEventsEventIdTasksTaskIdImport } from "./routes/_authenticated/events/$eventId/tasks.$taskId"

// Create/Update Routes

const SignUpRoute = SignUpImport.update({
  id: "/sign-up",
  path: "/sign-up",
  getParentRoute: () => rootRoute,
} as any)

const SignInRoute = SignInImport.update({
  id: "/sign-in",
  path: "/sign-in",
  getParentRoute: () => rootRoute,
} as any)

const AuthenticatedRouteRoute = AuthenticatedRouteImport.update({
  id: "/_authenticated",
  getParentRoute: () => rootRoute,
} as any)

const IndexRoute = IndexImport.update({
  id: "/",
  path: "/",
  getParentRoute: () => rootRoute,
} as any)

const AuthenticatedTokenRoute = AuthenticatedTokenImport.update({
  id: "/token",
  path: "/token",
  getParentRoute: () => AuthenticatedRouteRoute,
} as any)

const AuthenticatedHomeRoute = AuthenticatedHomeImport.update({
  id: "/home",
  path: "/home",
  getParentRoute: () => AuthenticatedRouteRoute,
} as any)

const AuthenticatedTemplatesIndexRoute =
  AuthenticatedTemplatesIndexImport.update({
    id: "/templates/",
    path: "/templates/",
    getParentRoute: () => AuthenticatedRouteRoute,
  } as any)

const AuthenticatedTemplatesIdRoute = AuthenticatedTemplatesIdImport.update({
  id: "/templates/$id",
  path: "/templates/$id",
  getParentRoute: () => AuthenticatedRouteRoute,
} as any)

const AuthenticatedEventsNewRoute = AuthenticatedEventsNewImport.update({
  id: "/events/new",
  path: "/events/new",
  getParentRoute: () => AuthenticatedRouteRoute,
} as any)

const AuthenticatedEventsEventIdRouteRoute =
  AuthenticatedEventsEventIdRouteImport.update({
    id: "/events/$eventId",
    path: "/events/$eventId",
    getParentRoute: () => AuthenticatedRouteRoute,
  } as any)

const AuthenticatedEventsEventIdExpensesRoute =
  AuthenticatedEventsEventIdExpensesImport.update({
    id: "/expenses",
    path: "/expenses",
    getParentRoute: () => AuthenticatedEventsEventIdRouteRoute,
  } as any)

const AuthenticatedEventsEventIdChatRoute =
  AuthenticatedEventsEventIdChatImport.update({
    id: "/chat",
    path: "/chat",
    getParentRoute: () => AuthenticatedEventsEventIdRouteRoute,
  } as any)

const AuthenticatedEventsEventIdTasksIndexRoute =
  AuthenticatedEventsEventIdTasksIndexImport.update({
    id: "/tasks/",
    path: "/tasks/",
    getParentRoute: () => AuthenticatedEventsEventIdRouteRoute,
  } as any)

const AuthenticatedEventsEventIdTasksTaskIdRoute =
  AuthenticatedEventsEventIdTasksTaskIdImport.update({
    id: "/tasks/$taskId",
    path: "/tasks/$taskId",
    getParentRoute: () => AuthenticatedEventsEventIdRouteRoute,
  } as any)

// Populate the FileRoutesByPath interface

declare module "@tanstack/react-router" {
  interface FileRoutesByPath {
    "/": {
      id: "/"
      path: "/"
      fullPath: "/"
      preLoaderRoute: typeof IndexImport
      parentRoute: typeof rootRoute
    }
    "/_authenticated": {
      id: "/_authenticated"
      path: ""
      fullPath: ""
      preLoaderRoute: typeof AuthenticatedRouteImport
      parentRoute: typeof rootRoute
    }
    "/sign-in": {
      id: "/sign-in"
      path: "/sign-in"
      fullPath: "/sign-in"
      preLoaderRoute: typeof SignInImport
      parentRoute: typeof rootRoute
    }
    "/sign-up": {
      id: "/sign-up"
      path: "/sign-up"
      fullPath: "/sign-up"
      preLoaderRoute: typeof SignUpImport
      parentRoute: typeof rootRoute
    }
    "/_authenticated/home": {
      id: "/_authenticated/home"
      path: "/home"
      fullPath: "/home"
      preLoaderRoute: typeof AuthenticatedHomeImport
      parentRoute: typeof AuthenticatedRouteImport
    }
    "/_authenticated/token": {
      id: "/_authenticated/token"
      path: "/token"
      fullPath: "/token"
      preLoaderRoute: typeof AuthenticatedTokenImport
      parentRoute: typeof AuthenticatedRouteImport
    }
    "/_authenticated/events/$eventId": {
      id: "/_authenticated/events/$eventId"
      path: "/events/$eventId"
      fullPath: "/events/$eventId"
      preLoaderRoute: typeof AuthenticatedEventsEventIdRouteImport
      parentRoute: typeof AuthenticatedRouteImport
    }
    "/_authenticated/events/new": {
      id: "/_authenticated/events/new"
      path: "/events/new"
      fullPath: "/events/new"
      preLoaderRoute: typeof AuthenticatedEventsNewImport
      parentRoute: typeof AuthenticatedRouteImport
    }
    "/_authenticated/templates/$id": {
      id: "/_authenticated/templates/$id"
      path: "/templates/$id"
      fullPath: "/templates/$id"
      preLoaderRoute: typeof AuthenticatedTemplatesIdImport
      parentRoute: typeof AuthenticatedRouteImport
    }
    "/_authenticated/templates/": {
      id: "/_authenticated/templates/"
      path: "/templates"
      fullPath: "/templates"
      preLoaderRoute: typeof AuthenticatedTemplatesIndexImport
      parentRoute: typeof AuthenticatedRouteImport
    }
    "/_authenticated/events/$eventId/chat": {
      id: "/_authenticated/events/$eventId/chat"
      path: "/chat"
      fullPath: "/events/$eventId/chat"
      preLoaderRoute: typeof AuthenticatedEventsEventIdChatImport
      parentRoute: typeof AuthenticatedEventsEventIdRouteImport
    }
    "/_authenticated/events/$eventId/expenses": {
      id: "/_authenticated/events/$eventId/expenses"
      path: "/expenses"
      fullPath: "/events/$eventId/expenses"
      preLoaderRoute: typeof AuthenticatedEventsEventIdExpensesImport
      parentRoute: typeof AuthenticatedEventsEventIdRouteImport
    }
    "/_authenticated/events/$eventId/tasks/$taskId": {
      id: "/_authenticated/events/$eventId/tasks/$taskId"
      path: "/tasks/$taskId"
      fullPath: "/events/$eventId/tasks/$taskId"
      preLoaderRoute: typeof AuthenticatedEventsEventIdTasksTaskIdImport
      parentRoute: typeof AuthenticatedEventsEventIdRouteImport
    }
    "/_authenticated/events/$eventId/tasks/": {
      id: "/_authenticated/events/$eventId/tasks/"
      path: "/tasks"
      fullPath: "/events/$eventId/tasks"
      preLoaderRoute: typeof AuthenticatedEventsEventIdTasksIndexImport
      parentRoute: typeof AuthenticatedEventsEventIdRouteImport
    }
  }
}

// Create and export the route tree

interface AuthenticatedEventsEventIdRouteRouteChildren {
  AuthenticatedEventsEventIdChatRoute: typeof AuthenticatedEventsEventIdChatRoute
  AuthenticatedEventsEventIdExpensesRoute: typeof AuthenticatedEventsEventIdExpensesRoute
  AuthenticatedEventsEventIdTasksTaskIdRoute: typeof AuthenticatedEventsEventIdTasksTaskIdRoute
  AuthenticatedEventsEventIdTasksIndexRoute: typeof AuthenticatedEventsEventIdTasksIndexRoute
}

const AuthenticatedEventsEventIdRouteRouteChildren: AuthenticatedEventsEventIdRouteRouteChildren =
  {
    AuthenticatedEventsEventIdChatRoute: AuthenticatedEventsEventIdChatRoute,
    AuthenticatedEventsEventIdExpensesRoute:
      AuthenticatedEventsEventIdExpensesRoute,
    AuthenticatedEventsEventIdTasksTaskIdRoute:
      AuthenticatedEventsEventIdTasksTaskIdRoute,
    AuthenticatedEventsEventIdTasksIndexRoute:
      AuthenticatedEventsEventIdTasksIndexRoute,
  }

const AuthenticatedEventsEventIdRouteRouteWithChildren =
  AuthenticatedEventsEventIdRouteRoute._addFileChildren(
    AuthenticatedEventsEventIdRouteRouteChildren,
  )

interface AuthenticatedRouteRouteChildren {
  AuthenticatedHomeRoute: typeof AuthenticatedHomeRoute
  AuthenticatedTokenRoute: typeof AuthenticatedTokenRoute
  AuthenticatedEventsEventIdRouteRoute: typeof AuthenticatedEventsEventIdRouteRouteWithChildren
  AuthenticatedEventsNewRoute: typeof AuthenticatedEventsNewRoute
  AuthenticatedTemplatesIdRoute: typeof AuthenticatedTemplatesIdRoute
  AuthenticatedTemplatesIndexRoute: typeof AuthenticatedTemplatesIndexRoute
}

const AuthenticatedRouteRouteChildren: AuthenticatedRouteRouteChildren = {
  AuthenticatedHomeRoute: AuthenticatedHomeRoute,
  AuthenticatedTokenRoute: AuthenticatedTokenRoute,
  AuthenticatedEventsEventIdRouteRoute:
    AuthenticatedEventsEventIdRouteRouteWithChildren,
  AuthenticatedEventsNewRoute: AuthenticatedEventsNewRoute,
  AuthenticatedTemplatesIdRoute: AuthenticatedTemplatesIdRoute,
  AuthenticatedTemplatesIndexRoute: AuthenticatedTemplatesIndexRoute,
}

const AuthenticatedRouteRouteWithChildren =
  AuthenticatedRouteRoute._addFileChildren(AuthenticatedRouteRouteChildren)

export interface FileRoutesByFullPath {
  "/": typeof IndexRoute
  "": typeof AuthenticatedRouteRouteWithChildren
  "/sign-in": typeof SignInRoute
  "/sign-up": typeof SignUpRoute
  "/home": typeof AuthenticatedHomeRoute
  "/token": typeof AuthenticatedTokenRoute
  "/events/$eventId": typeof AuthenticatedEventsEventIdRouteRouteWithChildren
  "/events/new": typeof AuthenticatedEventsNewRoute
  "/templates/$id": typeof AuthenticatedTemplatesIdRoute
  "/templates": typeof AuthenticatedTemplatesIndexRoute
  "/events/$eventId/chat": typeof AuthenticatedEventsEventIdChatRoute
  "/events/$eventId/expenses": typeof AuthenticatedEventsEventIdExpensesRoute
  "/events/$eventId/tasks/$taskId": typeof AuthenticatedEventsEventIdTasksTaskIdRoute
  "/events/$eventId/tasks": typeof AuthenticatedEventsEventIdTasksIndexRoute
}

export interface FileRoutesByTo {
  "/": typeof IndexRoute
  "": typeof AuthenticatedRouteRouteWithChildren
  "/sign-in": typeof SignInRoute
  "/sign-up": typeof SignUpRoute
  "/home": typeof AuthenticatedHomeRoute
  "/token": typeof AuthenticatedTokenRoute
  "/events/$eventId": typeof AuthenticatedEventsEventIdRouteRouteWithChildren
  "/events/new": typeof AuthenticatedEventsNewRoute
  "/templates/$id": typeof AuthenticatedTemplatesIdRoute
  "/templates": typeof AuthenticatedTemplatesIndexRoute
  "/events/$eventId/chat": typeof AuthenticatedEventsEventIdChatRoute
  "/events/$eventId/expenses": typeof AuthenticatedEventsEventIdExpensesRoute
  "/events/$eventId/tasks/$taskId": typeof AuthenticatedEventsEventIdTasksTaskIdRoute
  "/events/$eventId/tasks": typeof AuthenticatedEventsEventIdTasksIndexRoute
}

export interface FileRoutesById {
  __root__: typeof rootRoute
  "/": typeof IndexRoute
  "/_authenticated": typeof AuthenticatedRouteRouteWithChildren
  "/sign-in": typeof SignInRoute
  "/sign-up": typeof SignUpRoute
  "/_authenticated/home": typeof AuthenticatedHomeRoute
  "/_authenticated/token": typeof AuthenticatedTokenRoute
  "/_authenticated/events/$eventId": typeof AuthenticatedEventsEventIdRouteRouteWithChildren
  "/_authenticated/events/new": typeof AuthenticatedEventsNewRoute
  "/_authenticated/templates/$id": typeof AuthenticatedTemplatesIdRoute
  "/_authenticated/templates/": typeof AuthenticatedTemplatesIndexRoute
  "/_authenticated/events/$eventId/chat": typeof AuthenticatedEventsEventIdChatRoute
  "/_authenticated/events/$eventId/expenses": typeof AuthenticatedEventsEventIdExpensesRoute
  "/_authenticated/events/$eventId/tasks/$taskId": typeof AuthenticatedEventsEventIdTasksTaskIdRoute
  "/_authenticated/events/$eventId/tasks/": typeof AuthenticatedEventsEventIdTasksIndexRoute
}

export interface FileRouteTypes {
  fileRoutesByFullPath: FileRoutesByFullPath
  fullPaths:
    | "/"
    | ""
    | "/sign-in"
    | "/sign-up"
    | "/home"
    | "/token"
    | "/events/$eventId"
    | "/events/new"
    | "/templates/$id"
    | "/templates"
    | "/events/$eventId/chat"
    | "/events/$eventId/expenses"
    | "/events/$eventId/tasks/$taskId"
    | "/events/$eventId/tasks"
  fileRoutesByTo: FileRoutesByTo
  to:
    | "/"
    | ""
    | "/sign-in"
    | "/sign-up"
    | "/home"
    | "/token"
    | "/events/$eventId"
    | "/events/new"
    | "/templates/$id"
    | "/templates"
    | "/events/$eventId/chat"
    | "/events/$eventId/expenses"
    | "/events/$eventId/tasks/$taskId"
    | "/events/$eventId/tasks"
  id:
    | "__root__"
    | "/"
    | "/_authenticated"
    | "/sign-in"
    | "/sign-up"
    | "/_authenticated/home"
    | "/_authenticated/token"
    | "/_authenticated/events/$eventId"
    | "/_authenticated/events/new"
    | "/_authenticated/templates/$id"
    | "/_authenticated/templates/"
    | "/_authenticated/events/$eventId/chat"
    | "/_authenticated/events/$eventId/expenses"
    | "/_authenticated/events/$eventId/tasks/$taskId"
    | "/_authenticated/events/$eventId/tasks/"
  fileRoutesById: FileRoutesById
}

export interface RootRouteChildren {
  IndexRoute: typeof IndexRoute
  AuthenticatedRouteRoute: typeof AuthenticatedRouteRouteWithChildren
  SignInRoute: typeof SignInRoute
  SignUpRoute: typeof SignUpRoute
}

const rootRouteChildren: RootRouteChildren = {
  IndexRoute: IndexRoute,
  AuthenticatedRouteRoute: AuthenticatedRouteRouteWithChildren,
  SignInRoute: SignInRoute,
  SignUpRoute: SignUpRoute,
}

export const routeTree = rootRoute
  ._addFileChildren(rootRouteChildren)
  ._addFileTypes<FileRouteTypes>()

/* ROUTE_MANIFEST_START
{
  "routes": {
    "__root__": {
      "filePath": "__root.tsx",
      "children": [
        "/",
        "/_authenticated",
        "/sign-in",
        "/sign-up"
      ]
    },
    "/": {
      "filePath": "index.tsx"
    },
    "/_authenticated": {
      "filePath": "_authenticated/route.tsx",
      "children": [
        "/_authenticated/home",
        "/_authenticated/token",
        "/_authenticated/events/$eventId",
        "/_authenticated/events/new",
        "/_authenticated/templates/$id",
        "/_authenticated/templates/"
      ]
    },
    "/sign-in": {
      "filePath": "sign-in.tsx"
    },
    "/sign-up": {
      "filePath": "sign-up.tsx"
    },
    "/_authenticated/home": {
      "filePath": "_authenticated/home.tsx",
      "parent": "/_authenticated"
    },
    "/_authenticated/token": {
      "filePath": "_authenticated/token.tsx",
      "parent": "/_authenticated"
    },
    "/_authenticated/events/$eventId": {
      "filePath": "_authenticated/events/$eventId/route.tsx",
      "parent": "/_authenticated",
      "children": [
        "/_authenticated/events/$eventId/chat",
        "/_authenticated/events/$eventId/expenses",
        "/_authenticated/events/$eventId/tasks/$taskId",
        "/_authenticated/events/$eventId/tasks/"
      ]
    },
    "/_authenticated/events/new": {
      "filePath": "_authenticated/events/new.tsx",
      "parent": "/_authenticated"
    },
    "/_authenticated/templates/$id": {
      "filePath": "_authenticated/templates/$id.tsx",
      "parent": "/_authenticated"
    },
    "/_authenticated/templates/": {
      "filePath": "_authenticated/templates/index.tsx",
      "parent": "/_authenticated"
    },
    "/_authenticated/events/$eventId/chat": {
      "filePath": "_authenticated/events/$eventId/chat.tsx",
      "parent": "/_authenticated/events/$eventId"
    },
    "/_authenticated/events/$eventId/expenses": {
      "filePath": "_authenticated/events/$eventId/expenses.tsx",
      "parent": "/_authenticated/events/$eventId"
    },
    "/_authenticated/events/$eventId/tasks/$taskId": {
      "filePath": "_authenticated/events/$eventId/tasks.$taskId.tsx",
      "parent": "/_authenticated/events/$eventId"
    },
    "/_authenticated/events/$eventId/tasks/": {
      "filePath": "_authenticated/events/$eventId/tasks.index.tsx",
      "parent": "/_authenticated/events/$eventId"
    }
  }
}
ROUTE_MANIFEST_END */
