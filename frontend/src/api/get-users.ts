import { z } from "zod";
import { api } from "./api";

export const userSchema = z.object({
  id: z.number(),
  name: z.string(),
  username: z.string(),
  email: z.string(),
  // TODO: remove after backend removes this
  role: z.enum(["USER", "ADMIN", "GUEST", "HOUSE_OWNER"]).catch("USER"),
});

export type User = z.infer<typeof userSchema>;

export const getUsersQueryOptions = () => ({
  queryKey: ["users"],
  queryFn: async () => {
    const { data } = await api.api.getUsers({ size: 10_000 });
    return userSchema.array().parse(data.content);
  },
});
