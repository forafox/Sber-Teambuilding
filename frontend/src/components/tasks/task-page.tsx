import { getTaskByIdQueryOptions } from "@/api/get-task";
import { useSuspenseQuery } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { UpdateTaskDialog } from "./update-task-dialog";
import { useState } from "react";
import { Badge } from "@/components/ui/badge";
import { ExternalLink, Pencil } from "lucide-react";
import { UserHoverCard } from "../user/user-hover-card";

export function TaskPage({
  taskId,
  eventId,
}: {
  taskId: number;
  eventId: number;
}) {
  const { data: task } = useSuspenseQuery(
    getTaskByIdQueryOptions(eventId, taskId),
  );
  const [isUpdateDialogOpen, setIsUpdateDialogOpen] = useState(false);

  function formatCurrency(expenses: number): string {
    return expenses.toLocaleString("ru-RU", {
      style: "currency",
      currency: "RUB",
    });
  }

  return (
    <div className="container mx-auto space-y-6 py-6">
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-2xl">{task.title}</CardTitle>
          <Badge variant={task.status === "DONE" ? "default" : "secondary"}>
            {task.status === "DONE" ? "Завершено" : "В процессе"}
          </Badge>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <h3 className="text-muted-foreground font-medium">Описание</h3>
            <p className="mt-1">{task.description || "Нет описания"}</p>
          </div>

          {task.expenses && (
            <div>
              <h3 className="text-muted-foreground font-medium">Сумма</h3>
              <p className="mt-1">{formatCurrency(task.expenses)}</p>
            </div>
          )}

          {task.url && (
            <div>
              <a
                href={task.url}
                target="_blank"
                rel="noopener noreferrer"
                className="text-primary mt-1 flex items-center gap-1 hover:underline"
              >
                <h3 className="text-muted-foreground font-medium">Ссылка</h3>
                <ExternalLink className="size-4" />
              </a>
            </div>
          )}

          <div className="grid grid-cols-1 gap-4">
            <div>
              <h3 className="text-muted-foreground font-medium">Автор</h3>
              <p className="mt-1">
                <UserHoverCard user={task.author} />
              </p>
            </div>
            <div>
              <h3 className="text-muted-foreground font-medium">Исполнитель</h3>
              <p className="mt-1">
                {task.assignee ? (
                  <UserHoverCard user={task.assignee} />
                ) : (
                  "Не назначен"
                )}
              </p>
            </div>
          </div>
        </CardContent>
        <CardFooter className="mt-6 flex items-center justify-start gap-4">
          <Button
            variant="outline"
            onClick={() =>
              window.open(
                `https://kuper.ru/multisearch?q=${encodeURIComponent(task.title)}`,
                "_blank",
              )
            }
          >
            <ExternalLink className="mr-2" />
            Найти на Купер
          </Button>
          <Button onClick={() => setIsUpdateDialogOpen(true)}>
            <Pencil className="mr-2" />
            Редактировать
          </Button>
        </CardFooter>
      </Card>

      <UpdateTaskDialog
        open={isUpdateDialogOpen}
        onOpenChange={setIsUpdateDialogOpen}
        defaultTask={task}
        eventId={eventId}
      />
    </div>
  );
}