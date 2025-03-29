import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";

export function useDeleteTaskMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      taskId,
      eventId,
    }: {
      taskId: number;
      eventId: number;
    }) => {
      await api.api.deleteTask(taskId, eventId);
    },
    onSuccess: () => {
      // Invalidate the event query to refetch with updated participants
      queryClient.invalidateQueries();
    },
  });
}
