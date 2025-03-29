import { queryOptions } from "@tanstack/react-query";
import { taskSchema } from "./get-tasks";
import { api } from "./api";

export const getTaskByIdQueryOptions = (eventId: number, taskId: number) =>
  queryOptions({
    queryKey: ["events", eventId, "tasks", taskId],
    queryFn: async () => {
      const { data } = await api.api.getTaskById(eventId, taskId);
      return taskSchema.parse(data);
    },
  });
