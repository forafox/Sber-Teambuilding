import { useQuery } from "@tanstack/react-query";
import { Button } from "../ui/button";
import { getMeQueryOptions } from "@/api/get-me";
import { LogIn } from "lucide-react";
import { Link, useNavigate } from "@tanstack/react-router";
import { PrincipalProfile } from "./principal-profile";
import { useLogOutMutation } from "@/api/log-out";

export function SignInButton() {
  const { isError, data, isPending } = useQuery(getMeQueryOptions());
  const navigate = useNavigate();
  const { mutate: logOut } = useLogOutMutation();

  if (isError || isPending) {
    return (
      <Button variant="outline" asChild>
        <Link to="/sign-in">
          <LogIn /> Войти
        </Link>
      </Button>
    );
  }

  return (
    <PrincipalProfile
      principal={data}
      onLogout={() => {
        logOut(undefined, {
          onSuccess: () => {
            navigate({ to: "/sign-in" });
          },
        });
      }}
    />
  );
}
