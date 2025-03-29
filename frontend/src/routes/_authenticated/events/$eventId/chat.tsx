import { createFileRoute } from "@tanstack/react-router";
import { EventChat } from "@/pages/events/event-chat";

export const Route = createFileRoute("/_authenticated/events/$eventId/chat")({
  component: ChatPage,
});

function ChatPage() {
  const { eventId } = Route.useParams();
  return <EventChat eventId={eventId} />;
}
