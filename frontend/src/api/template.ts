import { queryOptions, useMutation } from "@tanstack/react-query";
import { eventSchema } from "./get-event";
import { api } from "./api";
import { taskSchema } from "./get-tasks";

export const getTemplatesQueryOptions = queryOptions({
  queryKey: ["templates"],
  queryFn: async () => {
    const response = await api.api.getAllEventTemplates();
    return eventSchema.array().parse(response.data);
  },
});

export const getTemplateQueryOptions = (id: number) =>
  queryOptions({
    queryKey: ["templates", id],
    queryFn: async () => {
      const response = await api.api.getEventTemplateById(id);
      return eventSchema.parse(response.data);
    },
  });

export const getTemplateTasksQueryOptions = (id: number) =>
  queryOptions({
    queryKey: ["templates", id, "tasks"],
    queryFn: async () => {
      const response = await api.api.getTaskTemplatesByEvent(id);
      return taskSchema.array().parse(response.data);
    },
  });

export function useCreateTemplate() {
  return useMutation({
    mutationFn: async (id: number) => {
      const response = await api.api.applyTemplate(id);
      return eventSchema.parse(response.data);
    },
  });
}
