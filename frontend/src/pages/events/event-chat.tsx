import { ChatContainer } from "@/components/chat/ChatContainer";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getEventQueryOptions } from "@/api/get-event";

interface EventChatProps {
  eventId: string;
}

export function EventChat({ eventId }: EventChatProps) {
  const { data: event } = useSuspenseQuery(
    getEventQueryOptions(Number(eventId)),
  );

  if (!event) {
    return <div>Event not found</div>;
  }

  return (
    <div className="container mx-auto flex h-[calc(100vh-4rem)] flex-col px-4 py-6">
      <h1 className="mb-4 text-2xl font-bold">Чат мероприятия</h1>
      <div className="relative h-full flex-1 overflow-hidden rounded-lg border">
        <ChatContainer chatId={event?.chatId} />
      </div>
    </div>
  );
}
