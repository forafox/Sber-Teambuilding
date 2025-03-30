import { useState, useEffect } from "react";
import { Message } from "@/api/get-chat";
import { format, parseISO } from "date-fns";
import { PinIcon, ChevronLeftIcon, ChevronRightIcon } from "lucide-react";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";

type PinnedMessagesBannerProps = {
  pinnedMessages: Message[];
  scrollToMessage: (messageId: number) => void;
};

export function PinnedMessagesBanner({
  pinnedMessages,
  scrollToMessage,
}: PinnedMessagesBannerProps) {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [expanded, setExpanded] = useState(false);
  const [prevMessageCount, setPrevMessageCount] = useState(
    pinnedMessages.length,
  );

  const getTimestamp = (message: Message): Date => {
    if (message.timestamp instanceof Date) {
      return message.timestamp;
    }
    if (typeof message.timestamp === "string") {
      return parseISO(message.timestamp);
    }
    return new Date();
  };

  useEffect(() => {
    if (!pinnedMessages.length) return;

    if (pinnedMessages.length < prevMessageCount) {
      if (currentIndex >= pinnedMessages.length && pinnedMessages.length > 0) {
        setCurrentIndex(pinnedMessages.length - 1);
      }
    }

    setPrevMessageCount(pinnedMessages.length);
  }, [pinnedMessages.length, currentIndex, prevMessageCount]);

  if (!pinnedMessages.length) return null;

  const sortedMessages = [...pinnedMessages].sort((a, b) => {
    const timeA = getTimestamp(a).getTime();
    const timeB = getTimestamp(b).getTime();
    return timeB - timeA;
  });

  const currentMessage = sortedMessages[currentIndex];

  const handleShowNext = () => {
    setCurrentIndex((prev) =>
      prev < sortedMessages.length - 1 ? prev + 1 : prev,
    );
  };

  const handleShowPrevious = () => {
    setCurrentIndex((prev) => (prev > 0 ? prev - 1 : prev));
  };

  const handleToggleExpanded = () => {
    setExpanded((prev) => !prev);
  };

  const handleCurrentMessageClick = () => {
    if (!currentMessage) return;

    scrollToMessage(currentMessage.id);

    // Переход к следующему сообщению
    setTimeout(() => {
      if (currentIndex < sortedMessages.length - 1) {
        setCurrentIndex(currentIndex + 1);
      } else {
        // Если это последнее сообщение, переходим к первому
        setCurrentIndex(0);
      }
    }, 100);
  };

  const handleMessageClick = (messageId: number, index: number) => {
    scrollToMessage(messageId);
    setCurrentIndex(index);
    setExpanded(false);
  };

  const formatMessageDate = (message: Message): string => {
    try {
      const date = getTimestamp(message);
      return format(date, "dd.MM.yyyy HH:mm");
    } catch (error) {
      console.error("Error formatting date:", error);
      return "Дата неизвестна";
    }
  };

  const renderCollapsedView = () => {
    if (!currentMessage) return null;

    return (
      <div
        className="border-border bg-muted hover:bg-muted/80 flex cursor-pointer items-start rounded-md border px-2 py-1 text-xs"
        onClick={handleCurrentMessageClick}
      >
        <PinIcon className="text-muted-foreground mt-0.5 mr-1.5 h-3 w-3 shrink-0" />
        <div className="min-w-0 flex-1">
          <div className="flex items-baseline justify-between">
            <span className="truncate font-medium">
              {currentMessage.author.name}
            </span>
            <div className="ml-1 flex shrink-0 items-center gap-0.5">
              {sortedMessages.length > 1 && (
                <>
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-4 w-4 p-0"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleShowPrevious();
                    }}
                    disabled={currentIndex === 0}
                  >
                    <ChevronLeftIcon className="h-2.5 w-2.5" />
                  </Button>
                  <span className="text-muted-foreground text-[10px]">
                    {currentIndex + 1}/{sortedMessages.length}
                  </span>
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-4 w-4 p-0"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleShowNext();
                    }}
                    disabled={currentIndex === sortedMessages.length - 1}
                  >
                    <ChevronRightIcon className="h-2.5 w-2.5" />
                  </Button>
                </>
              )}
            </div>
          </div>
          <p className="text-muted-foreground line-clamp-1">
            {currentMessage.content}
          </p>
        </div>
      </div>
    );
  };

  const renderExpandedView = () => (
    <div className="border-border bg-muted flex flex-col rounded-md border">
      <div
        className="flex cursor-pointer items-center justify-between px-2 py-1 text-xs"
        onClick={handleToggleExpanded}
      >
        <div className="flex items-center gap-1.5">
          <PinIcon className="text-muted-foreground h-3 w-3" />
          <span className="text-muted-foreground font-medium">
            {sortedMessages.length > 1
              ? `${sortedMessages.length} закрепленных сообщений`
              : "Закрепленное сообщение"}
          </span>
        </div>
      </div>
      <ScrollArea className="max-h-60">
        <div className="flex flex-col gap-0.5 p-1.5">
          {sortedMessages.map((message, index) => (
            <div
              key={message.id}
              className={`hover:bg-primary/10 cursor-pointer rounded p-1.5 text-xs ${
                index === currentIndex ? "bg-primary/10" : ""
              }`}
              onClick={() => handleMessageClick(message.id, index)}
            >
              <div className="flex gap-1.5">
                <PinIcon className="text-muted-foreground mt-0.5 h-3 w-3 shrink-0" />
                <div className="min-w-0 flex-1">
                  <div className="flex justify-between">
                    <span className="truncate font-medium">
                      {message.author.name}
                    </span>
                    <span className="text-muted-foreground ml-1 shrink-0 text-[10px]">
                      {formatMessageDate(message)}
                    </span>
                  </div>
                  <p className="line-clamp-2">{message.content}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </ScrollArea>
    </div>
  );

  return (
    <div className="px-2 pt-2">
      {expanded ? renderExpandedView() : renderCollapsedView()}
    </div>
  );
}
