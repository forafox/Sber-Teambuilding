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
import { UpdateTaskDialog } from "./update-task-dialog.tsx";
import { CreateTaskDialog } from "./create-task-dialog";
import { useUpdateTaskMutation } from "@/api/update-task";
import { Avatar } from "../ui/avatar";
import { AvatarFallback } from "../ui/avatar";
import { getAvatarFallback } from "../user/lib";
import { TasksTable } from "./components/data-table";

import { DataTableColumnHeader } from "./components/data-table-column-header";
import { DataTableRowActions } from "./components/data-table-row-actions";
import * as React from "react";
import { ColumnDef } from "@tanstack/react-table";
import { DataTableToolbar } from "./components/data-table-toolbar";
import { useReactTable } from "@tanstack/react-table";
import {
  ColumnFiltersState,
  SortingState,
  VisibilityState,
  getCoreRowModel,
  getFacetedRowModel,
  getFacetedUniqueValues,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
} from "@tanstack/react-table";

type Props = {
  eventId: number;
};

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
    <Button
      id={`deletetask${taskId}`}
      onClick={() => handleDeleteTask(taskId)}
      disabled={isPending}
    >
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

export function EventTasks({ eventId }: Props) {
  const { data: tasksData } = useSuspenseQuery(getTasksQueryOptions(eventId));
  const [tasksShow, setTasksShow] = useState<"TABLE" | "LIST">("LIST");
  const [tasksStatus, setTasksStatus] = useState<string[]>([
    "IN_PROGRESS",
    "DONE",
  ]);
  const [isOpenUpdateDialog, setIsOpenUpdateDialog] = useState<null | Task>(
    null,
  );
  console.log(tasksStatus);
  const [isOpenCreateDialog, setIsOpenCreateDialog] = useState(false);
  const activeTasks = tasksData || [];
  const columns: ColumnDef<Task>[] = [
    {
      accessorKey: "title",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Название" />
      ),
      cell: ({ row }) => {
        return (
          <div className="ml-4 flex space-x-2">
            <span className="max-w-[500px] truncate font-medium">
              {row.getValue("title")}
            </span>
          </div>
        );
      },
    },
    {
      accessorKey: "description",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Описание" />
      ),
      cell: ({ row }) => {
        return (
          <div className="flex space-x-2">
            <span className="max-w-[500px] truncate font-medium">
              {row.getValue("description")}
            </span>
          </div>
        );
      },
    },
    {
      accessorKey: "author",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Автор" />
      ),
      cell: ({ row }) => {
        return (
          <div className="flex space-x-2">
            <span className="max-w-[500px] truncate font-medium">
              {row.original.author.name}
            </span>
          </div>
        );
      },
    },
    {
      accessorKey: "assignee",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Исполнитель" />
      ),
      cell: ({ row }) => {
        if (row.original.assignee) {
          return (
            <div className="flex space-x-2">
              <span className="max-w-[500px] truncate font-medium">
                {row.original.assignee.name}
              </span>
            </div>
          );
        }
        return null;
      },
    },
    {
      accessorKey: "status",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Статус" />
      ),
      cell: ({ row }) => {
        const status = row.original.status;
        if (status == "IN_PROGRESS") {
          return (
            <div className="flex w-[100px] items-center">
              <span>Активна</span>
            </div>
          );
        }
        return (
          <div className="flex w-[100px] items-center">
            <span>Выполнена</span>
          </div>
        );
      },
      filterFn: (row, _, value) => {
        return value.includes(row.original.status);
      },
    },
    {
      accessorKey: "price",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Стоимость" />
      ),
      cell: ({ row }) => {
        const expenses = row.original.expenses;

        if (!expenses) {
          return null;
        }

        return (
          <div className="flex items-center">
            <span className="min-h-4 max-w-[100px] text-clip">{expenses}</span>
          </div>
        );
      },
    },
    {
      id: "actions",
      cell: ({ row }) => (
        <DataTableRowActions
          row={row}
          onEdit={() => setIsOpenUpdateDialog(row.original)}
          onDelete={() => {
            document.getElementById(`deletetask${row.original.id}`)?.click();
          }}
        />
      ),
    },
  ];

  const [rowSelection, setRowSelection] = React.useState({});
  const [columnVisibility, setColumnVisibility] =
    React.useState<VisibilityState>({});
  const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>(
    [],
  );
  const [sorting, setSorting] = React.useState<SortingState>([]);
  const table = useReactTable<Task>({
    data: activeTasks,
    columns,
    state: {
      sorting,
      columnVisibility,
      rowSelection,
      columnFilters,
    },
    enableRowSelection: true,
    onRowSelectionChange: setRowSelection,
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    onColumnVisibilityChange: setColumnVisibility,
    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFacetedRowModel: getFacetedRowModel(),
    getFacetedUniqueValues: getFacetedUniqueValues(),
  });

  const handleEditTask = (task: Task) => {
    setIsOpenUpdateDialog(task);
  };

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
      <div className="space-y-4">
        <DataTableToolbar
          table={table}
          tasksStatus={tasksStatus}
          setTasksStatus={setTasksStatus}
          tasksShow={tasksShow}
          setTasksShow={setTasksShow}
        />
        {tasksShow == "LIST" ? (
          <TaskList
            activeTasks={activeTasks}
            tasksStatus={tasksStatus}
            eventId={eventId}
            handleEditTask={handleEditTask}
          />
        ) : (
          <TasksTable table={table} columns={columns} />
        )}
      </div>
      {isOpenUpdateDialog && (
        <UpdateTaskDialog
          open={Boolean(isOpenUpdateDialog)}
          onOpenChange={setIsOpenUpdateDialog}
          defaultTask={isOpenUpdateDialog}
          eventId={eventId}
        />
      )}
      <CreateTaskDialog
        open={isOpenCreateDialog}
        onOpenChange={setIsOpenCreateDialog}
        eventId={eventId}
      />
    </Card>
  );
}

