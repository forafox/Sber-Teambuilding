import { queryOptions } from "@tanstack/react-query";
import { api } from "./api";
import { eventSchema } from "./get-event";

export const getEventsQueryOptions = () =>
  queryOptions({
    queryKey: ["events"],
    queryFn: async () => {
      const { data } = await api.api.getEvents({ size: 10_000 });
      try {
        // Пробуем парсить как массив (старый формат)
        return eventSchema.array().parse(data);
      } catch (error) {
        // Если ответ пришел в виде объекта со свойством content
        if (
          data &&
          typeof data === "object" &&
          "content" in data &&
          Array.isArray(data.content)
        ) {
          return eventSchema.array().parse(data.content);
        }
        // Если ответ пришел в виде одиночного объекта
        if (data && typeof data === "object" && !Array.isArray(data)) {
          return [eventSchema.parse(data)];
        }
        throw error;
      }
    },
  });
