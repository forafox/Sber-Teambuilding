import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { MessageRequest } from "./api.gen";
import { Message, messageSchema } from "./get-chat";

// Расширяем тип MessageRequest для поддержки replyToMessageId
type ExtendedMessageRequest = MessageRequest & {
  replyToMessageId?: number;
};

export function useSendMessage(chatId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (message: ExtendedMessageRequest) => {
      const response = await api.api.createMessage(chatId, message);
      return messageSchema.parse(response.data);
    },
    onSuccess: (data) => {
      queryClient.invalidateQueries({
        queryKey: ["chats", chatId, "messages"],
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
