import { queryOptions, useMutation } from "@tanstack/react-query";
import { api } from "./api";
import { z } from "zod";
import { userSchema } from "./get-users";

const transactionSchema = z.object({
  id: z.number(),
  sender: userSchema,
  recipient: userSchema,
  amount: z.number(),
});

export const getEventTransactionsQueryOptions = (eventId: number) =>
  queryOptions({
    queryKey: ["event", eventId, "transactions"],
    queryFn: async () => {
      const response = await api.api.getAllMoneyTransfersByEventId(eventId);
      return transactionSchema.array().parse(response.data.content);
    },
  });

export function useCreateEventTransactionMutation() {
  return useMutation({
    mutationFn: async (transaction: {
      fromId: number;
      toId: number;
      amount: number;
      eventId: number;
    }) => {
      const response = await api.api.createMoneyTransfer(transaction.eventId, {
        amount: transaction.amount,
        senderId: transaction.fromId,
        recipientId: transaction.toId,
      });
      return transactionSchema.parse(response.data);
    },
  });
}
