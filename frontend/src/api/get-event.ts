import { z } from "zod";
import { api } from "./api";
import { userSchema } from "./get-users";

export const eventSchema = z.object({
  id: z.number(),
  title: z.string(),
  date: z.coerce.date(),
  participants: userSchema.array(),
  author: userSchema,
  chatId: z.number(),
  description: z
    .string()
    .nullish()
    .transform((it) => it ?? undefined),
  location: z
    .string()
    .nullish()
    .transform((it) => it ?? undefined),
  status: z.enum(["IN_PROGRESS", "DONE", "PLANNED", "COMPLETED", "CANCELLED"]).default("IN_PROGRESS"),
});

export type Event = z.infer<typeof eventSchema>;

export const getEventQueryOptions = (eventId: number) => ({
  queryKey: ["event", eventId],
  queryFn: async () => {
    const { data } = await api.api.getEventById(eventId);
    return eventSchema.parse(data);
  },
});
