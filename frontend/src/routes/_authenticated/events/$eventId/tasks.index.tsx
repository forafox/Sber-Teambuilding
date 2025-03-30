import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card";
import { createFileRoute } from "@tanstack/react-router";
import { Calendar, Pencil } from "lucide-react";
import { Button } from "@/components/ui/button";
import { getMeQueryOptions } from "@/api/get-me";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getEventQueryOptions } from "@/api/get-event";
import { useState } from "react";
import { UpdateEventDialog } from "@/components/events/update-event-dialog";
import { EventTasks } from "@/components/tasks/event-tasks";
import { UserHoverCard } from "@/components/user/user-hover-card";
import { MapDialog } from "@/components/map/map-dialog";

export const Route = createFileRoute("/_authenticated/events/$eventId/tasks/")({
  component: RouteComponent,
});

function RouteComponent() {
  const params = Route.useParams();
  const eventId = Number(params.eventId);
  const { data: event } = useSuspenseQuery(getEventQueryOptions(eventId));
  const { data: me } = useSuspenseQuery(getMeQueryOptions());
  const [updateOpen, setUpdateOpen] = useState(false);
  const [mapOpen, setMapOpen] = useState(false);

  const handleOpenMap = () => {
    // setMapOpen(true);
  };

  const isAuthor = me?.id === event.author.id;
  const formattedDate = event.date.toLocaleString("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

  return (
    <div className="mx-auto mt-8 grid gap-8 px-4 pb-4 md:container">
      <Card>
        <CardHeader className="pb-3">
          <div className="flex items-start justify-between">
            <div>
              <CardTitle className="text-2xl font-bold">
                {event.title}
              </CardTitle>
              <CardDescription className="mt-2 flex items-center">
                <Calendar className="mr-2 h-4 w-4" />
                {formattedDate}
              </CardDescription>
            </div>
            {isAuthor && (
              <Button variant="outline" onClick={() => setUpdateOpen(true)}>
                <Pencil className="mr-2 h-4 w-4" />
                Изменить
              </Button>
            )}
          </div>
        </CardHeader>
        <CardContent>
          <div className="mb-6">
            <h3 className="mb-2 text-lg font-semibold">Организатор</h3>
            <div className="flex items-center gap-2">
              <UserHoverCard user={event.author} />
            </div>
          </div>

          <div>
            <div className="mb-3 flex items-center justify-between">
              <h3 className="text-lg font-semibold">
                Участники ({event.participants.length})
              </h3>
            </div>

            <div className="flex flex-row flex-wrap gap-6">
              {event.participants.map((participant) => (
                <UserHoverCard key={participant.id} user={participant} />
              ))}
              {event.participants.length === 0 && (
                <p className="text-muted-foreground col-span-2 py-4 text-center">
                  Нет участников
                </p>
              )}
            </div>

            {event.location && (
              <p className="text-muted-foreground mb-2 text-sm">
                Место проведения:{" "}
                <Button onClick={handleOpenMap}>{event.location}</Button>
              </p>
            )}

            {event.description && (
              <p className="text-muted-foreground mb-2 text-sm">
                Цель мероприятия:{" "}
                <Button onClick={handleOpenMap}>{event.description}</Button>
              </p>
            )}
          </div>
        </CardContent>
        <UpdateEventDialog
          open={updateOpen}
          onOpenChange={setUpdateOpen}
          defaultEvent={event}
        />
      </Card>
      <EventTasks eventId={event.id} />
      <MapDialog
        open={mapOpen}
        onOpenChange={setMapOpen}
        position={[59.965112, 30.28907]}
      />
    </div>
  );
}
