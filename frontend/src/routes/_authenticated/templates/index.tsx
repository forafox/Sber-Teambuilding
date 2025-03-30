import { createFileRoute } from "@tanstack/react-router";
import { TemplatesPage } from "@/pages/templates";
import { getTemplatesQueryOptions } from "@/api/template";

export const Route = createFileRoute("/_authenticated/templates/")({
  component: TemplatesPage,
  loader: ({ context }) => {
    context.queryClient.prefetchQuery(getTemplatesQueryOptions);
  },
});
