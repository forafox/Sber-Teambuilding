import { getMeQueryOptions } from "@/api/get-me";
import { getUsersQueryOptions } from "@/api/get-users";
import { useQueryClient } from "@tanstack/react-query";
import { createFileRoute, Outlet, redirect } from "@tanstack/react-router";
import { useEffect } from "react";
import useWebSocket from "react-use-websocket";

export const Route = createFileRoute("/_authenticated")({
  component: RouteComponent,
  beforeLoad: async ({ context }) => {
    try {
      await context.queryClient.fetchQuery(getMeQueryOptions());
      context.queryClient.fetchQuery(getUsersQueryOptions());
    } catch (e) {
      console.error(e);
      return redirect({ to: "/sign-in" });
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
