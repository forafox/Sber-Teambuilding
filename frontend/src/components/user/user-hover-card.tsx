import { User } from "@/api/get-users";
import {
  HoverCard,
  HoverCardContent,
  HoverCardTrigger,
} from "../ui/hover-card";
import { Avatar, AvatarFallback } from "../ui/avatar";
import { getAvatarFallback } from "./lib";

type Props = {
  user: User;
};

export function UserHoverCard({ user }: Props) {
  return (
    <HoverCard>
      <HoverCardTrigger asChild>
        <button className="flex items-center gap-2">
          <Avatar className="size-6 text-xs">
            <AvatarFallback>{getAvatarFallback(user.name)}</AvatarFallback>
          </Avatar>
          <span className="font-medium">@{user.username}</span>
        </button>
      </HoverCardTrigger>
      <HoverCardContent>
        <div className="flex items-center gap-2">
          <Avatar>
            <AvatarFallback>{getAvatarFallback(user.name)}</AvatarFallback>
          </Avatar>
          <div className="flex flex-col">
            <p className="text-sm font-medium">{user.name}</p>
            <p className="text-muted-foreground text-sm">@{user.username}</p>
          </div>
        </div>
      </HoverCardContent>
    </HoverCard>
  );
}
