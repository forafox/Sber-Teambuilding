import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { MessageRequest } from "./api.gen";
import { Message, messageSchema } from "./get-chat";

// Расширяем тип MessageRequest для поддержки replyToMessageId и pinned
type ExtendedMessageRequest = Omit<MessageRequest, "replyToMessageId"> & {
  replyToMessageId?: number;
  pinned?: boolean;
};

export function useSendMessage(chatId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (message: ExtendedMessageRequest) => {
      const response = await api.api.createMessage(chatId, {
        content: message.content,
        replyToMessageId: message.replyToMessageId,
        pinned: message.pinned || false,
      });
      return messageSchema.parse(response.data);
    },
    onSuccess: (data) => {
      queryClient.invalidateQueries({
        queryKey: ["chats", chatId, "messages"],
      });
      queryClient.invalidateQueries({
        queryKey: ["chats", chatId],
      });
      queryClient.setQueryData<Message[]>(
        ["chats", chatId, "messages"],
        (oldMessages) => {
          if (!oldMessages) return [data];
          return [...oldMessages, data];
        },
      );
    },
  });
}
