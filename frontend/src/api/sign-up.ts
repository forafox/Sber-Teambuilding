import { useMutation } from "@tanstack/react-query";
import { SignUpRequest } from "./api.gen";
import { api } from "./api";
import { setAccessToken, setRefreshToken } from "./tokens";
import { isAxiosError } from "axios";

export function useSignUpMutation() {
  return useMutation({
    mutationFn: async (data: SignUpRequest) => {
      try {
        const { data: result } = await api.api.register(data);
        return result;
      } catch (error) {
        if (isAxiosError(error) && error.response?.status === 409) {
          throw new Error(
            "Пользователь с таким именем или email уже существует",
          );
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
