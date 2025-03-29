import { useMutation } from "@tanstack/react-query";
import { api } from "./api";
import { eventSchema } from "./get-event";

export function useCreateEventFromPrompt() {
  return useMutation({
    mutationFn: async (prompt: string) => {
      const { data } = await api.api.createFromPrompt(prompt);
      return eventSchema.parse(data);
    },
  });
}
