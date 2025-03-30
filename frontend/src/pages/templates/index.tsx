import { getTemplatesQueryOptions } from "@/api/template";
import { Event } from "@/api/get-event";
import { useQuery } from "@tanstack/react-query";
import { Link } from "@tanstack/react-router";
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

export function TemplatesPage() {
  const {
    data: templates,
    isLoading,
    error,
  } = useQuery(getTemplatesQueryOptions);

  if (isLoading) {
    return <TemplatesSkeleton />;
  }

  if (error) {
    return (
      <div className="flex h-full items-center justify-center">
        <p className="text-destructive">
          Произошла ошибка при загрузке шаблонов
        </p>
      </div>
    );
  }

  if (!templates?.length) {
    return (
      <div className="flex h-full items-center justify-center">
        <p className="text-muted-foreground">Нет доступных шаблонов</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8">
      <h1 className="mb-6 text-2xl font-bold">Шаблоны мероприятий</h1>
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
        {templates.map((template) => (
          <TemplateCard key={template.id} template={template} />
        ))}
      </div>
    </div>
  );
}

function TemplateCard({ template }: { template: Event }) {
  return (
    <Card className="flex h-full flex-col">
      <CardHeader>
        <CardTitle>{template.title}</CardTitle>
        <CardDescription>
          {template.description || "Нет описания"}
        </CardDescription>
      </CardHeader>
      <CardContent className="flex-grow">
        <div className="space-y-2">
          <p className="text-sm">
            <span className="font-medium">Дата создания:</span>{" "}
            {format(template.date, "PPP", { locale: ru })}
          </p>
          <p className="text-sm">
            <span className="font-medium">Автор:</span> {template.author.name}
          </p>
          <p className="text-sm">
            <span className="font-medium">Участников:</span>{" "}
            {template.participants.length}
          </p>
          {template.location && (
            <p className="text-sm">
              <span className="font-medium">Место:</span> {template.location}
            </p>
          )}
        </div>
      </CardContent>
      <CardFooter>
        <Link
          to="/templates/$id"
          params={{ id: template.id.toString() }}
          className="w-full"
        >
          <Button variant="default" className="w-full">
            Открыть
          </Button>
        </Link>
      </CardFooter>
    </Card>
  );
}

function TemplatesSkeleton() {
  return (
    <div className="container mx-auto py-8">
      <h1 className="mb-6 text-2xl font-bold">Шаблоны мероприятий</h1>
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
        {[1, 2, 3, 4, 5, 6].map((i) => (
          <Card key={i} className="flex h-full flex-col">
            <CardHeader>
              <Skeleton className="h-6 w-3/4" />
              <Skeleton className="mt-2 h-4 w-full" />
            </CardHeader>
            <CardContent className="flex-grow">
              <div className="space-y-3">
                <Skeleton className="h-4 w-2/3" />
                <Skeleton className="h-4 w-1/2" />
                <Skeleton className="h-4 w-3/4" />
                <Skeleton className="h-4 w-1/3" />
              </div>
            </CardContent>
            <CardFooter>
              <Skeleton className="h-10 w-full" />
            </CardFooter>
          </Card>
        ))}
      </div>
    </div>
  );
}
