import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { TaskRequest } from "./api.gen";
import { taskSchema } from "./get-tasks";

export function useCreateTaskMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      eventId,
      ...task
    }: TaskRequest & { eventId: number }) => {
      const { data: result } = await api.api.createTask(eventId, {
        ...task,
        status: "IN_PROGRESS",
      });
      return taskSchema.parse(result);
    },
    onSuccess: () => {
      // Invalidate the event query to refetch with updated participants
      return queryClient.invalidateQueries();
    },
  });
}
