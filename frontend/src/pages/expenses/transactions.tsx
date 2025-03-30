import React, { useState } from "react";
import {
  getEventTransactionsQueryOptions,
  useUpdateEventTransactionMutation,
  useDeleteEventTransactionMutation,
} from "@/api/transactions";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { formatCurrency } from "@/lib/utils";
import { Skeleton } from "@/components/ui/skeleton";
import { AlertCircle, MoreVertical, Pencil, Trash2 } from "lucide-react";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { UserHoverCard } from "@/components/user/user-hover-card";

// Schema for edit form validation
const editTransactionSchema = z.object({
  amount: z.coerce.number().positive("Сумма должна быть положительной"),
});

interface TransactionsPageProps {
  eventId: number;
}

export function TransactionsPage({ eventId }: TransactionsPageProps) {
  const [editingTransaction, setEditingTransaction] = useState<{
    id: number;
    amount: number;
    senderId: number;
    recipientId: number;
  } | null>(null);

  const queryClient = useQueryClient();

  const {
    data: transactions,
    isLoading,
    error,
  } = useQuery(getEventTransactionsQueryOptions(eventId));

  const updateMutation = useUpdateEventTransactionMutation();
  const deleteMutation = useDeleteEventTransactionMutation();

  // Set up form for editing transaction
  const form = useForm<z.infer<typeof editTransactionSchema>>({
    resolver: zodResolver(editTransactionSchema),
    defaultValues: {
      amount: editingTransaction?.amount || 0,
    },
  });

  // Update form values when editingTransaction changes
  React.useEffect(() => {
    if (editingTransaction) {
      form.reset({
        amount: editingTransaction.amount,
      });
    }
  }, [editingTransaction, form]);

  // Handle form submission
  const onSubmit = async (data: z.infer<typeof editTransactionSchema>) => {
    if (!editingTransaction) return;

    try {
      await updateMutation.mutateAsync({
        id: editingTransaction.id,
        amount: data.amount,
        eventId: eventId,
        senderId: editingTransaction.senderId,
        recipientId: editingTransaction.recipientId,
      });

      // Invalidate query to refresh transactions
      queryClient.invalidateQueries({
        queryKey: ["event", eventId, "transactions"],
      });

      // Close dialog
      setEditingTransaction(null);
    } catch (error) {
      console.error("Failed to update transaction:", error);
    }
  };

  const handleDelete = async (transactionId: number) => {
    try {
      await deleteMutation.mutateAsync({
        transactionId,
        eventId,
      });

      // Invalidate query to refresh transactions
      queryClient.invalidateQueries({
        queryKey: ["event", eventId, "transactions"],
      });
    } catch (error) {
      console.error("Failed to delete transaction:", error);
    }
  };

  if (isLoading) {
    return <TransactionsTableSkeleton />;
  }

  if (error) {
    return (
      <Alert variant="destructive">
        <AlertCircle className="h-4 w-4" />
        <AlertTitle>Ошибка</AlertTitle>
        <AlertDescription>
          Не удалось загрузить историю транзакций. Пожалуйста, попробуйте позже.
        </AlertDescription>
      </Alert>
    );
  }

  return (
    <>
      <Card>
        <CardHeader>
          <CardTitle>История транзакций</CardTitle>
          <CardDescription>
            Все денежные переводы между участниками в рамках мероприятия
          </CardDescription>
        </CardHeader>
        <CardContent>
          {transactions && transactions.length > 0 ? (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Отправитель</TableHead>
                  <TableHead>Получатель</TableHead>
                  <TableHead className="text-right">Сумма</TableHead>
                  <TableHead className="w-10"></TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {transactions.map((transaction) => (
                  <TableRow key={transaction.id}>
                    <TableCell className="font-medium">
                      <UserHoverCard user={transaction.sender} />
                    </TableCell>
                    <TableCell>
                      <UserHoverCard user={transaction.recipient} />
                    </TableCell>
                    <TableCell className="text-right">
                      {formatCurrency(transaction.amount)}
                    </TableCell>
                    <TableCell>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-8 w-8"
                          >
                            <MoreVertical className="h-4 w-4" />
                            <span className="sr-only">Действия</span>
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem
                            onClick={() =>
                              setEditingTransaction({
                                id: transaction.id,
                                amount: transaction.amount,
                                senderId: transaction.sender.id,
                                recipientId: transaction.recipient.id,
                              })
                            }
                          >
                            <Pencil className="mr-2 h-4 w-4" />
                            Изменить
                          </DropdownMenuItem>
                          <DropdownMenuItem
                            onClick={() => handleDelete(transaction.id)}
                            className="text-destructive"
                          >
                            <Trash2 className="mr-2 h-4 w-4" />
                            Удалить
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          ) : (
            <div className="text-muted-foreground py-6 text-center">
              Транзакций пока нет
            </div>
          )}
        </CardContent>
      </Card>

      {/* Edit Transaction Dialog */}
      <Dialog
        open={!!editingTransaction}
        onOpenChange={(open) => !open && setEditingTransaction(null)}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Изменить сумму транзакции</DialogTitle>
            <DialogDescription>
              Укажите новую сумму для этой транзакции
            </DialogDescription>
          </DialogHeader>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <FormField
                control={form.control}
                name="amount"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Сумма</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        step="0.01"
                        {...field}
                        placeholder="0.00"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <DialogFooter>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEditingTransaction(null)}
                >
                  Отмена
                </Button>
                <Button type="submit" disabled={updateMutation.isPending}>
                  {updateMutation.isPending ? "Сохранение..." : "Сохранить"}
                </Button>
              </DialogFooter>
            </form>
          </Form>
        </DialogContent>
      </Dialog>
    </>
  );
}

function TransactionsTableSkeleton() {
  return (
    <Card>
      <CardHeader>
        <Skeleton className="h-8 w-40" />
        <Skeleton className="mt-2 h-4 w-64" />
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <Skeleton className="h-8 w-full" />
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
        </div>
      </CardContent>
    </Card>
  );
}
