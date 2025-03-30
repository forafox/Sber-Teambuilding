import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { MoreHorizontal, PencilIcon } from "lucide-react";
import { useState } from "react";
import { UpdateTaskDialog } from "@/components/tasks/update-task-dialog";
import { Task } from "@/api/get-tasks";

interface ExpensesRowActionsProps {
  task: Task;
  eventId: number;
}

export function ExpensesRowActions({ task, eventId }: ExpensesRowActionsProps) {
  const [isUpdateDialogOpen, setIsUpdateDialogOpen] = useState<Task | null>(
    null,
  );

  return (
    <>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="size-6">
            <span className="sr-only">Открыть меню</span>
            <MoreHorizontal className="size-4" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem onClick={() => setIsUpdateDialogOpen(task)}>
            <PencilIcon />
            Редактировать
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <UpdateTaskDialog
        open={Boolean(isUpdateDialogOpen)}
        onOpenChange={setIsUpdateDialogOpen}
        defaultTask={task}
        eventId={eventId}
      />
    </>
  );
}
