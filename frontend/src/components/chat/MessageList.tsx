import { useEffect, useRef, useMemo } from "react";
import { useGetMessages } from "@/api/get-messages";
import { MessageItem } from "./MessageItem";
import { ScrollArea } from "@/components/ui/scroll-area";

type MessageListProps = {
  chatId: number;
};

export function MessageList({ chatId }: MessageListProps) {
  const { data: messages, isLoading, error } = useGetMessages(chatId);
  const scrollAreaRef = useRef<HTMLDivElement>(null);

  // Сортируем сообщения по id для правильного отображения после обновления
  const sortedMessages = useMemo(() => {
    if (!messages?.length) return [];
    return [...messages].sort((a, b) => a.id - b.id);
  }, [messages]);

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
          <MessageItem key={message.id} message={message} chatId={chatId} />
        ))}
      </div>
    </ScrollArea>
  );
}
