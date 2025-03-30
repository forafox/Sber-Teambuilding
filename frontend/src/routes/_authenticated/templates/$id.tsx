import { createFileRoute } from "@tanstack/react-router";
import { TemplateDetailPage } from "@/pages/templates/detail";
import { getTemplateQueryOptions } from "@/api/template";

export const Route = createFileRoute("/_authenticated/templates/$id")({
  component: TemplateDetailPage,
  loader: ({ context, params }) => {
    const id = parseInt(params.id);
    if (isNaN(id)) {
      throw new Error("Invalid template ID");
    }
    context.queryClient.prefetchQuery(getTemplateQueryOptions(id));
  },
});
