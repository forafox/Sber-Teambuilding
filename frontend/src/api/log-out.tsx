import { useMutation, useQueryClient } from "@tanstack/react-query";
import { removeAccessToken, removeRefreshToken } from "./tokens";

export function useLogOutMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async () => {
      removeAccessToken();
      removeRefreshToken();
    },
    onSuccess: () => {
      return queryClient.invalidateQueries();
    },
  });
}
