import { getMeQueryOptions } from "@/api/get-me";
import { getEventsQueryOptions } from "@/api/get-events";
import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useSuspenseQuery } from "@tanstack/react-query";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
  CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  BotIcon,
  CalendarIcon,
  LogOutIcon,
  MailIcon,
  PlusIcon,
  UserIcon,
  SendIcon,
} from "lucide-react";
import { format } from "date-fns";
import { ru } from "date-fns/locale";
import { CreateEventPromptDialog } from "@/components/events/create-event-prompt-dialog";
import { useState } from "react";
import Fuse from "fuse.js";
import { Input } from "@/components/ui/input";
import { clearTokens } from "@/api/tokens";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { useSelectTelegramUsername } from "@/api/telegram-username";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { toast } from "sonner";

type fuseResult = {
  title: string;
  id: number;
  author: {
    name: string;
    id: number;
    role: "USER" | "ADMIN" | "GUEST" | "HOUSE_OWNER";
    email: string;
    username: string;
  };
  date: Date;
  participants: {
    name: string;
    id: number;
    role: "USER" | "ADMIN" | "GUEST" | "HOUSE_OWNER";
    email: string;
    username: string;
  }[];
  chatId: number;
  description?: string | undefined;
  location?: string | undefined;
}[];

export const Route = createFileRoute("/_authenticated/home")({
  component: RouteComponent,
  beforeLoad: async ({ context }) => {
    await Promise.all([
      context.queryClient.fetchQuery(getMeQueryOptions()),
      context.queryClient.fetchQuery(getEventsQueryOptions()),
    ]);
  },
});

