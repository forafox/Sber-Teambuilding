import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { Message, messageSchema } from "./get-chat";
import { PollUpdateRequest } from "./api.gen";

// Параметры для обновления сообщения
type UpdateMessageParams = {
  chatId: number;
  messageId: number;
  content: string;
  replyToMessageId?: number;
  pinned?: boolean;
  poll?: PollUpdateRequest;
};

export function useUpdateMessage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      chatId,
      messageId,
      ...message
    }: UpdateMessageParams) => {
      const response = await api.api.updateMessage(chatId, messageId, {
        id: messageId,
        content: message.content,
        replyToMessageId: message.replyToMessageId,
        pinned: message.pinned !== undefined ? message.pinned : false,
        poll: message.poll,
      });
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
