import { getMeQueryOptions } from "@/api/get-me";
import { getUsersQueryOptions } from "@/api/get-users";
import { useQueryClient } from "@tanstack/react-query";
import { createFileRoute, Outlet, redirect } from "@tanstack/react-router";
import { useEffect } from "react";
import useWebSocket from "react-use-websocket";

export const Route = createFileRoute("/_authenticated")({
  component: RouteComponent,
  beforeLoad: async ({ context, location }) => {
    try {
      await context.queryClient.fetchQuery(getMeQueryOptions());
      context.queryClient.fetchQuery(getUsersQueryOptions());
    } catch (e) {
      console.error(e);
      const currentPath = location.pathname;
      return redirect({
        to: "/sign-in",
        search: { redirect: currentPath + location.searchStr },
      });
    }
  },
});

function RouteComponent() {
  useInvalidateOnWebsocket();
  return (
    <div>
      <Outlet />
    </div>
  );
}

function useInvalidateOnWebsocket() {
  const { lastMessage } = useWebSocket("/ws");
  const queryClient = useQueryClient();
  useEffect(() => {
    queryClient.invalidateQueries();
  }, [lastMessage, queryClient]);
}
