"use client";

import { Table } from "@tanstack/react-table";
import { X } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { DataTableViewOptions } from "./data-table-view-options";

import { statuses } from "../data/data";
import { DataTableFacetedFilter } from "./data-table-faceted-filter";
import { Task } from "@/api/get-tasks";
import { ListIcon, LayoutGridIcon } from "lucide-react";
import Fuse from "fuse.js";
import { User } from "@/api/get-users";

type fuseResult = {
  id: number;
  status: "IN_PROGRESS" | "DONE";
  title: string;
  author: {
    id: number;
    name: string;
    username: string;
    email: string;
    role: "USER" | "ADMIN" | "GUEST" | "HOUSE_OWNER";
  };
  assignee?: User | undefined | null;
  description?: string | undefined;
  expenses?: number | undefined;
  url?: string | undefined;
}[];

interface DataTableToolbarProps {
  table: Table<Task> | undefined;
  tasksStatus: string[];
  setTasksStatus: React.Dispatch<React.SetStateAction<string[]>>;
  tasksShow: "TABLE" | "LIST";
  setTasksShow: React.Dispatch<React.SetStateAction<"TABLE" | "LIST">>;
  setActiveTasks: React.Dispatch<React.SetStateAction<Task[] | []>>;
  tasksData: Task[];
}

function ChangeTaskShow({
  tasksShow,
  onClick,
  show,
}: {
  tasksShow: "TABLE" | "LIST";
  onClick: () => void;
  show: "TABLE" | "LIST";
}) {
  return (
    <button
      onClick={onClick}
      className={`${tasksShow != show ? "text-primary bg-transparent" : "bg-primary text-primary-foreground hover:bg-primary/90 shadow-xs"} focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructiv inline-flex shrink-0 items-center justify-center rounded-md p-2 text-sm font-medium whitespace-nowrap transition-all outline-none focus-visible:ring-[3px]`}
    >
      {show == "TABLE" ? <ListIcon size={16} /> : <LayoutGridIcon size={16} />}
    </button>
  );
}

export function DataTableToolbar({
  table,
  tasksStatus,
  setTasksStatus,
  tasksShow,
  setTasksShow,
  setActiveTasks,
  tasksData,
}: DataTableToolbarProps) {
  const fuse = new Fuse(tasksData, {
    keys: ["title"],
    includeScore: false,
    threshold: 0.3,
  });

  if (table) {
    const isFiltered = table.getState().columnFilters.length > 0;
    return (
      <div className="flex items-center justify-between">
        <div className="flex flex-1 items-center space-x-2">
          <Input
            placeholder="Фильтр задач..."
            value={(table.getColumn("title")?.getFilterValue() as string) ?? ""}
            onChange={(event) => {
              table.getColumn("title")?.setFilterValue(event.target.value);
              if (event.target.value == "") {
                setActiveTasks(tasksData);
              } else {
                const newArray = fuse.search(event.target.value);
                const items: fuseResult = [];
                newArray.forEach((elem) => items.push(elem.item));
                setActiveTasks(items);
              }
            }}
            className="h-8 w-[150px] lg:w-[250px]"
          />
          {table.getColumn("status") && (
            <DataTableFacetedFilter
              column={table.getColumn("status")}
              title="Статус"
              options={statuses}
              tasksStatus={tasksStatus}
              setTasksStatus={setTasksStatus}
            />
          )}
          {isFiltered && (
            <Button
              variant="ghost"
              onClick={() => table.resetColumnFilters()}
              className="h-8 px-2 lg:px-3"
            >
              Удалить
              <X />
            </Button>
          )}
          <ChangeTaskShow
            onClick={() => setTasksShow("TABLE")}
            tasksShow={tasksShow}
            show="TABLE"
          />
          <ChangeTaskShow
            onClick={() => setTasksShow("LIST")}
            tasksShow={tasksShow}
            show="LIST"
          />
        </div>
        <DataTableViewOptions table={table} hidden={tasksShow == "LIST"} />
      </div>
    );
  }
}
