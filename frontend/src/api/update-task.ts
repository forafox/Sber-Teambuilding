import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { TaskRequest } from "./api.gen";
import { taskSchema } from "./get-tasks";

export function useUpdateTaskMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      taskId,
      eventId,
      ...task
    }: TaskRequest & { taskId: number; eventId: number }) => {
      const { data: result } = await api.api.updateTask(taskId, eventId, task);
      return taskSchema.parse(result);
    },
    onSuccess: () => {
      // Invalidate the event query to refetch with updated participants
      queryClient.invalidateQueries();
    },
  });
}
