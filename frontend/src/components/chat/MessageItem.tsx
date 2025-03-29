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
import {
  ClipboardCopyIcon,
  EditIcon,
  TrashIcon,
  ReplyIcon,
  PinIcon,
} from "lucide-react";
import { isMobileDevice } from "@/lib/utils";
import { useOutsideClick } from "@/hooks/useOutsideClick";
import { getMessagesQueryOptions } from "@/api/get-messages";

type MessageItemProps = {
  message: Message;
  chatId: number;
  onReply?: (message: Message) => void;
  scrollToMessage?: (messageId: number) => void;
  onTogglePin?: (message: Message) => void;
};

export function MessageItem({
  message,
  chatId,
  onReply,
  scrollToMessage,
  onTogglePin,
}: MessageItemProps) {
  const { data: userData } = useSuspenseQuery(getMeQueryOptions());
  const { data: messages } = useSuspenseQuery(getMessagesQueryOptions(chatId));
  const isCurrentUser = userData?.id === message.author?.id;
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [editedContent, setEditedContent] = useState(message.content);
  const [isRemoveReply, setIsRemoveReply] = useState(false);
  const [isContextMenuOpen, setIsContextMenuOpen] = useState(false);
  const isMobile = isMobileDevice();
  const contextMenuRef = useRef<HTMLDivElement>(null);
  const contentRef = useRef<HTMLDivElement>(null);

  const replyToMessage = message.replyToMessageId
    ? (messages?.find((msg) => msg.id === message.replyToMessageId) ?? null)
    : null;

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
    setIsRemoveReply(false);
    setIsEditDialogOpen(true);
    setIsContextMenuOpen(false);
  };

  const handleReplyMessage = () => {
    if (onReply) {
      onReply(message);
    }
    setIsContextMenuOpen(false);
  };

  const handleTogglePinMessage = () => {
    if (onTogglePin) {
      onTogglePin(message);
    }
    setIsContextMenuOpen(false);
  };

  const handleSaveEdit = () => {
    if (
      editedContent.trim() &&
      (editedContent !== message.content || isRemoveReply)
    ) {
      updateMessage.mutate({
        content: editedContent.trim(),
        replyToMessageId: isRemoveReply
          ? undefined
          : message.replyToMessageId
            ? message.replyToMessageId
            : undefined,
        pinned: message.pinned,
      });
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

  const handleReplyClick = (e: React.MouseEvent) => {
    e.stopPropagation(); // Предотвращаем всплытие события
    if (message.replyToMessageId && scrollToMessage) {
      scrollToMessage(message.replyToMessageId);
    }
  };

  const renderReplyInfo = () => {
    if (!message.replyToMessageId) return null;

    return (
      <div
        className="bg-muted/30 hover:bg-muted/50 mb-1 cursor-pointer rounded px-2 py-1 transition-colors"
        onClick={handleReplyClick}
      >
        <div className="flex items-center">
          <div className="bg-primary mr-2 h-3 w-0.5 rounded-full"></div>
          <div className="flex-1 text-xs leading-tight">
            <div className="text-primary font-semibold">
              {replyToMessage
                ? replyToMessage.author.name
                : "Удалённое сообщение"}
            </div>
            {replyToMessage && (
              <div className="text-muted-foreground truncate">
                {replyToMessage.content.length > 50
                  ? replyToMessage.content.substring(0, 50) + "..."
                  : replyToMessage.content}
              </div>
            )}
          </div>
        </div>
      </div>
    );
  };

  const renderMobileMessage = () => (
    <div
      ref={contentRef}
      className={`flex max-w-[80%] flex-col ${
        isCurrentUser ? "items-end" : "items-start"
      }`}
      onClick={handleMessageClick}
    >
      {renderReplyInfo()}
      <div
        className={`rounded-lg px-3 py-2 ${
          isCurrentUser
            ? "bg-primary text-primary-foreground"
            : "bg-muted text-muted-foreground"
        } ${message.pinned ? "border-primary border" : ""}`}
      >
        {message.pinned && (
          <div className="text-xs text-primary-foreground/70 flex items-center gap-1 mb-1">
            <PinIcon className="h-3 w-3" />
            <span>Закреплено</span>
          </div>
        )}
        <p className="text-sm">{message.content}</p>
      </div>
      <span className="text-muted-foreground mt-1 text-xs">
        {format(message.timestamp, "HH:mm")}
      </span>

      {isContextMenuOpen && contentRef.current && isContextMenuOpen && (
        <div
          ref={contextMenuRef}
          className="bg-popover text-popover-foreground animate-in fade-in-0 zoom-in-95 absolute z-50 min-w-32 rounded-md border p-1 shadow-md"
          style={{
            position: "fixed",
            maxWidth: "calc(100vw - 16px)",
            left: isCurrentUser
              ? "auto"
              : Math.max(contentRef.current.getBoundingClientRect().left, 8),
            right: isCurrentUser
              ? Math.max(window.innerWidth - contentRef.current.getBoundingClientRect().right, 8)
              : "auto",
            top: Math.min(
              contentRef.current.getBoundingClientRect().bottom + 8,
              window.innerHeight - 180
            ),
          }}
        >
          <div
            className="hover:bg-accent hover:text-accent-foreground cursor-pointer px-2 py-1.5 text-sm"
            onClick={handleCopyMessage}
          >
            <ClipboardCopyIcon className="mr-2 inline-block h-4 w-4" />
            <span>Копировать</span>
          </div>
          {onReply && (
            <div
              className="hover:bg-accent hover:text-accent-foreground cursor-pointer px-2 py-1.5 text-sm"
              onClick={handleReplyMessage}
            >
              <ReplyIcon className="mr-2 inline-block h-4 w-4" />
              <span>Ответить</span>
            </div>
          )}
          {onTogglePin && (
            <div
              className="hover:bg-accent hover:text-accent-foreground cursor-pointer px-2 py-1.5 text-sm"
              onClick={handleTogglePinMessage}
            >
              <PinIcon className="mr-2 inline-block h-4 w-4" />
              <span>{message.pinned ? "Открепить" : "Закрепить"}</span>
            </div>
          )}
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
  );

  const renderDesktopMessage = () => (
    <ContextMenu>
      <ContextMenuTrigger>
        <div
          className={`flex max-w-[80%] flex-col ${
            isCurrentUser ? "items-end" : "items-start"
          }`}
        >
          {renderReplyInfo()}
          <div
            className={`rounded-lg px-3 py-2 ${
              isCurrentUser
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground"
            } ${message.pinned ? "border-primary border" : ""}`}
          >
            {message.pinned && (
              <div className="text-xs text-primary-foreground/70 flex items-center gap-1 mb-1">
                <PinIcon className="h-3 w-3" />
                <span>Закреплено</span>
              </div>
            )}
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
        {onReply && (
          <ContextMenuItem onClick={handleReplyMessage}>
            <ReplyIcon className="mr-2 h-4 w-4" />
            <span>Ответить</span>
          </ContextMenuItem>
        )}
        {onTogglePin && (
          <ContextMenuItem onClick={handleTogglePinMessage}>
            <PinIcon className="mr-2 h-4 w-4" />
            <span>{message.pinned ? "Открепить" : "Закрепить"}</span>
          </ContextMenuItem>
        )}
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
  );

  return (
    <>
      <div
        className={`flex items-start gap-3 ${
          isCurrentUser ? "flex-row-reverse" : "flex-row"
        }`}
        id={`message-${message.id}`}
      >
        <Avatar className="h-8 w-8">
          <AvatarImage src="" alt={message.author?.name || ""} />
          <AvatarFallback>
            {message.author?.name ? getInitials(message.author.name) : ""}
          </AvatarFallback>
        </Avatar>
        {isMobile ? renderMobileMessage() : renderDesktopMessage()}
      </div>

      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Изменить сообщение</DialogTitle>
          </DialogHeader>
          {message.replyToMessageId && (
            <div className="text-muted-foreground flex items-center justify-between text-sm">
              <div className="flex-1">
                {replyToMessage ? (
                  <>
                    <span>Ответ на сообщение от </span>
                    <span className="font-semibold">
                      {replyToMessage.author.name}
                    </span>
                  </>
                ) : (
                  <span>Ответ на удалённое сообщение</span>
                )}
              </div>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setIsRemoveReply(!isRemoveReply)}
                className={isRemoveReply ? "text-destructive" : ""}
              >
                {isRemoveReply ? "Восстановить ответ" : "Убрать ответ"}
              </Button>
            </div>
          )}
          <Input
            value={editedContent}
            onChange={(e) => setEditedContent(e.target.value)}
            onKeyDown={handleKeyDown}
          />
          <DialogFooter className="gap-2 sm:gap-0">
            <Button
              type="button"
              variant="secondary"
              onClick={() => setIsEditDialogOpen(false)}
            >
              Отмена
            </Button>
            <Button type="submit" onClick={handleSaveEdit}>
              Сохранить
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
