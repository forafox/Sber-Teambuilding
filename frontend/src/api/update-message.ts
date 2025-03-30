import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";

import { Message, messageSchema } from "./get-chat";

interface UpdateMessageParams {
  chatId: number;
  messageId: number;
  content: string;
  replyToMessageId?: number;
  pinned?: boolean;
}

interface MessageUpdateRequest {
  content: string;
  replyToMessageId?: number;
  pinned: boolean;
  id: number;
}

export function useUpdateMessage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      chatId,
      messageId,
      ...message
    }: UpdateMessageParams) => {
      const updateRequest: MessageUpdateRequest = {
        content: message.content,
        replyToMessageId: message.replyToMessageId,
        pinned: message.pinned !== undefined ? message.pinned : false,
        id: messageId,
      };

      const response = await api.api.updateMessage(
        chatId,
        messageId,
        updateRequest,
      );
      return messageSchema.parse(response.data);
    },
    onSuccess: (data, { chatId, messageId }) => {
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
          // Обновляем сообщение в массиве, сохраняя позицию и сортировку по id
          return oldMessages
            .map((msg) => (msg.id === messageId ? data : msg))
            .sort((a, b) => a.id - b.id);
        },
      );
    },
  });
}
