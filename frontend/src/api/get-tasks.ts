import { z } from "zod";
import { userSchema } from "./get-users";
import { api } from "./api";

export const taskSchema = z.object({
  id: z.number(),
  title: z
    .string()
    .min(3, { message: "Название должно содержать минимум 3 символа" }),
  assignee: userSchema.nullish(),
  author: userSchema,
  description: z.string().optional(),
  expenses: z.coerce.number().optional(),
  status: z.enum(["IN_PROGRESS", "DONE"]),
});

export type Task = z.infer<typeof taskSchema>;

export const getTasksQueryOptions = (eventId: number) => ({
  queryKey: ["tasks", eventId],
  queryFn: async () => {
    const { data } = await api.api.getTasks(eventId, { pageSize: 10_000 });
    return taskSchema.array().parse(data.content);
  },
});
