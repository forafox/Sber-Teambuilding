import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useRedeemInviteTokenMutation } from "@/api/invite";
import { toast } from "sonner";
import { z } from "zod";

const search = z.object({
  title: z.string().transform((t) => decodeURIComponent(t)),
  token: z.string().transform((t) => decodeURIComponent(t)),
});

export const Route = createFileRoute("/_authenticated/token")({
  component: RouteComponent,
  validateSearch: search,
});

function RouteComponent() {
  const { title, token } = Route.useSearch();
  const navigate = useNavigate();
  const redeemTokenMutation = useRedeemInviteTokenMutation();

  const handleAccept = async () => {
    try {
      // Call the API to verify the token
      const event = await redeemTokenMutation.mutateAsync(token);

      // Show success toast
      toast.success("Приглашение принято", {
        description: `Вы присоединились к мероприятию "${title}"`,
      });

      // Navigate to the event page with the correct event ID
      navigate({ to: `/events/${event.id}/tasks` });
    } catch (error) {
      toast.error("Ошибка", {
        description: `Не удалось принять приглашение: ${error}`,
      });
    }
  };

  const handleDeny = () => {
    // Show info toast
    toast.info("Приглашение отклонено", {
      description: "Вы вернетесь на главную страницу",
    });

    // Navigate to home
    navigate({ to: "/home" });
  };

  return (
    <div className="flex min-h-screen items-center justify-center p-4">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-center">Приглашение</CardTitle>
          <CardDescription className="text-center">
            Вас приглашают на мероприятие
          </CardDescription>
        </CardHeader>
        <CardContent>
          <h3 className="mb-4 text-center text-xl font-bold">{title}</h3>
          <p className="text-muted-foreground text-center">
            Примите приглашение, чтобы присоединиться к мероприятию
          </p>
        </CardContent>
        <CardFooter className="flex justify-between">
          <Button variant="outline" onClick={handleDeny}>
            Отклонить
          </Button>
          <Button
            onClick={handleAccept}
            disabled={redeemTokenMutation.isPending}
          >
            {redeemTokenMutation.isPending ? "Принятие..." : "Принять"}
          </Button>
        </CardFooter>
      </Card>
    </div>
  );
}