type TaskListProps = {
  activeTasks: Task[];
  tasksStatus: string[];
  eventId: number;
  handleEditTask: (task: Task) => void;
};

function TaskList({
  activeTasks,
  tasksStatus,
  eventId,
  handleEditTask,
}: TaskListProps) {
  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
      {activeTasks.map((task) => (
        <>
          {tasksStatus.includes(task.status) && (
            <Card key={task.id}>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <CardTitle>{task.title}</CardTitle>
                  <div className="border-primary/25 rounded-full border-2">
                    <CardContent>{task.expenses}₽</CardContent>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <CardDescription>Автор: {task.author.name}</CardDescription>
              </CardContent>
              <CardContent>{task.description}</CardContent>
              {task.assignee ? (
                <div className="bg-primary/15 mx-6 flex items-center gap-2 rounded-xl p-4">
                  <Avatar className="size-10 text-xs">
                    <AvatarFallback>
                      {getAvatarFallback(task.assignee.name)}
                    </AvatarFallback>
                  </Avatar>
                  <div className="flex flex-col">
                    <CardContent>{task.assignee.name}</CardContent>
                    <CardContent>
                      <CardDescription>{task.assignee.email}</CardDescription>
                    </CardContent>
                  </div>
                </div>
              ) : (
                <div className="bg-primary/15 mx-6 flex flex-col rounded-xl p-4">
                  <CardContent>Исполнитель не назначен</CardContent>
                  <CardContent>
                    <CardDescription>
                      Вы можете назначить исполнителя
                    </CardDescription>
                  </CardContent>
                </div>
              )}
              <CardFooter className="flex gap-3">
                <Button
                  onClick={() => handleEditTask(task)}
                  className="hover:bg-primary/10 border-inherit bg-transparent text-inherit"
                >
                  <PencilIcon className="mr-2 h-4 w-4" />
                  Изменить
                </Button>
                <DeleteTaskButton
                  taskId={task.id}
                  eventId={eventId}
                ></DeleteTaskButton>
                {task.status == "IN_PROGRESS" && (
                  <MakeDoneTask task={task} eventId={eventId} />
                )}
              </CardFooter>
            </Card>
          )}
        </>
      ))}
    </div>
  );
}
