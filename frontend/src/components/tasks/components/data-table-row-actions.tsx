"use client";

import { Row } from "@tanstack/react-table";
import { MoreHorizontal } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Task, taskSchema } from "@/api/get-tasks";

interface DataTableRowActionsProps {
  row: Row<Task>;
  onEdit: () => void;
  onDelete: () => void;
}

export function DataTableRowActions({
  row,
  onEdit,
  onDelete,
}: DataTableRowActionsProps) {
  taskSchema.parse(row.original);

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          className="data-[state=open]:bg-muted flex h-8 w-8 p-0"
        >
          <MoreHorizontal />
          <span className="sr-only">Открыть меню</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-[160px]">
        <DropdownMenuItem onClick={onEdit}>Изменить</DropdownMenuItem>
        <DropdownMenuItem onClick={onDelete}>Удалить</DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
