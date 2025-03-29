import { useMutation } from "@tanstack/react-query";
import { SignUpRequest } from "./api.gen";
import { api } from "./api";
import { setAccessToken, setRefreshToken } from "./tokens";

export function useSignUpMutation() {
  return useMutation({
    mutationFn: async (data: SignUpRequest) => {
      const { data: result } = await api.api.register(data);
      return result;
    },
    onSuccess: (data) => {
      setAccessToken(data.accessToken);
      setRefreshToken(data.refreshToken);
    },
  });
}
