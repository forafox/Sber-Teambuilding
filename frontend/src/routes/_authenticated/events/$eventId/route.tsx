import { createFileRoute } from "@tanstack/react-router";
import { getEventQueryOptions } from "@/api/get-event";
import { getMeQueryOptions } from "@/api/get-me";
import { EventPage } from "@/pages/events/event-page";

export const Route = createFileRoute("/_authenticated/events/$eventId")({
  component: EventDetails,
  loader: async ({ params, context }) => {
    const [principal, data] = await Promise.all([
      context.queryClient.fetchQuery(getMeQueryOptions()),
      context.queryClient.fetchQuery(
        getEventQueryOptions(Number(params.eventId)),
      ),
    ]);
    return { principal, data };
  },
});

function EventDetails() {
  const params = Route.useParams();
  const eventId = Number(params.eventId);
  return <EventPage eventId={eventId} />;
}
