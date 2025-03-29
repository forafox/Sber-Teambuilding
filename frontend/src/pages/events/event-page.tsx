import { Link, Outlet, useNavigate } from "@tanstack/react-router";
import { Button } from "@/components/ui/button";
import { DollarSign, ListChecks, MessageCircleIcon } from "lucide-react";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getEventQueryOptions } from "@/api/get-event";
import { getMeQueryOptions } from "@/api/get-me";
import { PrincipalProfile } from "@/components/principal/principal-profile";
import { EventSheet } from "./event-sheet";
import { useLogOutMutation } from "@/api/log-out";

interface EventPageProps {
  eventId: number;
}

export function EventPage({ eventId }: EventPageProps) {
  const { data: event } = useSuspenseQuery(getEventQueryOptions(eventId));
  const { data: principal } = useSuspenseQuery(getMeQueryOptions());
  const navigate = useNavigate();
  const { mutate: logOut } = useLogOutMutation();

  return (
    <>
      <header className="border-b">
        <div className="mx-auto flex h-12 flex-row items-center justify-between px-4 md:container">
          <nav className="flex items-center justify-between">
            <Link
              to="/events/$eventId/tasks"
              params={{ eventId: eventId.toString() }}
            >
              <h1 className="text-lg font-semibold">{event.title}</h1>
            </Link>
          </nav>
          <div className="md:hidden">
            <EventSheet eventId={eventId} />
          </div>
          <div className="hidden items-center gap-4 md:flex">
            <Button
              variant="link"
              className="!px-0 data-[status=active]:underline"
              asChild
            >
              <Link
                from="/events/$eventId"
                to="/events/$eventId/tasks"
                params={{ eventId: eventId.toString() }}
              >
                <ListChecks />
                Задачи
              </Link>
            </Button>
            <Button
              variant="link"
              className="!px-0 data-[status=active]:underline"
              asChild
            >
              <Link
                from="/events/$eventId"
                to="/events/$eventId/expenses"
                params={{ eventId: eventId.toString() }}
              >
                <DollarSign />
                Смета
              </Link>
            </Button>
            <Button
              variant="link"
              className="!px-0 data-[status=active]:underline"
              asChild
            >
              <Link
                from="/events/$eventId"
                to="/events/$eventId/chat"
                params={{ eventId: eventId.toString() }}
              >
                <MessageCircleIcon />
                Чат
              </Link>
            </Button>
            <PrincipalProfile
              principal={principal}
              onLogout={() => {
                logOut(undefined, {
                  onSuccess: () => {
                    navigate({ to: "/" });
                  },
                });
              }}
            />
          </div>
        </div>
      </header>
      <Outlet />
    </>
  );
}
