import { queryOptions } from "@tanstack/react-query";
import { api } from "./api";
import { eventSchema } from "./get-event";

export const getEventsQueryOptions = () =>
  queryOptions({
    queryKey: ["events"],
    queryFn: async () => {
      const { data } = await api.api.getEvents({ size: 10_000 });
      return eventSchema.array().parse(data);
    },
  });
