import { useGetChat } from "@/api/get-chat";
import { MessageInput } from "./MessageInput";
import { MessageList } from "./MessageList";
import { useEffect, useState } from "react";
import useWebSocket from "react-use-websocket";
import { useQueryClient } from "@tanstack/react-query";
import { Message } from "@/api/get-chat";

type ChatContainerProps = {
  chatId: number;
};

export function ChatContainer({ chatId }: ChatContainerProps) {
  const { data: chat, isLoading, error } = useGetChat(chatId);
  const { lastMessage } = useWebSocket("/ws");
  const queryClient = useQueryClient();
  const [replyToMessage, setReplyToMessage] = useState<Message | null>(null);

  // Обновление чата при получении сообщения через WebSocket
  useEffect(() => {
    if (lastMessage?.data === "MESSAGES_UPDATED" && chat?.id) {
      queryClient.invalidateQueries({
        queryKey: ["chats", chat.id, "messages"],
      });
    }
  }, [lastMessage, chatId, queryClient, chat?.id]);

  const handleReply = (message: Message) => {
    setReplyToMessage(message);
  };

  const handleCancelReply = () => {
    setReplyToMessage(null);
  };

  if (isLoading) {
    return (
      <div className="flex h-full w-full items-center justify-center">
        <span className="h-8 w-8 animate-spin rounded-full border-2 border-current border-t-transparent" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex h-full w-full items-center justify-center">
        <p className="text-destructive">
          Ошибка при загрузке чата. Пожалуйста, попробуйте позже.
        </p>
      </div>
    );
  }

  if (!chat) {
    return (
      <div className="flex h-full w-full items-center justify-center">
        <p className="text-muted-foreground">Чат недоступен</p>
      </div>
    );
  }

  return (
    <div className="flex h-full flex-col">
      <div className="flex-1 overflow-hidden">
        <MessageList chatId={chatId} onReply={handleReply} />
      </div>
      <MessageInput
        chatId={chatId}
        replyToMessage={replyToMessage}
        onCancelReply={handleCancelReply}
      />
    </div>
  );
}
