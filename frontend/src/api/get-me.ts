import { queryOptions } from "@tanstack/react-query";
import { api } from "./api";
import { userSchema } from "./get-users";

export const getMeQueryOptions = () =>
  queryOptions({
    queryKey: ["me"],
    queryFn: async () => {
      const { data } = await api.api.getCurrentUser();
      try {
        // Пробуем парсить как объект
        return userSchema.parse(data);
      } catch (error) {
        // Если ответ пришел в виде массива, берем первый элемент
        if (Array.isArray(data)) {
          return userSchema.parse(data[0]);
        }
        throw error;
      }
    },
  });
