import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Form,
  FormItem,
  FormLabel,
  FormControl,
  FormField,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useSignUpMutation } from "@/api/sign-up";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/sign-up")({
  component: RouteComponent,
});

const signUpSchema = z.object({
  username: z
    .string()
    .min(3, "Имя пользователя должно содержать не менее 3 символов"),
  fullName: z.string().min(2, "Имя должно содержать не менее 2 символов"),
  email: z.string().email("Введите корректный email"),
  password: z.string().min(6, "Пароль должен содержать не менее 6 символов"),
});

function RouteComponent() {
  const form = useForm<z.infer<typeof signUpSchema>>({
    resolver: zodResolver(signUpSchema),
    defaultValues: {
      username: "",
      fullName: "",
      email: "",
      password: "",
    },
  });
  const { mutate, error, isPending } = useSignUpMutation();
  const navigate = useNavigate();

  const handleSubmit = async (data: z.infer<typeof signUpSchema>) => {
    mutate(
      {
        username: data.username,
        name: data.fullName,
        email: data.email,
        password: data.password,
      },
      {
        onSuccess: () => {
          navigate({ to: "/home" });
        },
      },
    );
  };

  return (
    <div className="flex h-screen flex-col items-center justify-center px-4 md:px-0">
      <Card className="w-full md:w-128">
        <CardHeader>
          <CardTitle>Регистрация</CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form
              className="space-y-4"
              onSubmit={form.handleSubmit(handleSubmit)}
            >
              <FormField
                control={form.control}
                name="username"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Имя пользователя</FormLabel>
                    <FormControl>
                      <Input placeholder="username" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="fullName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Полное имя</FormLabel>
                    <FormControl>
                      <Input placeholder="Иван Иванов" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input
                        type="email"
                        placeholder="email@example.com"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Пароль</FormLabel>
                    <FormControl>
                      <Input type="password" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormMessage>{error?.message}</FormMessage>
              <div className="flex flex-col gap-2 md:flex-row">
                <Button type="submit" disabled={isPending}>
                  Зарегистрироваться
                </Button>
                <Button variant="link" asChild>
                  <Link to="/sign-in">Уже есть аккаунт? Войти</Link>
                </Button>
              </div>
            </form>
          </Form>
        </CardContent>
      </Card>
    </div>
  );
}
