import {
  Table,
  TableBody,
  TableCell,
  TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { calculateBalances } from "./logic";
import { User } from "@/api/get-users";
import { Task } from "@/api/get-tasks";
import { UserHoverCard } from "@/components/user/user-hover-card";
import { ExpensesRowActions } from "@/components/expenses/expenses-row-actions";
import { TransactionsPage } from "./transactions";
import { BalanceTable } from "@/components/expenses/balance-table";

interface ExpensesPageProps {
  tasks: Task[];
  participants: User[];
  eventId: number;
}

export function ExpensesPage({
  tasks,
  participants,
  eventId,
}: ExpensesPageProps) {
  const tasksWithExpenses = tasks.filter((task) => task.expenses !== undefined);

  // Calculate balances between participants
  const balances = calculateBalances(tasksWithExpenses, participants);
  const filteredTasks = tasksWithExpenses.sort((a, b) => {
    if (a.expenses === undefined) return 1;
    if (b.expenses === undefined) return -1;
    return b.expenses - a.expenses;
  });

  const totalExpenses = tasksWithExpenses.reduce(
    (acc, task) => acc + (task.expenses || 0),
    0,
  );

  function getParticipantByUsername(username: string) {
    return participants.find(
      (participant) => participant.username === username,
    );
  }

  return (
    <div className="space-y-8">
      <div className="flex items-center space-x-2">{/* TODO: filters */}</div>

      <Card>
        <CardHeader>
          <CardTitle>Задачи</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Название</TableHead>
                  <TableHead>Исполнитель</TableHead>
                  <TableHead>Автор</TableHead>
                  <TableHead className="text-right">Сумма</TableHead>
                  <TableHead className="w-[50px]"></TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredTasks.map((task) => (
                  <TableRow key={task.id}>
                    <TableCell>{task.title}</TableCell>
                    <TableCell>
                      {task.assignee && <UserHoverCard user={task.assignee} />}
                    </TableCell>
                    <TableCell>
                      <UserHoverCard user={task.author} />
                    </TableCell>
                    <TableCell className="text-right">
                      {task.expenses?.toLocaleString("ru-RU", {
                        style: "currency",
                        currency: "RUB",
                      })}
                    </TableCell>
                    <TableCell>
                      <ExpensesRowActions task={task} eventId={eventId} />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
              <TableFooter>
                <TableRow>
                  <TableCell colSpan={3}>Итого</TableCell>
                  <TableCell className="text-right">
                    {totalExpenses.toLocaleString("ru-RU", {
                      style: "currency",
                      currency: "RUB",
                    })}
                  </TableCell>
                  <TableCell />
                </TableRow>
              </TableFooter>
            </Table>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Баланс между участниками</CardTitle>
        </CardHeader>
        <CardContent>
          <BalanceTable balances={balances} participants={participants} />
        </CardContent>
      </Card>

      <TransactionsPage eventId={eventId} />
    </div>
  );
}
