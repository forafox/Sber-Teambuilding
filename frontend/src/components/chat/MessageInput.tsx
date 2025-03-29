import { useState } from "react";
import { Button } from "@/components/ui/button";
import { SendIcon, XIcon } from "lucide-react";
import { useSendMessage } from "@/api/send-message";
import { Message } from "@/api/get-chat";

type MessageInputProps = {
  chatId: number;
  replyToMessage?: Message | null;
  onCancelReply?: () => void;
};

export function MessageInput({
  chatId,
  replyToMessage,
  onCancelReply,
}: MessageInputProps) {
  const [message, setMessage] = useState("");
  const sendMessage = useSendMessage(chatId);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!message.trim()) return;

    try {
      await sendMessage.mutateAsync({
        content: message.trim(),
        replyToMessageId: replyToMessage?.id,
      });
      setMessage("");
      if (onCancelReply) {
        onCancelReply();
      }
    } catch (error) {
      console.error("Failed to send message:", error);
    }
  };

  return (
    <div className="border-t">
      {replyToMessage && (
        <div className="border-primary bg-muted/10 flex items-center gap-3 border-l-2 p-2 pl-3">
          <div className="flex-1">
            <div className="flex items-center">
              <div className="text-primary mb-0.5 text-sm font-medium">
                {replyToMessage.author.name}
              </div>
            </div>
            <div className="text-muted-foreground max-w-[calc(100%-40px)] truncate text-xs">
              {replyToMessage.content}
            </div>
          </div>
          <Button
            variant="ghost"
            size="icon"
            className="h-6 w-6 rounded-full"
            onClick={onCancelReply}
          >
            <XIcon className="h-4 w-4" />
          </Button>
        </div>
      )}
      <form
        onSubmit={handleSubmit}
        className="flex w-full items-center gap-2 p-3"
      >
        <input
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
