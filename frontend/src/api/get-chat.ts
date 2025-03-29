import { useQuery } from "@tanstack/react-query";
import { api } from "./api";
import { z } from "zod";
import { eventSchema } from "./get-event";
import { userSchema } from "./get-users";

export const messageSchema = z.object({
  id: z.number(),
  content: z.string(),
  author: userSchema,
  timestamp: z.coerce.date(),
});

export const chatSchema = z.object({
  id: z.number(),
  event: eventSchema,
  messages: messageSchema.array(),
});

export type Message = z.infer<typeof messageSchema>;
export type Chat = z.infer<typeof chatSchema>;

export function useGetChat(chatId: number) {
  return useQuery({
    queryKey: ["chats", chatId],
    queryFn: async () => {
      try {
        const response = await api.api.getChatById(chatId);
        return response.data;
      } catch (error) {
        console.error("Failed to fetch chat:", error);
        throw error;
      }
    },
  });
}
