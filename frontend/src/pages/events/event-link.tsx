import { useCreateInviteTokenMutation } from "@/api/invite";
import { Button } from "@/components/ui/button";
import { ClipboardCheck, ClipboardCopy } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";

const HOST = window.location.origin;

export function EventLink({
  eventId,
  title,
}: {
  eventId: number;
  title: string;
}) {
  const { mutate } = useCreateInviteTokenMutation();
  const [isCopied, setIsCopied] = useState(false);
  return (
    <Button
      size="icon"
      onClick={() =>
        mutate(eventId, {
          onSuccess: (token) => {
            setIsCopied(true);
            const link = `${HOST}/token?token=${token}&title=${encodeURIComponent(
              title,
            )}`;
            navigator.clipboard.writeText(link);
            toast.success("Пригласительная ссылка скопирована в буфер обмена");
          },
        })
      }
    >
      {isCopied ? <ClipboardCheck /> : <ClipboardCopy />}
    </Button>
  );
}
