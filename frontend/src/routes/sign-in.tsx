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
import { zodResolver } from "@hookform/resolvers/zod"; // Importing zodResolver
import { useSignInMutation } from "@/api/sign-in";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/sign-in")({
  component: RouteComponent,
});

const signInSchema = z.object({
  username: z.string().min(1),
  password: z.string().min(1),
});

function RouteComponent() {
  const navigate = useNavigate();
  const form = useForm<z.infer<typeof signInSchema>>({
    resolver: zodResolver(signInSchema),
    defaultValues: {
      username: "",
      password: "",
    },
  });
  const { mutate, error, isPending } = useSignInMutation();

  async function onSubmit(data: z.infer<typeof signInSchema>) {
    mutate(data, {
      onSuccess: () => {
        navigate({ to: "/home" });
      },
    });
  }

  return (
    <div className="flex h-screen flex-col items-center justify-center px-4 md:px-0">
      <Card className="w-full md:w-128">
        <CardHeader>
          <CardTitle>Войдите в систему</CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form className="space-y-4" onSubmit={form.handleSubmit(onSubmit)}>
              <FormField
                control={form.control}
                name="username"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Имя пользователя</FormLabel>
                    <FormControl>
                      <Input {...field} />
                    </FormControl>
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
                  </FormItem>
                )}
              />
              <FormMessage>{error?.message}</FormMessage>
              <div className="flex flex-col gap-2 md:flex-row">
                <Button disabled={isPending} type="submit">
                  Войти
                </Button>
                <Button variant="link" asChild>
                  <Link to="/sign-up">Зарегистрироваться</Link>
                </Button>
              </div>
            </form>
          </Form>
        </CardContent>
      </Card>
    </div>
  );
}
