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
import { useUpdateTaskMutation } from "@/api/update-task";
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
import { Task } from "@/api/get-tasks";
import { taskSchema } from "@/api/get-tasks";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getEventQueryOptions } from "@/api/get-event";

type Props = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  defaultTask: Task;
  eventId: number;
};

const schema = taskSchema.omit({ id: true, author: true });

export function UpdateTaskDialog({
  open,
  onOpenChange,
  defaultTask,
  eventId,
}: Props) {
  const { data: event } = useSuspenseQuery(getEventQueryOptions(eventId));
  const { mutate, error, isPending } = useUpdateTaskMutation();
  const form = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      title: defaultTask.title,
      assignee: defaultTask.assignee,
      description: defaultTask.description,
      expenses: defaultTask.expenses,
      url: defaultTask.url,
    },
  });

  function onSubmit(data: z.infer<typeof schema>) {
    mutate(
      {
        eventId,
        taskId: defaultTask.id,
        assigneeUsername: data.assignee?.username,
        title: data.title,
        description: data.description ?? "",
        expenses: data?.expenses ?? undefined,
        url: data?.url ?? undefined,
        status: data.status,
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
          <DialogTitle>Редактирование задачи</DialogTitle>
          <DialogDescription>
            Вы можете изменить название задачи, описание, исполнителей и
            денежную сумму.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form
            id="update-task-form"
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
                  <FormLabel>Исполнитель</FormLabel>
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
            <FormField
              control={form.control}
              name="url"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Ссылка</FormLabel>
                  <FormControl>
                    <Input type="url" {...field} />
                  </FormControl>
                </FormItem>
              )}
            />
            {error && <FormMessage>{error.message}</FormMessage>}
            <FormMessage />
          </form>
        </Form>
        <DialogFooter>
          <Button type="submit" disabled={isPending} form="update-task-form">
            Обновить
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
