import { useMutation } from "@tanstack/react-query";
import { SignInRequest } from "./api.gen";
import { api } from "./api";
import { setAccessToken, setRefreshToken } from "./tokens";
import { isAxiosError } from "axios";
import { z } from "zod";

// Определяем схему для ответа при авторизации
export const jwtSchema = z.object({
  id: z.number(),
  username: z.string(),
  accessToken: z.string(),
  refreshToken: z.string(),
});

export type JwtResponse = z.infer<typeof jwtSchema>;

export function useSignInMutation() {
  return useMutation({
    mutationFn: async (data: SignInRequest) => {
      try {
        const { data: result } = await api.api.loginUser(data);
        // Валидируем ответ с помощью Zod схемы
        return jwtSchema.parse(result);
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
