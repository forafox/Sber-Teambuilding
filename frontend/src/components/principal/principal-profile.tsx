import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { UserResponse } from "@/api/api.gen";
import { useNavigate } from "@tanstack/react-router";
import { HomeIcon, LogOut, User } from "lucide-react";

interface PrincipalProfileProps {
  principal: UserResponse;
  onLogout: () => void;
}

export function PrincipalProfile({
  principal,
  onLogout,
}: PrincipalProfileProps) {
  const navigate = useNavigate();

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="icon">
          <User />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-56" align="end">
        <DropdownMenuLabel className="font-normal">
          <div className="flex flex-col space-y-1">
            <p className="text-sm leading-none font-medium">{principal.name}</p>
            <p className="text-muted-foreground text-xs leading-none">
              @{principal.username}
            </p>
          </div>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        <DropdownMenuItem onClick={() => navigate({ to: "/home" })}>
          <HomeIcon />
          Главная
        </DropdownMenuItem>
        <DropdownMenuItem onClick={onLogout}>
          <LogOut />
          Выйти
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
