import { useState } from "react";
import { Button } from "@/components/ui/button";
import { SendIcon } from "lucide-react";
import { useSendMessage } from "@/api/send-message";

type MessageInputProps = {
  chatId: number;
};

export function MessageInput({ chatId }: MessageInputProps) {
  const [message, setMessage] = useState("");
  const sendMessage = useSendMessage(chatId);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!message.trim()) return;

    try {
      await sendMessage.mutateAsync({ content: message.trim() });
      setMessage("");
    } catch (error) {
      console.error("Failed to send message:", error);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="flex w-full items-center gap-2 border-t p-3"
    >
      <input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        placeholder="Введите сообщение..."
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
  );
}
