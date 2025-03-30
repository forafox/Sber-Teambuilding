import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { Message } from "./get-chat";

interface DeleteMessageParams {
  chatId: number;
  messageId: number;
}

export function useDeleteMessage() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ chatId, messageId }: DeleteMessageParams) => {
      await api.api.deleteMessage(chatId, messageId);
      return { chatId, messageId };
    },
    onSuccess: ({ chatId, messageId }) => {
      queryClient.invalidateQueries({
        queryKey: ["chats", chatId, "messages"],
      });
      queryClient.invalidateQueries({
        queryKey: ["chats", chatId],
      });
      queryClient.setQueryData<Message[]>(
        ["chats", chatId, "messages"],
        (oldMessages) => {
          if (!oldMessages) return [];
          // Удаляем сообщение из массива и сохраняем сортировку по id
          return oldMessages
            .filter((msg) => msg.id !== messageId)
            .sort((a, b) => a.id - b.id);
        },
      );
    },
  });
}
