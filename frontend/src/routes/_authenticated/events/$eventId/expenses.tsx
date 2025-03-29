import { createFileRoute } from "@tanstack/react-router";
import { ExpensesPage } from "@/pages/expenses";
import { getTasksQueryOptions } from "@/api/get-tasks";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getEventQueryOptions } from "@/api/get-event";

export const Route = createFileRoute(
  "/_authenticated/events/$eventId/expenses",
)({
  component: Component,
  loader: async ({ params, context }) => {
    const eventId = Number(params.eventId);
    const tasksPromise = context.queryClient.fetchQuery(
      getTasksQueryOptions(eventId),
    );
    const eventPromise = context.queryClient.fetchQuery(
      getEventQueryOptions(eventId),
    );
    const [tasks, event] = await Promise.all([tasksPromise, eventPromise]);
    return { tasks, event };
  },
});

function Component() {
  const params = Route.useParams();
  const eventId = Number(params.eventId);
  const { data: tasks } = useSuspenseQuery(getTasksQueryOptions(eventId));
  const { data: event } = useSuspenseQuery(getEventQueryOptions(eventId));

  return (
    <div className="container mx-auto space-y-8 px-4 py-6">
      <h2 className="text-2xl font-bold">Расходы</h2>
      <ExpensesPage tasks={tasks} participants={event.participants} />
    </div>
  );
}
