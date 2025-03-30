import { useEffect, useRef, useMemo } from "react";
import { useGetMessages } from "@/api/get-messages";
import { MessageItem } from "./MessageItem";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Message } from "@/api/get-chat";

type MessageListProps = {
  chatId: number;
  onReply: (message: Message) => void;
  focusInputField?: () => void;
};

export function MessageList({
  chatId,
  onReply,
  focusInputField,
}: MessageListProps) {
  const { data: messages, isLoading, error } = useGetMessages(chatId);
  const scrollAreaRef = useRef<HTMLDivElement>(null);

  // Сортируем сообщения по id для правильного отображения после обновления
  const sortedMessages = useMemo(() => {
    if (!messages?.length) return [];
    return [...messages].sort((a, b) => a.id - b.id);
  }, [messages]);

  // Функция прокрутки к указанному сообщению
  const scrollToMessage = (messageId: number) => {
    // Находим DOM-элемент сообщения по ID
    const messageElement = document.getElementById(`message-${messageId}`);

    if (messageElement) {
      // Прокручиваем элемент в видимую область
      messageElement.scrollIntoView({
        behavior: "smooth",
        block: "center",
      });

      // Добавляем временное подсвечивание для лучшей видимости
      messageElement.classList.add("bg-primary/10");
      setTimeout(() => {
        messageElement.classList.remove("bg-primary/10");
      }, 2000);
    }
  };

  // Scroll to bottom when new messages are received
  useEffect(() => {
    if (scrollAreaRef.current && sortedMessages?.length) {
      const scrollElement = scrollAreaRef.current.querySelector(
        '[data-radix-scroll-area-viewport=""]',
      );
      if (scrollElement) {
        scrollElement.scrollTop = scrollElement.scrollHeight;
      }
    }
  }, [sortedMessages]);

  if (isLoading) {
    return (
      <div className="flex h-full w-full items-center justify-center">
        <span className="h-6 w-6 animate-spin rounded-full border-2 border-current border-t-transparent" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex h-full w-full items-center justify-center">
        <p className="text-destructive">
          Ошибка при загрузке сообщений. Пожалуйста, попробуйте позже.
        </p>
      </div>
    );
  }

  if (!sortedMessages?.length) {
    return (
      <div className="text-muted-foreground flex h-full w-full items-center justify-center">
        Нет сообщений. Начните общение первым!
      </div>
    );
  }

  return (
    <ScrollArea ref={scrollAreaRef} className="h-full px-4 py-4">
      <div className="flex flex-col gap-4">
        {sortedMessages.map((message) => (
          <MessageItem
            key={message.id}
            message={message}
            chatId={chatId}
            onReply={onReply}
            scrollToMessage={scrollToMessage}
            focusInputField={focusInputField}
          />
        ))}
      </div>
    </ScrollArea>
  );
}
