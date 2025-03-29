import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../ui/dialog";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useCreateEventFromPrompt } from "@/api/create-event-propmt";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "../ui/form";
import { Textarea } from "../ui/textarea";
import { Button } from "../ui/button";
import { useNavigate } from "@tanstack/react-router";

const formSchema = z.object({
  prompt: z.string().min(1, "Пожалуйста, введите описание события"),
});

type Props = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
};

export function CreateEventPromptDialog({ open, onOpenChange }: Props) {
  const navigate = useNavigate();
  const { mutate, error, isPending } = useCreateEventFromPrompt();

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      prompt: "",
    },
  });

  const onSubmit = (values: z.infer<typeof formSchema>) => {
    mutate(values.prompt, {
      onSuccess: (data) => {
        navigate({
          to: "/events/$eventId",
          params: { eventId: String(data.id) },
        });
        onOpenChange(false);
      },
    });
  };

  return (
    <Dialog
      open={open}
      onOpenChange={(open) => {
        if (!open) {
          form.reset();
        }
        onOpenChange(open);
      }}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Создание события</DialogTitle>
          <DialogDescription>
            Опишите событие, которое вы хотите создать. Мы поможем вам с
            остальным.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form
            id="create-event-prompt-form"
            onSubmit={form.handleSubmit(onSubmit, (e) => console.error(e))}
            className="space-y-4 [&>*]:grid [&>*]:gap-2"
          >
            <FormField
              control={form.control}
              name="prompt"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Описание события</FormLabel>
                  <FormControl>
                    <Textarea
                      placeholder="Например: Хочу организовать тимбилдинг для 10 человек в формате квеста в центре города"
                      className="min-h-[100px]"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {error && <FormMessage>{error.message}</FormMessage>}
          </form>
        </Form>
        <DialogFooter>
          <Button
            type="submit"
            disabled={isPending}
            form="create-event-prompt-form"
          >
            Создать
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
