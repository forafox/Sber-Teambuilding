import { useQuery } from "@tanstack/react-query";
import { useParams } from "@tanstack/react-router";
import { getTemplateTasksQueryOptions } from "@/api/template";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { AlertCircle, Loader2 } from "lucide-react";

export default function TemplateTasksTable() {
  const { id } = useParams({
    from: "/_authenticated/templates/$id",
  });
  const templateId = Number(id);

  const {
    data: tasks,
    isLoading,
    error,
  } = useQuery(getTemplateTasksQueryOptions(templateId));

  if (isLoading) {
    return (
      <div className="flex items-center justify-center p-8">
        <Loader2 className="text-muted-foreground h-8 w-8 animate-spin" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="border-destructive text-destructive rounded-lg border p-4">
        <div className="flex items-center gap-2">
          <AlertCircle className="h-4 w-4" />
          <div className="font-medium">Ошибка</div>
        </div>
        <div className="mt-2 text-sm">
          Не удалось загрузить список задач шаблона. Попробуйте обновить
          страницу.
          {error.message}
        </div>
      </div>
    );
  }

  if (!tasks?.length) {
    return (
      <div className="rounded-lg border p-4">
        <div className="font-medium">Нет задач</div>
        <div className="mt-2 text-sm">В этом шаблоне пока нет задач.</div>
      </div>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Задачи шаблона</CardTitle>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Название</TableHead>
              <TableHead className="text-right">Расходы</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {tasks.map((task) => (
              <TableRow key={task.id}>
                <TableCell className="font-medium">{task.title}</TableCell>
                <TableCell className="text-right">
                  {task.expenses ? `${task.expenses} ₽` : "—"}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
