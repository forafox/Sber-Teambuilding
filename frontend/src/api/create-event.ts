import { useMutation, useQueryClient } from "@tanstack/react-query";
import { EventRequest } from "./api.gen";
import { api } from "./api";

export function useCreateEventMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: EventRequest) => {
      const { data: result } = await api.api.createEvent(data);
      return result;
    },
    onSuccess: () => {
      return queryClient.invalidateQueries();
    },
  });
}
