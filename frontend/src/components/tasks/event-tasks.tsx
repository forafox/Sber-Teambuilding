import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
  CardFooter,
} from "../ui/card";
import { getTasksQueryOptions, Task } from "@/api/get-tasks";
import { useSuspenseQuery } from "@tanstack/react-query";
import { Button } from "../ui/button";
import { PlusIcon, CheckIcon, PencilIcon, Trash2Icon } from "lucide-react";
import { useDeleteTaskMutation } from "@/api/delete-task";
import { useState } from "react";
import { UpdateTaskDialog } from "./update-task-dialog";
import { CreateTaskDialog } from "./create-task-dialog";
import { useUpdateTaskMutation } from "@/api/update-task";
import { Link } from "@tanstack/react-router";

type Props = {
  eventId: number;
};

function ChangeStatusButton({
  tasksStatus,
  onClick,
  status,
}: {
  tasksStatus: "IN_PROGRESS" | "DONE";
  onClick: () => void;
  status: "IN_PROGRESS" | "DONE";
}) {
  return (
    <button
      onClick={onClick}
      className={`${tasksStatus == status ? "text-primary bg-transparent" : "bg-primary text-primary-foreground hover:bg-primary/90 shadow-xs"} focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructiv inline-flex shrink-0 items-center justify-center rounded-md px-4 py-2 text-sm font-medium whitespace-nowrap transition-all outline-none focus-visible:ring-[3px]`}
    >
      {status == "IN_PROGRESS" ? "Активные задачи" : "Выполненные задачи"}
    </button>
  );
}

function DeleteTaskButton({
  taskId,
  eventId,
}: {
  taskId: number;
  eventId: number;
}): React.JSX.Element {
  const { mutate, error, isPending } = useDeleteTaskMutation();

  if (error) alert(`Something went wrong: ${error.message}`);

  const handleDeleteTask = (taskId: number) => {
    mutate({ taskId, eventId });
  };

  return (
    <Button onClick={() => handleDeleteTask(taskId)} disabled={isPending}>
      <Trash2Icon className="h-4 w-4" />
    </Button>
  );
}

function MakeDoneTask({ task, eventId }: { task: Task; eventId: number }) {
  const { mutate, error, isPending } = useUpdateTaskMutation();

  const handleMakeDoneTask = () => {
    mutate({ taskId: task.id, eventId, ...task, status: "DONE" });
  };

  if (error) alert(`Something went wrong: ${error.message}`);

  return (
    <Button onClick={handleMakeDoneTask} disabled={isPending}>
      <CheckIcon className="mr-2 h-4 w-4" />
      Выполнено
    </Button>
  );
}

function TaskCard({ task, eventId }: { task: Task; eventId: number }) {
  const [isOpenUpdateDialog, setIsOpenUpdateDialog] = useState(false);

  const handleEditTask = () => {
    setIsOpenUpdateDialog(true);
  };

  return (
    <Card key={task.id}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle>
            <Link
              to="/events/$eventId/tasks/$taskId"
              params={{
                eventId: String(eventId),
                taskId: String(task.id),
              }}
            >
              {task.title}
            </Link>
          </CardTitle>
        </div>
        {task.expenses && <CardDescription>{task.expenses}₽</CardDescription>}
      </CardHeader>
      <CardContent>
        <CardDescription>Автор: {task.author.name}</CardDescription>
      </CardContent>
      <CardContent>{task.description}</CardContent>
      {task.assignee && (
        <div className="bg-primary/15 mx-6 flex flex-col rounded-xl p-4">
          <CardContent>{task.assignee?.name}</CardContent>
          <CardContent>
            <CardDescription>{task.assignee?.email}</CardDescription>
          </CardContent>
        </div>
      )}
      <CardFooter className="flex gap-3">
        <Button
          onClick={handleEditTask}
          className="hover:bg-primary/10 border-inherit bg-transparent text-inherit"
        >
          <PencilIcon className="mr-2 h-4 w-4" />
          Изменить
        </Button>
        <DeleteTaskButton taskId={task.id} eventId={eventId}></DeleteTaskButton>
        {task.status == "IN_PROGRESS" && (
          <MakeDoneTask task={task} eventId={eventId} />
        )}
      </CardFooter>
      <UpdateTaskDialog
        open={isOpenUpdateDialog}
        onOpenChange={setIsOpenUpdateDialog}
        defaultTask={task}
        eventId={eventId}
      />
    </Card>
  );
}

export function EventTasks({ eventId }: Props) {
  const { data: tasksData } = useSuspenseQuery(getTasksQueryOptions(eventId));
  const [tasksStatus, setTasksStatus] = useState<"IN_PROGRESS" | "DONE">(
    "IN_PROGRESS",
  );
  const [isOpenCreateDialog, setIsOpenCreateDialog] = useState(false);
  const activeEvents = tasksData || [];

  const handleCreateTask = () => {
    setIsOpenCreateDialog(true);
  };

  return (
    <Card className="gap-y-3 px-6">
      <div className="mb-4 flex w-full items-center justify-between gap-4 max-[470px]:flex-col max-[470px]:justify-center">
        <h2 className="text-2xl font-bold">Задачи мероприятие</h2>
        <Button onClick={handleCreateTask}>
          <PlusIcon className="mr-2 h-4 w-4" />
          Создать задачу
        </Button>
      </div>
      <div className="flex flex-wrap items-center justify-center gap-4">
        <ChangeStatusButton
          tasksStatus={tasksStatus}
          onClick={() => setTasksStatus("IN_PROGRESS")}
          status={"IN_PROGRESS"}
        />
        <ChangeStatusButton
          tasksStatus={tasksStatus}
          onClick={() => setTasksStatus("DONE")}
          status={"DONE"}
        />
      </div>
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
        {activeEvents.map((task) => (
          <>
            {task.status == tasksStatus && (
              <TaskCard key={task.id} task={task} eventId={eventId} />
            )}
          </>
        ))}
      </div>
      <CreateTaskDialog
        open={isOpenCreateDialog}
        onOpenChange={setIsOpenCreateDialog}
        eventId={eventId}
      />
    </Card>
  );
}
