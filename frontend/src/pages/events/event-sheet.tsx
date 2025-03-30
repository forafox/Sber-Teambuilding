import { useState } from "react";
import { Link, useNavigate } from "@tanstack/react-router";
import { Button } from "@/components/ui/button";
import {
  Sheet,
  SheetContent,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import {
  MenuIcon,
  ListChecks,
  DollarSign,
  MessageCircleIcon,
  LogOut,
  HomeIcon,
} from "lucide-react";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getMeQueryOptions } from "@/api/get-me";
import { useLogOutMutation } from "@/api/log-out";

interface EventSheetProps {
  eventId: number;
}

export function EventSheet({ eventId }: EventSheetProps) {
  const [isOpen, setOpen] = useState(false);
  const { data: principal } = useSuspenseQuery(getMeQueryOptions());
  const onClick = () => {
    setOpen(false);
  };
  const navigate = useNavigate();
  const { mutate: logOut } = useLogOutMutation();
  return (
    <Sheet open={isOpen} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button variant="outline" size="icon" className="size-8">
          <MenuIcon />
        </Button>
      </SheetTrigger>
      <SheetContent>
        <SheetHeader>
          <SheetTitle>Мероприятие</SheetTitle>
        </SheetHeader>
        <div className="flex flex-col items-start gap-2 px-4">
          <Button
            variant="link"
            className="!px-0 data-[status=active]:underline"
            asChild
            onClick={onClick}
          >
            <Link from="/events/$eventId" to="/home">
              <HomeIcon />
              Главная
            </Link>
          </Button>
          <Button
            variant="link"
            className="!px-0 data-[status=active]:underline"
            asChild
            onClick={onClick}
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
            onClick={onClick}
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
            onClick={onClick}
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
        </div>
        <SheetFooter>
          <div className="flex w-full items-center justify-between px-4">
            <div className="flex flex-col">
              <p className="text-sm font-medium">{principal.name}</p>
              <p className="text-muted-foreground text-sm">
                @{principal.username}
              </p>
            </div>
            <Button
              variant="outline"
              onClick={() => {
                logOut(undefined, {
                  onSuccess: () => {
                    navigate({ to: "/" });
                  },
                });
              }}
              size="icon"
            >
              <LogOut />
            </Button>
          </div>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
