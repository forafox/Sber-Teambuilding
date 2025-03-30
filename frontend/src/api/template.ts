import { queryOptions, useMutation } from "@tanstack/react-query";
import { eventSchema } from "./get-event";
import { api } from "./api";

export const getTemplatesQueryOptions = queryOptions({
  queryKey: ["templates"],
  queryFn: async () => {
    const response = await api.api.getAllEventTemplates();
    return eventSchema.array().parse(response);
  },
});

export const getTemplateQueryOptions = (id: number) =>
  queryOptions({
    queryKey: ["templates", id],
    queryFn: async () => {
      const response = await api.api.getEventById(id);
      return eventSchema.parse(response);
    },
  });

export function useCreateTemplate() {
  return useMutation({
    mutationFn: async (id: number) => {
      await api.api.applyTemplate(id);
    },
  });
}
