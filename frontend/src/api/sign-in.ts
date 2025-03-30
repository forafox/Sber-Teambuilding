import { useMutation } from "@tanstack/react-query";
import { SignInRequest } from "./api.gen";
import { api } from "./api";
import { setAccessToken, setRefreshToken } from "./tokens";
import { isAxiosError } from "axios";

export function useSignInMutation() {
  return useMutation({
    mutationFn: async (data: SignInRequest) => {
      try {
        const { data: result } = await api.api.loginUser(data);
        return result;
      } catch (error) {
        if (isAxiosError(error) && error.response?.status === 401) {
          throw new Error("Неверное имя пользователя или пароль");
        }
        throw error;
      }
    },
    onSuccess: (data) => {
      setAccessToken(data.accessToken);
      setRefreshToken(data.refreshToken);
    },
  });
}
