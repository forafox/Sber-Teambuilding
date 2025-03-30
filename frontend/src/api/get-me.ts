import { queryOptions } from "@tanstack/react-query";
import { api } from "./api";
import { userSchema } from "./get-users";

export const getMeQueryOptions = () =>
  queryOptions({
    queryKey: ["me"],
    queryFn: async () => {
      const { data } = await api.api.getCurrentUser();
      return userSchema.parse(data);
    },
  });
