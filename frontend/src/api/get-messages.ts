import { useQuery } from "@tanstack/react-query";
import { api } from "./api";
import { messageSchema } from "./get-chat";

export const getMessagesQueryOptions = (chatId: number) => ({
  queryKey: ["chats", chatId, "messages"],
  queryFn: async () => {
    try {
      const response = await api.api.getAllMessagesByChatId(chatId);
      return messageSchema.array().parse(response.data);
    } catch (error) {
      console.error("Failed to fetch messages:", error);
      throw error;
    }
  },
});

export function useGetMessages(chatId: number) {
  return useQuery(getMessagesQueryOptions(chatId));
}
