import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { UserHoverCard } from "@/components/user/user-hover-card";
import { Transaction } from "@/pages/expenses/logic";
import { User } from "@/api/get-users";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { CheckCircle, CheckIcon } from "lucide-react";
import { useCreateEventTransactionMutation } from "@/api/transactions";
import { Button } from "../ui/button";

interface BalanceTableProps {
  balances: Transaction[];
  participants: User[];
  eventId: number;
}

export function BalanceTable({
  balances,
  participants,
  eventId,
}: BalanceTableProps) {
  const { mutate, isPending } = useCreateEventTransactionMutation();
  function getParticipantByUsername(username: string) {
    return participants.find(
      (participant) => participant.username === username,
    );
  }

  if (balances.length === 0) {
    return (
      <Alert className="bg-green-50">
        <CheckCircle className="h-5 w-5 text-green-500" />
        <AlertDescription className="ml-2">
          Все траты распределены равномерно. Транзакции не требуются.
        </AlertDescription>
      </Alert>
    );
  }

  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Кто должен</TableHead>
            <TableHead>Кому должен</TableHead>
            <TableHead className="text-right">Сумма</TableHead>
            <TableHead />
          </TableRow>
        </TableHeader>
        <TableBody>
          {balances.map((balance) => (
            <TableRow key={`${balance.from}-${balance.to}`}>
              <TableCell>
                <UserHoverCard user={getParticipantByUsername(balance.from)!} />
              </TableCell>
              <TableCell>
                <UserHoverCard user={getParticipantByUsername(balance.to)!} />
              </TableCell>
              <TableCell className="text-right">
                {balance.amount.toLocaleString("ru-RU", {
                  style: "currency",
                  currency: "RUB",
                })}
              </TableCell>
              <TableCell>
                <Button
                  variant="outline"
                  disabled={isPending}
                  size="icon"
                  onClick={() =>
                    mutate({
                      fromId: getParticipantByUsername(balance.from)!.id,
                      toId: getParticipantByUsername(balance.to)!.id,
                      amount: balance.amount,
                      eventId: eventId,
                    })
                  }
                >
                  <CheckIcon />
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
