import { getTaskByIdQueryOptions } from "@/api/get-task";
import { TaskPage } from "@/components/tasks/task-page";
import { useSuspenseQuery } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute(
  "/_authenticated/events/$eventId/tasks/$taskId",
)({
  component: RouteComponent,
  loader: async ({ params, context }) => {
    const eventId = Number(params.eventId);
    const taskId = Number(params.taskId);
    const { data: task } = await context.queryClient.fetchQuery(
      getTaskByIdQueryOptions(eventId, taskId),
    );
    return { task };
  },
});

function RouteComponent() {
  const params = Route.useParams();
  return (
    <TaskPage eventId={Number(params.eventId)} taskId={Number(params.taskId)} />
  );
}
