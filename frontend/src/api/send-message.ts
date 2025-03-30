import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { MessageRequest, PollRequest } from "./api.gen";
import { Message, messageSchema } from "./get-chat";

// Расширяем тип MessageRequest для поддержки replyToMessageId, pinned и poll
type ExtendedMessageRequest = Omit<MessageRequest, "replyToMessageId"> & {
  replyToMessageId?: number;
  pinned?: boolean;
  poll?: PollRequest;
};

// Преобразуем тип опроса для соответствия API
const mapPollType = (
  type: "SINGLE_CHOICE" | "MULTIPLE_CHOICE" | string,
): "SINGLE" | "MULTIPLE" => {
  switch (type) {
    case "SINGLE_CHOICE":
      return "SINGLE";
    case "MULTIPLE_CHOICE":
      return "MULTIPLE";
    default:
      return "SINGLE"; // Возвращаем значение по умолчанию для безопасности
  }
};

export function useSendMessage(chatId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (message: ExtendedMessageRequest) => {
      // Преобразуем тип опроса, если он есть
      const pollWithCorrectType = message.poll
        ? {
            ...message.poll,
            pollType: mapPollType(message.poll.pollType),
          }
        : undefined;

      const response = await api.api.createMessage(chatId, {
        content: message.content,
        replyToMessageId: message.replyToMessageId,
        pinned: message.pinned || false,
        poll: pollWithCorrectType,
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
