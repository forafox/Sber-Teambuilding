import { useQuery } from "@tanstack/react-query";
import { api } from "./api";
import { z } from "zod";
import { userSchema } from "./get-users";

export const optionSchema = z.object({
  id: z.number(),
  title: z.string(),
  voters: z.array(userSchema),
});

export const pollSchema = z
  .object({
    id: z.number(),
    title: z.string(),
    pollType: z.enum([
      "SINGLE_CHOICE",
      "MULTIPLE_CHOICE",
      "OPEN_ENDED",
      "SINGLE",
      "MULTIPLE",
    ]),
    options: z.array(optionSchema),
  })
  .nullable()
  .optional();

export const messageSchema = z.object({
  id: z.number(),
  content: z.string(),
  author: userSchema,
  timestamp: z.coerce.date(),
  replyToMessageId: z.number().nullable().optional(),
  pinned: z.boolean().optional(),
  poll: pollSchema,
});

export const chatSchema = z.object({
  id: z.number(),
  messages: messageSchema.array(),
  pinnedMessages: messageSchema.array(),
});

export type Option = z.infer<typeof optionSchema>;
export type Poll = z.infer<typeof pollSchema>;
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
