import { useCallback, useState, useRef } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { format } from "date-fns";
import { useSuspenseQuery } from "@tanstack/react-query";
import { getMeQueryOptions } from "@/api/get-me";
import { Message } from "@/api/get-chat";
import {
  ContextMenu,
  ContextMenuContent,
  ContextMenuItem,
  ContextMenuTrigger,
} from "@/components/ui/context-menu";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useUpdateMessage } from "@/api/update-message";
import { useDeleteMessage } from "@/api/delete-message";
import { ClipboardCopyIcon, EditIcon, TrashIcon } from "lucide-react";
import { isMobileDevice } from "@/lib/utils";
import { useOutsideClick } from "@/hooks/useOutsideClick";

type MessageItemProps = {
  message: Message;
  chatId: number;
};

export function MessageItem({ message, chatId }: MessageItemProps) {
  const { data: userData } = useSuspenseQuery(getMeQueryOptions());
  const isCurrentUser = userData?.id === message.author?.id;
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [editedContent, setEditedContent] = useState(message.content);
  const [isContextMenuOpen, setIsContextMenuOpen] = useState(false);
  const isMobile = isMobileDevice();
  const contextMenuRef = useRef<HTMLDivElement>(null);
  const contentRef = useRef<HTMLDivElement>(null);

  useOutsideClick(contextMenuRef, () => {
    if (isContextMenuOpen) {
      setIsContextMenuOpen(false);
    }
  });

  const updateMessage = useUpdateMessage(chatId, message.id);
  const deleteMessage = useDeleteMessage(chatId, message.id);

  const getInitials = useCallback((name: string) => {
    return name
      .split(" ")
      .map((n) => n[0])
      .join("")
      .toUpperCase();
  }, []);

  const handleCopyMessage = () => {
    navigator.clipboard.writeText(message.content);
    setIsContextMenuOpen(false);
  };

  const handleEditMessage = () => {
    setEditedContent(message.content);
    setIsEditDialogOpen(true);
    setIsContextMenuOpen(false);
  };

  const handleSaveEdit = () => {
    if (editedContent.trim() && editedContent !== message.content) {
      updateMessage.mutate({ content: editedContent.trim() });
    }
    setIsEditDialogOpen(false);
  };

  const handleDeleteMessage = () => {
    deleteMessage.mutate();
    setIsContextMenuOpen(false);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSaveEdit();
    }
  };

  const handleMessageClick = (e: React.MouseEvent) => {
    if (isMobile) {
      e.preventDefault();
      e.stopPropagation();
      setIsContextMenuOpen(true);
    }
  };

  return (
    <>
      <div
        className={`flex items-start gap-3 ${
          isCurrentUser ? "flex-row-reverse" : "flex-row"
        }`}
      >
        <Avatar className="h-8 w-8">
          <AvatarImage src="" alt={message.author?.name || ""} />
          <AvatarFallback>
            {message.author?.name ? getInitials(message.author.name) : ""}
          </AvatarFallback>
        </Avatar>

        {isMobile ? (
          <div
            ref={contentRef}
            className={`flex max-w-[80%] flex-col ${
              isCurrentUser ? "items-end" : "items-start"
            }`}
            onClick={handleMessageClick}
          >
            <div
              className={`rounded-lg px-3 py-2 ${
                isCurrentUser
                  ? "bg-primary text-primary-foreground"
                  : "bg-muted text-muted-foreground"
              }`}
            >
              <p className="text-sm">{message.content}</p>
            </div>
            <span className="text-muted-foreground mt-1 text-xs">
              {format(message.timestamp, "HH:mm")}
            </span>

            {isContextMenuOpen && (
              <div
                ref={contextMenuRef}
                className="bg-popover text-popover-foreground animate-in fade-in-0 zoom-in-95 absolute z-50 min-w-32 rounded-md border p-1 shadow-md"
                style={{
                  left: isCurrentUser
                    ? "auto"
                    : `${contentRef.current?.getBoundingClientRect().left}px`,
                  right: isCurrentUser
                    ? `calc(100% - ${contentRef.current?.getBoundingClientRect().right}px)`
                    : "auto",
                  top: `${contentRef.current?.getBoundingClientRect().bottom}px`,
                }}
              >
                <div
                  className="hover:bg-accent hover:text-accent-foreground cursor-pointer px-2 py-1.5 text-sm"
                  onClick={handleCopyMessage}
                >
                  <ClipboardCopyIcon className="mr-2 inline-block h-4 w-4" />
                  <span>Копировать</span>
                </div>
                {isCurrentUser && (
                  <>
                    <div
                      className="hover:bg-accent hover:text-accent-foreground cursor-pointer px-2 py-1.5 text-sm"
                      onClick={handleEditMessage}
                    >
                      <EditIcon className="mr-2 inline-block h-4 w-4" />
                      <span>Изменить</span>
                    </div>
                    <div
                      className="text-destructive hover:bg-destructive hover:text-destructive-foreground cursor-pointer px-2 py-1.5 text-sm"
                      onClick={handleDeleteMessage}
                    >
                      <TrashIcon className="mr-2 inline-block h-4 w-4" />
                      <span>Удалить</span>
                    </div>
                  </>
                )}
              </div>
            )}
          </div>
        ) : (
          <ContextMenu>
            <ContextMenuTrigger>
              <div
                className={`flex max-w-[80%] flex-col ${
                  isCurrentUser ? "items-end" : "items-start"
                }`}
              >
                <div
                  className={`rounded-lg px-3 py-2 ${
                    isCurrentUser
                      ? "bg-primary text-primary-foreground"
                      : "bg-muted text-muted-foreground"
                  }`}
                >
                  <p className="text-sm">{message.content}</p>
                </div>
                <span className="text-muted-foreground mt-1 text-xs">
                  {format(message.timestamp, "HH:mm")}
                </span>
              </div>
            </ContextMenuTrigger>
            <ContextMenuContent>
              <ContextMenuItem onClick={handleCopyMessage}>
                <ClipboardCopyIcon className="mr-2 h-4 w-4" />
                <span>Копировать</span>
              </ContextMenuItem>
              {isCurrentUser && (
                <>
                  <ContextMenuItem onClick={handleEditMessage}>
                    <EditIcon className="mr-2 h-4 w-4" />
                    <span>Изменить</span>
                  </ContextMenuItem>
                  <ContextMenuItem
                    onClick={handleDeleteMessage}
                    variant="destructive"
                  >
                    <TrashIcon className="mr-2 h-4 w-4" />
                    <span>Удалить</span>
                  </ContextMenuItem>
                </>
              )}
            </ContextMenuContent>
          </ContextMenu>
        )}
      </div>

      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Изменить сообщение</DialogTitle>
          </DialogHeader>
          <div className="flex items-center space-y-2">
            <Input
              value={editedContent}
              onChange={(e) => setEditedContent(e.target.value)}
              onKeyDown={handleKeyDown}
              className="flex-1"
              autoFocus
            />
          </div>
          <DialogFooter className="sm:justify-start">
            <Button
              type="button"
              variant="secondary"
              onClick={() => setIsEditDialogOpen(false)}
            >
              Отмена
            </Button>
            <Button type="button" onClick={handleSaveEdit}>
              Сохранить
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
