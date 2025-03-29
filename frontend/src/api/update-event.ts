import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { EventRequest } from "./api.gen";
import { eventSchema } from "./get-event";

export function useUpdateEventMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      eventId,
      ...event
    }: EventRequest & { eventId: number }) => {
      const { data: result } = await api.api.updateEvent(eventId, event);
      return eventSchema.parse(result);
    },
    onSuccess: () => {
      // Invalidate the event query to refetch with updated participants
      queryClient.invalidateQueries();
    },
  });
}
