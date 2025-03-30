import { useEffect, useState, useRef } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useGetChat } from "@/api/get-chat";
import { MessageList } from "./MessageList";
import { MessageInput } from "./MessageInput";
import { PinnedMessagesBanner } from "./PinnedMessagesBanner";
import useWebSocket from "react-use-websocket";
import { Message } from "@/api/get-chat";

type ChatContainerProps = {
  chatId: number;
};

export function ChatContainer({ chatId }: ChatContainerProps) {
  const { data: chat, isLoading, error } = useGetChat(chatId);
  const { lastMessage } = useWebSocket("/ws");
  const queryClient = useQueryClient();
  const [replyToMessage, setReplyToMessage] = useState<Message | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (lastMessage?.data === "MESSAGES_UPDATED" && chat?.id) {
      queryClient.invalidateQueries({
        queryKey: ["chats", chat.id, "messages"],
      });
      queryClient.invalidateQueries({
        queryKey: ["chats", chat.id],
      });
    }
  }, [lastMessage, chatId, queryClient, chat?.id]);

  const handleReply = (message: Message) => {
    setReplyToMessage(message);
  };

  const handleCancelReply = () => {
    setReplyToMessage(null);
  };

  const focusInputField = () => {
    if (inputRef.current) {
      inputRef.current.focus();
    }
  };

  const scrollToMessage = (messageId: number) => {
    const messageElement = document.getElementById(`message-${messageId}`);

    if (messageElement) {
      messageElement.scrollIntoView({
        behavior: "smooth",
        block: "center",
      });

      messageElement.classList.add("bg-primary/10");
      setTimeout(() => {
        messageElement.classList.remove("bg-primary/10");
      }, 2000);
    }
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
      {chat.pinnedMessages && chat.pinnedMessages.length > 0 && (
        <PinnedMessagesBanner
          pinnedMessages={chat.pinnedMessages.map((msg) => ({
            ...msg,
            timestamp: new Date(msg.timestamp),
          }))}
          scrollToMessage={scrollToMessage}
        />
      )}
      <div className="flex-1 overflow-hidden">
        <MessageList
          chatId={chatId}
          onReply={handleReply}
          focusInputField={focusInputField}
        />
      </div>
      <MessageInput
        chatId={chatId}
        replyToMessage={replyToMessage}
        onCancelReply={handleCancelReply}
        inputRef={inputRef as React.RefObject<HTMLInputElement>}
      />
    </div>
  );
}
