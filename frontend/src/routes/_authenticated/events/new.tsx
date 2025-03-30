import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { Button } from "@/components/ui/button";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ArrowLeft, Calendar } from "lucide-react";
import { useCreateEventMutation } from "@/api/create-event";
import { SelectUsers } from "@/components/users/select-users";
import { toast } from "sonner";
import { AxiosError } from "axios";
import { userSchema } from "@/api/get-users";
import { DateTimePicker } from "@/components/ui/date-time-picker";

export const Route = createFileRoute("/_authenticated/events/new")({
  component: NewEvent,
});

const eventSchema = z.object({
  title: z
    .string()
    .min(3, { message: "Название должно содержать минимум 3 символа" }),
  date: z.coerce.date(),
  participants: z.array(userSchema),
  description: z
    .string()
    .nullish()
    .transform((it) => it ?? undefined),
  location: z
    .string()
    .nullish()
    .transform((it) => it ?? undefined),
});

function NewEvent() {
  const navigate = useNavigate();
  const form = useForm<z.infer<typeof eventSchema>>({
    resolver: zodResolver(eventSchema),
    defaultValues: {
      title: "",
      date: new Date(),
      location: "",
      participants: [],
    },
  });

  const { mutateAsync, isPending, error } = useCreateEventMutation();

  const handleSubmit = async (data: z.infer<typeof eventSchema>) => {
    try {
      const event = await mutateAsync({
        title: data.title,
        date: new Date(data.date).toISOString(),
        participants: data.participants.map((participant) => participant.id),
        status: "IN_PROGRESS",
        description: data.description,
        location: data.location,
      });
      navigate({
        to: "/events/$eventId/tasks",
        params: { eventId: String(event.id) },
      });
    } catch (err) {
      const errorMessage = err instanceof AxiosError ? err.message : undefined;
      toast.error("Ошибка при создании мероприятия", {
        description: errorMessage,
      });
    }
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <Button variant="ghost" className="mb-6" asChild>
        <a
          href="/"
          onClick={(e) => {
            e.preventDefault();
            window.history.back();
          }}
        >
          <ArrowLeft className="mr-2 h-4 w-4" />
          Назад
        </a>
      </Button>

      <Card className="mx-auto max-w-md">
        <CardHeader>
          <CardTitle>Создать новое мероприятие</CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form
              onSubmit={form.handleSubmit(handleSubmit)}
              className="space-y-6"
            >
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Название мероприятия</FormLabel>
                    <FormControl>
                      <Input placeholder="Введите название" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="date"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Дата мероприятия</FormLabel>
                    <FormControl>
                      <div className="relative">
                        <Calendar className="text-muted-foreground absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2 transform" />
                        <DateTimePicker
                          value={field.value}
                          onChange={field.onChange}
                        />
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="location"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Место проведения</FormLabel>
                    <FormControl>
                      <Input placeholder="Введите координаты" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="participants"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Участники</FormLabel>
                    <FormControl>
                      <SelectUsers
                        value={field.value}
                        onChange={field.onChange}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {error && <p className="text-sm text-red-500">{error.message}</p>}

              <Button type="submit" className="w-full" disabled={isPending}>
                {isPending ? "Создание..." : "Создать мероприятие"}
              </Button>
            </form>
          </Form>
        </CardContent>
      </Card>
    </div>
  );
}
