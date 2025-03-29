import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { Message } from "./get-chat";

export function useDeleteMessage(chatId: number, messageId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      await api.api.deleteMessage(chatId, messageId);
      return messageId;
    },
    onSuccess: (deletedMessageId) => {
      queryClient.invalidateQueries({
        queryKey: ["chats", chatId, "messages"],
      });
      queryClient.setQueryData<Message[]>(
        ["chats", chatId, "messages"],
        (oldMessages) => {
          if (!oldMessages) return [];
          // Удаляем сообщение из массива и сохраняем сортировку по id
          return oldMessages
            .filter((msg) => msg.id !== deletedMessageId)
            .sort((a, b) => a.id - b.id);
        },
      );
    },
  });
}
