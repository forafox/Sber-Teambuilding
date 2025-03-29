import { queryOptions } from "@tanstack/react-query";
import { api } from "./api";
import { userSchema } from "./get-users";

export const getMeQueryOptions = () =>
  queryOptions({
    queryKey: ["me"],
    queryFn: async () => {
      const { data } = await api.api.me();
      return userSchema.parse(data);
    },
  });
