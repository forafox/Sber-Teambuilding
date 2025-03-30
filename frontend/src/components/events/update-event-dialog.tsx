import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../ui/dialog";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useUpdateEventMutation } from "@/api/update-event";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "../ui/form";
import { Input } from "../ui/input";
import { SelectUsers } from "../users/select-users";
import { Button } from "../ui/button";
import { Suspense } from "react";
import { DateTimePicker } from "../ui/date-time-picker";
import { Event, eventSchema } from "@/api/get-event";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { CheckCircle2, Clock } from "lucide-react";

type Props = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  defaultEvent: Event;
};

const schema = eventSchema.omit({ author: true, chatId: true });

// Определяем статусы мероприятий с иконками и переводами
const eventStatuses = [
  {
    value: "IN_PROGRESS",
    label: "В процессе",
    icon: Clock,
  },
  {
    value: "DONE",
    label: "Завершено",
    icon: CheckCircle2,
  },
];

export function UpdateEventDialog({ open, onOpenChange, defaultEvent }: Props) {
  const { mutate, error, isPending } = useUpdateEventMutation();
  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      id: defaultEvent.id,
      title: defaultEvent.title,
      date: defaultEvent.date,
      participants: defaultEvent.participants,
      status: defaultEvent.status || "IN_PROGRESS",
      description: defaultEvent.description,
      location: defaultEvent.location,
    },
  });

  function onSubmit(data: z.infer<typeof schema>) {
    mutate(
      {
        eventId: defaultEvent.id,
        date: data.date.toISOString(),
        title: data.title,
        participants: data.participants.map((it) => it.id),
        status: data.status,
        description: data.description,
        location: data.location,
      },
      {
        onSuccess: () => {
          onOpenChange(false);
        },
      },
    );
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(open) => {
        if (!open) {
          form.reset();
        }
        onOpenChange(open);
      }}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Редактирование события</DialogTitle>
          <DialogDescription>
            Вы можете изменить название события, дату, статус, участников, цель
            и место проведения.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form
            id="update-event-form"
            onSubmit={form.handleSubmit(onSubmit, (e) => console.error(e))}
            className="space-y-4 [&>*]:grid [&>*]:gap-2"
          >
            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Название события</FormLabel>
                  <FormControl>
                    <Input {...field} />
                  </FormControl>
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="date"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Дата события</FormLabel>
                  <FormControl>
                    <DateTimePicker
                      value={field.value}
                      onChange={field.onChange}
                    />
                  </FormControl>
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="status"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Статус мероприятия</FormLabel>
                  <Select
                    onValueChange={field.onChange}
                    defaultValue={field.value}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="Выберите статус" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {eventStatuses.map((status) => (
                        <SelectItem key={status.value} value={status.value}>
                          <div className="flex items-center gap-2">
                            <status.icon className="h-4 w-4" />
                            <span>{status.label}</span>
                          </div>
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
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
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Цель мероприятия</FormLabel>
                  <FormControl>
                    <Input placeholder="Укажите цель" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="participants"
              render={({ field: { value, onChange } }) => (
                <FormItem>
                  <FormLabel>Участники</FormLabel>
                  <FormControl>
                    <Suspense fallback={<></>}>
                      <SelectUsers value={value} onChange={onChange} />
                    </Suspense>
                  </FormControl>
                </FormItem>
              )}
            />

            {error && <FormMessage>{error.message}</FormMessage>}
            <FormMessage />
          </form>
        </Form>
        <DialogFooter>
          <Button type="submit" disabled={isPending} form="update-event-form">
            Обновить
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
