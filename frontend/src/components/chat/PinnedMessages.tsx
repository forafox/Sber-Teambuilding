import { Message } from "@/api/get-chat";
import { format } from "date-fns";
import { X, Pin } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";

type PinnedMessagesProps = {
  messages: Message[];
  onUnpin?: (messageId: number) => void;
  onMessageClick: (messageId: number) => void;
};

export function PinnedMessages({
  messages,
  onUnpin,
  onMessageClick,
}: PinnedMessagesProps) {
  // Если нет закрепленных сообщений, не отображаем компонент
  if (!messages.length) return null;

  const getInitials = (name: string) => {
    return name
      .split(" ")
      .map((n) => n[0])
      .join("")
      .toUpperCase();
  };

  return (
    <div className="bg-muted/20 border-b mb-2 px-4 py-2">
      <div className="flex items-center justify-between mb-1">
        <div className="flex items-center gap-1 text-sm font-medium">
          <Pin className="h-4 w-4" />
          <span>Закрепленные сообщения</span>
        </div>
      </div>
      <div className="max-h-[150px] overflow-y-auto space-y-2">
        {messages.map((message) => (
          <div
            key={message.id}
            className="flex items-start gap-2 p-2 rounded hover:bg-muted/30 cursor-pointer transition-colors"
            onClick={() => onMessageClick(message.id)}
          >
            <Avatar className="h-6 w-6">
              <AvatarImage src="" alt={message.author?.name || ""} />
              <AvatarFallback className="text-xs">
                {message.author?.name ? getInitials(message.author.name) : ""}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1 min-w-0 text-sm">
              <div className="flex items-center gap-2">
                <span className="font-medium">{message.author.name}</span>
                <span className="text-muted-foreground text-xs">
                  {format(message.timestamp, "dd.MM.yyyy HH:mm")}
                </span>
              </div>
              <p className="truncate">{message.content}</p>
            </div>
            {onUnpin && (
              <Button
                variant="ghost"
                size="icon"
                className="h-6 w-6"
                onClick={(e) => {
                  e.stopPropagation();
                  onUnpin(message.id);
                }}
              >
                <X className="h-4 w-4" />
              </Button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
} 