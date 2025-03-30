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

export function useUpdateEventTransactionMutation() {
  return useMutation({
    mutationFn: async (transaction: {
      id: number;
      amount: number;
      eventId: number;
      recipientId: number;
      senderId: number;
    }) => {
      const response = await api.api.updateMoneyTransfer(
        transaction.eventId,
        transaction.id,
        {
          amount: transaction.amount,
          senderId: transaction.senderId,
          recipientId: transaction.recipientId,
        },
      );
      return transactionSchema.parse(response.data);
    },
  });
}

export function useDeleteEventTransactionMutation() {
  return useMutation({
    mutationFn: async ({
      transactionId,
      eventId,
    }: {
      transactionId: number;
      eventId: number;
    }) => {
      await api.api.deleteMoneyTransfer(transactionId, eventId);
    },
  });
}
