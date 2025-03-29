import { Api } from "./api.gen";
import { getAccessToken } from "./tokens";

export const api = new Api({
  baseURL: "/",
});

api.instance.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
