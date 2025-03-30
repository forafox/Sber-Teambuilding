import { useMutation } from "@tanstack/react-query";
import { api } from "./api";
import { eventSchema } from "./get-event";

export function useCreateInviteTokenMutation() {
  return useMutation({
    mutationFn: async (eventId: number) => {
      const { data } = await api.api.createEventToken(eventId);
      return data;
    },

  });
}

export function useRedeemInviteTokenMutation() {
  return useMutation({
    mutationFn: async (token: string) => {
      const { data } = await api.api.verifyEventToken(token);
      return eventSchema.parse(data);
    },
  });
}
