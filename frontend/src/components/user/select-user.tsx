import * as React from "react";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getUsersQueryOptions } from "@/api/get-users";
import { UserResponse } from "@/api/api.gen";
import { cn } from "@/lib/utils";
import { ErrorBoundary } from "react-error-boundary";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

interface SelectUsersProps {
  value?: UserResponse | undefined | null;
  onChange?: (value: UserResponse | undefined) => void;
  className?: string;
  participants?: UserResponse[];
}

export function SelectUser({
  value = undefined,
  onChange,
  className,
  participants,
}: SelectUsersProps) {
  const { data } = useSuspenseQuery(getUsersQueryOptions());
  const users = data ?? [];
  const participantsIds = new Set(
    participants?.map((participant) => participant.id),
  );

  const handleSelect = (userId: number) => {
    const selectedUser = users.find((user) => user.id === userId);
    onChange?.(selectedUser);
  };

  const usersParticipants = users.filter((user) =>
    participantsIds.has(user.id),
  );

  return (
    <div className={cn("space-y-2", className)}>
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
          <Select
            value={value?.id?.toString()}
            onValueChange={(value) => handleSelect(Number(value))}
          >
            <SelectTrigger className="w-full">
              <SelectValue placeholder="Выберите пользователя" />
            </SelectTrigger>
            <SelectContent>
              {usersParticipants.length === 0 ? (
                <div className="text-muted-foreground p-2 text-sm">
                  Пользователи не найдены
                </div>
              ) : (
                usersParticipants.map((user) => (
                  <SelectItem key={user.id} value={user.id.toString()}>
                    <div className="flex flex-col">
                      <span>{user.name}</span>
                    </div>
                  </SelectItem>
                ))
              )}
            </SelectContent>
          </Select>
        </React.Suspense>
      </ErrorBoundary>
    </div>
  );
}
