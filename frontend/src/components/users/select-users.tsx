import * as React from "react";
import { CheckIcon, ChevronsUpDown, X } from "lucide-react";
import { useSuspenseQuery } from "@tanstack/react-query";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { getUsersQueryOptions } from "@/api/get-users";
import { UserResponse } from "@/api/api.gen";
import { cn } from "@/lib/utils";
import { ErrorBoundary } from "react-error-boundary";
import Fuse from "fuse.js";
interface SelectUsersProps {
  value?: UserResponse[];
  onChange?: (value: UserResponse[]) => void;
  className?: string;
}

export function SelectUsers({
  value = [],
  onChange,
  className,
}: SelectUsersProps) {
  const [open, setOpen] = React.useState(false);
  const [searchValue, setSearchValue] = React.useState("");

  const { data } = useSuspenseQuery(getUsersQueryOptions());
  const users = data ?? [];

  const handleSelect = (user: UserResponse) => {
    const isSelected = value.some(
      (selectedUser) => selectedUser.id === user.id,
    );
    let newValue: UserResponse[];

    if (isSelected) {
      newValue = value.filter((selectedUser) => selectedUser.id !== user.id);
    } else {
      newValue = [...value, user];
    }

    onChange?.(newValue);
  };

  const handleRemove = (userId: number) => {
    onChange?.(value.filter((selectedUser) => selectedUser.id !== userId));
  };

  const usersFuse = new Fuse(users, {
    includeScore: true,
    keys: ["username", "name", "email"],
  });

  const filteredUsers =
    searchValue.length > 0
      ? usersFuse.search(searchValue)
      : users.map((it) => ({ item: it }));

  return (
    <div className={cn("space-y-2", className)}>
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            className="w-full justify-between"
          >
            <span className="truncate">
              {value.length > 0
                ? `Выбрано пользователей: ${value.length}`
                : "Выберите пользователей"}
            </span>
            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-full p-0">
          <ErrorBoundary
            fallback={
              <div className="text-destructive p-4 text-sm">
                Ошибка загрузки пользователей
              </div>
            }
          >
            <React.Suspense
              fallback={
                <div className="p-4 text-sm">Загрузка пользователей...</div>
              }
            >
              <Command shouldFilter={false}>
                <CommandInput
                  placeholder="Поиск пользователей..."
                  value={searchValue}
                  onValueChange={setSearchValue}
                />
                <CommandList>
                  <CommandEmpty>Пользователи не найдены</CommandEmpty>
                  <CommandGroup>
                    {filteredUsers.map((user) => {
                      const isSelected = value.some(
                        (selectedUser) => selectedUser.id === user.item.id,
                      );
                      return (
                        <CommandItem
                          key={user.item.id}
                          value={user.item.username}
                          onSelect={() => handleSelect(user.item)}
                        >
                          <CheckIcon
                            className={cn(
                              "h-4 w-4",
                              isSelected ? "opacity-100" : "opacity-0",
                            )}
                          />
                          <div className="flex flex-col">
                            <span>{user.item.name}</span>
                            <span className="text-muted-foreground text-xs">
                              @{user.item.username}
                            </span>
                          </div>
                        </CommandItem>
                      );
                    })}
                  </CommandGroup>
                </CommandList>
              </Command>
            </React.Suspense>
          </ErrorBoundary>
        </PopoverContent>
      </Popover>

      {value.length > 0 && (
        <div className="mt-2 flex flex-wrap gap-1">
          {value.map((user) => (
            <Badge
              key={user.id}
              variant="secondary"
              className="px-2 py-1 text-xs"
            >
              {user.name}
              <X
                className="ml-2 size-4 cursor-pointer"
                onClick={() => handleRemove(user.id)}
              />
            </Badge>
          ))}
        </div>
      )}
    </div>
  );
}
