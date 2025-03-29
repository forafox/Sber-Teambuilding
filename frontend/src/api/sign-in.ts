import { useMutation } from "@tanstack/react-query";
import { SignInRequest } from "./api.gen";
import { api } from "./api";
import { setAccessToken, setRefreshToken } from "./tokens";

export function useSignInMutation() {
  return useMutation({
    mutationFn: async (data: SignInRequest) => {
      const { data: result } = await api.api.login(data);
      return result;
    },
    onSuccess: (data) => {
      setAccessToken(data.accessToken);
      setRefreshToken(data.refreshToken);
    },
  });
}