function RouteComponent() {
  const { data: userData } = useSuspenseQuery(getMeQueryOptions());
  const { data: eventsData } = useSuspenseQuery(getEventsQueryOptions());
  const navigate = useNavigate();
  const [createOpen, setCreateOpen] = useState(false);
  const [logoutDialogOpen, setLogoutDialogOpen] = useState(false);
  const [telegramDialogOpen, setTelegramDialogOpen] = useState(false);
  const telegramMutation = useSelectTelegramUsername();

  const [activeEvents, setActiveEvents] = useState(eventsData || []);
  const userInfo = userData;

  const telegramSchema = z.object({
    username: z.string().min(5, "Минимум 5 символов"),
  });

  const telegramForm = useForm<z.infer<typeof telegramSchema>>({
    resolver: zodResolver(telegramSchema),
    defaultValues: {
      username: "",
    },
  });

  const onSubmitTelegram = (values: z.infer<typeof telegramSchema>) => {
    telegramMutation.mutate(values.username, {
      onSuccess: () => {
        toast.success("Telegram подключен");
        setTelegramDialogOpen(false);
        telegramForm.reset();
      },
      onError: () => {
        toast.error("Не удалось подключить Telegram. Попробуйте еще раз.");
      },
    });
  };

  const fuse = new Fuse(eventsData, {
    keys: ["title"],
    includeScore: false,
    threshold: 0.3,
  });

  const handleSearchEvent = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    if (e.target.value == "") {
      setActiveEvents(eventsData);
    } else {
      const newArray = fuse.search(e.target.value);
      const items: fuseResult = [];
      newArray.forEach((elem) => items.push(elem.item));
      setActiveEvents(items);
    }
  };

  const handleCreateEvent = () => {
    navigate({ to: "/events/new" });
  };

  const handleLogout = () => {
    clearTokens();
    navigate({ to: "/sign-in" });
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8 flex items-center justify-between">
        <h1 className="text-3xl font-bold">Личный кабинет</h1>
        <Button
          variant="outline"
          className="flex items-center gap-2"
          onClick={() => setLogoutDialogOpen(true)}
        >
          <LogOutIcon className="h-4 w-4" />
          Выйти
        </Button>
      </div>

      {/* User Information Card */}
      <Card className="mb-8">
        <CardHeader>
          <CardTitle>Информация о пользователе</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="flex items-center gap-3">
              <UserIcon className="text-muted-foreground size-5" />
              <div>
                <p className="text-muted-foreground text-sm">Имя</p>
                <p className="font-medium">{userInfo?.name}</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <UserIcon className="text-muted-foreground size-5" />
              <div>
                <p className="text-muted-foreground text-sm">
                  Имя пользователя
                </p>
                <p className="font-medium">{userInfo?.username}</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <MailIcon className="text-muted-foreground size-5" />
              <div>
                <p className="text-muted-foreground text-sm">Email</p>
                <p className="font-medium">{userInfo?.email}</p>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <SendIcon className="text-muted-foreground size-5" />
                <div>
                  <p className="text-muted-foreground text-sm">Telegram</p>
                  <p className="font-medium">
                    {userInfo?.telegramUsername || "Не подключен"}
                  </p>
                </div>
              </div>
              <Button size="sm" onClick={() => setTelegramDialogOpen(true)}>
                {userInfo?.telegramUsername ? "Изменить" : "Подключить"}
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Active Events Section */}
      <div className="mb-8">
        <div className="mb-4 flex flex-col items-start gap-2 md:flex-row md:items-center md:justify-between">
          <h2 className="text-2xl font-bold">Активные мероприятия</h2>
          <Input
            onChange={handleSearchEvent}
            placeholder="Найти мероприятие"
            className="max-w-[300px] min-w-36"
          />
          <div className="flex flex-row-reverse items-center gap-4 md:flex-row">
            <Button variant="outline" onClick={handleCreateEvent}>
              <PlusIcon className="mr-2 h-4 w-4" />
              Создать мероприятие
            </Button>
            <Button asChild>
              <Link to="/templates">Шаблоны</Link>
            </Button>
            <Button
              onClick={() => setCreateOpen(true)}
              className="group relative overflow-hidden border-0 bg-gradient-to-r from-blue-600 to-purple-600 shadow-[0_0_15px_rgba(56,189,248,0.5)] transition-shadow duration-300 hover:from-blue-700 hover:to-purple-700 hover:shadow-[0_0_20px_rgba(168,85,247,0.7)]"
            >
              <span className="absolute inset-0 bg-[rgba(255,255,255,0.2)] opacity-0 blur-md transition-opacity duration-300 group-hover:opacity-70"></span>
              <span className="relative z-10 flex items-center justify-center">
                <BotIcon className="mr-2 size-5" />
                <span className="bg-gradient-to-r from-white to-blue-100 bg-clip-text font-medium text-transparent">
                  Giga
                </span>
                <span className="font-bold text-white">Chat</span>
              </span>
            </Button>
          </div>
        </div>

        {activeEvents.length === 0 ? (
          <div className="rounded-lg bg-gray-50 py-12 text-center">
            <CalendarIcon className="mx-auto mb-4 h-12 w-12 text-gray-400" />
            <h3 className="mb-2 text-lg font-medium text-gray-900">
              Нет активных мероприятий
            </h3>
            <p className="text-muted-foreground mb-4">
              Создайте новое мероприятие, чтобы начать
            </p>
            <Button onClick={handleCreateEvent}>
              <PlusIcon className="mr-2 h-4 w-4" />
              Создать мероприятие
            </Button>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            {activeEvents.map((event) => (
              <Card key={event.id} className="h-full">
                <CardHeader>
                  <CardTitle>{event.title}</CardTitle>
                  <CardDescription>
                    {format(new Date(event.date), "d MMMM yyyy", {
                      locale: ru,
                    })}
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <p className="text-muted-foreground mb-2 text-sm">
                    Организатор: {event.author.name}
                  </p>
                  <p className="text-muted-foreground text-sm">
                    Участников: {event.participants.length}
                  </p>
                  {event.location && (
                    <p className="text-muted-foreground mb-2 text-sm">
                      Место проведения: {event.location}
                    </p>
                  )}
                </CardContent>
                <CardFooter>
                  <Button variant="outline" asChild className="w-full">
                    <Link
                      to="/events/$eventId/tasks"
                      params={{ eventId: String(event.id) }}
                    >
                      Просмотр
                    </Link>
                  </Button>
                </CardFooter>
              </Card>
            ))}
          </div>
        )}
      </div>

      {/* Confirmation Dialog for Logout */}
      <Dialog open={logoutDialogOpen} onOpenChange={setLogoutDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Выход из аккаунта</DialogTitle>
            <DialogDescription>
              Вы уверены, что хотите выйти из личного кабинета?
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="flex justify-end gap-2 sm:justify-end">
            <Button
              variant="outline"
              onClick={() => setLogoutDialogOpen(false)}
            >
              Отмена
            </Button>
            <Button variant="default" onClick={handleLogout}>
              Выйти
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Create Event Dialog */}
      <CreateEventPromptDialog open={createOpen} onOpenChange={setCreateOpen} />

      {/* Telegram Username Dialog */}
      <Dialog open={telegramDialogOpen} onOpenChange={setTelegramDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Подключение Telegram</DialogTitle>
            <DialogDescription>
              Введите ваше имя пользователя в Telegram без символа @
            </DialogDescription>
          </DialogHeader>
          <Form {...telegramForm}>
            <form
              onSubmit={telegramForm.handleSubmit(onSubmitTelegram)}
              className="space-y-4"
            >
              <FormField
                control={telegramForm.control}
                name="username"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Имя пользователя Telegram</FormLabel>
                    <FormControl>
                      <Input placeholder="username" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <DialogFooter className="flex justify-end gap-2 sm:justify-end">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setTelegramDialogOpen(false)}
                >
                  Отмена
                </Button>
                <Button type="submit" disabled={telegramMutation.isPending}>
                  {telegramMutation.isPending ? "Подключение..." : "Подключить"}
                </Button>
              </DialogFooter>
            </form>
          </Form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
