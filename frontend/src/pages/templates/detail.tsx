import { getTemplateQueryOptions, useCreateTemplate } from "@/api/template";
import { useNavigate, useParams } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { format } from "date-fns";
import { ru } from "date-fns/locale";
import { Skeleton } from "@/components/ui/skeleton";
import { Link } from "@tanstack/react-router";
import { toast } from "sonner";
import { ArrowLeftIcon } from "lucide-react";
import TemplateTasksTable from "./detail-tasks";

export function TemplateDetailPage() {
  const { id } = useParams({ from: "/_authenticated/templates/$id" });
  const templateId = parseInt(id);
  const {
    data: template,
    isLoading,
    error,
  } = useQuery(getTemplateQueryOptions(templateId));
  const createTemplateMutation = useCreateTemplate();
  const navigate = useNavigate();

  const handleApplyTemplate = async () => {
    try {
      const event = await createTemplateMutation.mutateAsync(templateId);
      toast.success("Шаблон успешно применен");
      navigate({
        to: "/events/$eventId/tasks",
        params: { eventId: String(event.id) },
      });
    } catch (error) {
      toast.error("Не удалось применить шаблон", {
        description:
          error instanceof Error ? error.message : "Неизвестная ошибка",
      });
    }
  };

  if (isLoading) {
    return <TemplateDetailSkeleton />;
  }

  if (error) {
    return (
      <div className="flex h-full items-center justify-center">
        <p className="text-destructive">
          Произошла ошибка при загрузке шаблона
        </p>
      </div>
    );
  }

  if (!template) {
    return (
      <div className="flex h-full items-center justify-center">
        <p className="text-muted-foreground">Шаблон не найден</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8">
      <div className="mb-6 flex items-center">
        <Link to="/templates" className="mr-4">
          <Button variant="outline" size="icon">
            <ArrowLeftIcon />
          </Button>
        </Link>
        <h1 className="text-2xl font-bold">Шаблон мероприятия</h1>
      </div>

      <Card className="mx-auto mb-8 max-w-3xl">
        <CardHeader>
          <CardTitle className="text-xl">{template.title}</CardTitle>
          {template.description && (
            <CardDescription className="mt-2 text-base">
              {template.description}
            </CardDescription>
          )}
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <h3 className="mb-1 text-sm font-medium">Дата создания</h3>
              <p>{format(template.date, "PPP", { locale: ru })}</p>
            </div>
          </div>
        </CardContent>
        <CardFooter>
          <Button
            onClick={handleApplyTemplate}
            className="w-full"
            disabled={createTemplateMutation.isPending}
          >
            {createTemplateMutation.isPending
              ? "Применение..."
              : "Применить шаблон"}
          </Button>
        </CardFooter>
      </Card>

      <div className="mx-auto max-w-3xl">
        <TemplateTasksTable />
      </div>
    </div>
  );
}

function TemplateDetailSkeleton() {
  return (
    <div className="container mx-auto py-8">
      <div className="mb-6 flex items-center">
        <div className="mr-4">
          <Skeleton className="h-10 w-10" />
        </div>
        <Skeleton className="h-8 w-64" />
      </div>

      <Card className="mx-auto max-w-3xl">
        <CardHeader>
          <Skeleton className="h-7 w-3/4" />
          <Skeleton className="mt-2 h-5 w-full" />
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <Skeleton className="mb-2 h-4 w-24" />
              <Skeleton className="h-5 w-36" />
            </div>
            <div>
              <Skeleton className="mb-2 h-4 w-16" />
              <Skeleton className="h-5 w-28" />
            </div>
            <div>
              <Skeleton className="mb-2 h-4 w-20" />
              <Skeleton className="h-5 w-32" />
            </div>
          </div>

          <div>
            <Skeleton className="mb-2 h-4 w-24" />
            <div className="grid grid-cols-1 gap-2 md:grid-cols-2">
              {[1, 2, 3, 4].map((i) => (
                <Skeleton key={i} className="h-6" />
              ))}
            </div>
          </div>
        </CardContent>
        <CardFooter>
          <Skeleton className="h-10 w-full" />
        </CardFooter>
      </Card>
    </div>
  );
}
