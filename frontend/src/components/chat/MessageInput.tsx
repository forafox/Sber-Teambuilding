import { useState, RefObject } from "react";
import { Button } from "@/components/ui/button";
import { SendIcon, XIcon } from "lucide-react";
import { useSendMessage } from "@/api/send-message";
import { Message } from "@/api/get-chat";

type MessageInputProps = {
  chatId: number;
  replyToMessage?: Message | null;
  onCancelReply?: () => void;
  inputRef?: RefObject<HTMLInputElement>;
};

export function MessageInput({
  chatId,
  replyToMessage,
  onCancelReply,
  inputRef,
}: MessageInputProps) {
  const [message, setMessage] = useState("");
  const sendMessage = useSendMessage(chatId);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (message.trim() && !sendMessage.isPending) {
      sendMessage.mutate({
        content: message.trim(),
        replyToMessageId: replyToMessage?.id,
        pinned: false,
      });
      setMessage("");
      if (onCancelReply && replyToMessage) {
        onCancelReply();
      }
    }
  };

  return (
    <div className="border-t">
      {replyToMessage && (
        <div className="bg-muted/30 flex items-center gap-2 border-b p-2">
          <div className="flex flex-1 items-center gap-2">
            <div className="bg-primary h-4 w-1 rounded-full"></div>
            <div className="flex-1">
              <div className="text-sm font-semibold">
                {replyToMessage.author.name}
              </div>
              <div className="text-muted-foreground truncate text-xs">
                {replyToMessage.content.length > 50
                  ? replyToMessage.content.substring(0, 50) + "..."
                  : replyToMessage.content}
              </div>
            </div>
          </div>
          {onCancelReply && (
            <Button
              variant="ghost"
              size="icon"
              onClick={onCancelReply}
              className="h-8 w-8 rounded-full"
            >
              <XIcon className="h-4 w-4" />
            </Button>
          )}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="flex w-full items-center gap-2 p-3"
      >
        <input
          ref={inputRef}
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder={
            replyToMessage ? "Напишите ответ..." : "Введите сообщение..."
          }
          className="flex-1 appearance-none border-none bg-transparent px-3 py-2 focus:outline-none"
        />
        <Button
          type="submit"
          disabled={!message.trim() || sendMessage.isPending}
          size="icon"
          className="h-10 w-10 rounded-full"
          variant="default"
        >
          {sendMessage.isPending ? (
            <span className="h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent" />
          ) : (
            <SendIcon className="h-4 w-4" />
          )}
        </Button>
      </form>
    </div>
  );
}
