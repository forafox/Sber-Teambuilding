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
import { useCreateTaskMutation } from "@/api/create-task";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "../ui/form";
import { Input } from "../ui/input";
import { SelectUser } from "../user/select-user";
import { Button } from "../ui/button";
import { Suspense } from "react";
import { taskSchema } from "@/api/get-tasks";
import { getEventQueryOptions } from "@/api/get-event";
import { useSuspenseQuery } from "@tanstack/react-query";

type Props = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  eventId: number;
};

const schema = taskSchema.omit({ status: true, author: true, id: true });

export function CreateTaskDialog({ open, onOpenChange, eventId }: Props) {
  const { data: event } = useSuspenseQuery(getEventQueryOptions(eventId));
  const { mutate, error, isPending } = useCreateTaskMutation();
  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      title: "",
      expenses: undefined,
      description: "",
      assignee: undefined,
    },
  });

  function onSubmit(data: z.infer<typeof schema>) {
    function expenses() {
      if (data.expenses) return Number(data.expenses);
      else return undefined;
    }
    mutate(
      {
        eventId: eventId,
        title: data.title,
        assigneeUsername: data.assignee?.username,
        description: data.description,
        expenses: expenses(),
        status: "IN_PROGRESS",
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
          <DialogTitle>Создание задачи</DialogTitle>
          <DialogDescription>
            Вы можете изменить название задачи, описание, исполнителей и
            денежную сумму.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form
            id="create-task-form"
            onSubmit={form.handleSubmit(onSubmit, (e) => console.error(e))}
            className="space-y-4 [&>*]:grid [&>*]:gap-2"
          >
            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Название задачи</FormLabel>
                  <FormControl>
                    <Input {...field} />
                  </FormControl>
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Описание задачи</FormLabel>
                  <FormControl>
                    <Input {...field} />
                  </FormControl>
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="expenses"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Сумма для задачи</FormLabel>
                  <FormControl>
                    <Input {...field} />
                  </FormControl>
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="assignee"
              render={({ field: { value, onChange } }) => (
                <FormItem>
                  <FormLabel>Участники</FormLabel>
                  <FormControl>
                    <Suspense fallback={<></>}>
                      <SelectUser
                        value={value}
                        onChange={onChange}
                        participants={event.participants}
                      />
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
          <Button type="submit" disabled={isPending} form="create-task-form">
            Создать
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
