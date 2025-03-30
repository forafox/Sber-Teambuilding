import { useMutation } from "@tanstack/react-query";
import { api } from "./api";

export function useSelectTelegramUsername() {
  return useMutation({
    mutationFn: async (username: string) => {
      const response = await api.api.createTelegramUser({
        telegramUsername: username,
      });
      return response.data;
    },
  });
}
