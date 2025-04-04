import { useCallback, useRef, useState } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { format } from "date-fns";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import {
  ContextMenu,
  ContextMenuContent,
  ContextMenuItem,
  ContextMenuTrigger,
} from "@/components/ui/context-menu";
import { useDeleteMessage } from "@/api/delete-message";
import { useUpdateMessage } from "@/api/update-message";
import { Message } from "@/api/get-chat";
import { useOutsideClick } from "@/hooks/useOutsideClick";
import { getMessagesQueryOptions } from "@/api/get-messages";
import { useQueryClient } from "@tanstack/react-query";
import {
  ClipboardCopyIcon,
  Edit as EditIcon,
  Trash as TrashIcon,
  ReplyIcon,
  PinIcon,
  XCircleIcon,
} from "lucide-react";
import { isMobileDevice } from "@/lib/utils";
import { getMeQueryOptions } from "@/api/get-me";
import { useQuery } from "@tanstack/react-query";
import { PollDisplay } from "./PollDisplay";
import { PollUpdateRequest } from "@/api/api.gen";
import { toast } from "sonner";

type MessageItemProps = {
  message: Message;
  chatId: number;
  onReply?: (message: Message) => void;
  scrollToMessage?: (messageId: number) => void;
  focusInputField?: () => void;
};

export function MessageItem({
  message,
  chatId,
  onReply,
  scrollToMessage,
  focusInputField,
}: MessageItemProps) {
  const [isContextMenuOpen, setIsContextMenuOpen] = useState(false);
  const [clickPosition, setClickPosition] = useState({ x: 0, y: 0 });
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [editedContent, setEditedContent] = useState(message.content);
  const messageRef = useRef<HTMLDivElement>(null);
  const contextMenuRef = useRef<HTMLDivElement>(null);
  const deleteMessage = useDeleteMessage();
  const updateMessage = useUpdateMessage();
  const queryClient = useQueryClient();
  const isMobile = isMobileDevice();

  // Получаем данные текущего пользователя
  const { data: currentUser } = useQuery(getMeQueryOptions());

  // Проверяем, является ли сообщение сообщением текущего пользователя
  const isCurrentUser = currentUser?.id === message.author.id;

  // Проверяем, голосовал ли текущий пользователь в этом опросе
  const hasVotedInPoll =
    currentUser &&
    message.poll &&
    message.poll.options.some((option) =>
      option.voters.some((voter) => voter.id === currentUser.id),
    );

  const replyToMessage = queryClient
    .getQueryData<Message[]>(getMessagesQueryOptions(chatId).queryKey)
    ?.find((msg) => msg.id === message.replyToMessageId);

  // Определяем, нужен ли перенос строк для содержимого сообщения
  const isShortMessage = message.content.length <= 35;

  // Функция для преобразования Poll в PollUpdateRequest с корректными voters
  const formatPollForUpdate = (
    messagePoll: Message["poll"],
  ): PollUpdateRequest | undefined => {
    if (!messagePoll) return undefined;

    return {
      id: messagePoll.id,
      title: messagePoll.title,
      pollType: messagePoll.pollType,
      options: messagePoll.options.map((option) => ({
        id: option.id,
        title: option.title,
        voters: option.voters.map((voter) => voter.id),
      })),
    };
  };

  useOutsideClick(messageRef, () => {
    if (isContextMenuOpen) {
      setIsContextMenuOpen(false);
    }
  });

  const handleMessageClick = useCallback(
    (e: React.MouseEvent) => {
      if (isMobile) {
        e.preventDefault();
        setClickPosition({ x: e.clientX, y: e.clientY });
        setIsContextMenuOpen(true);
      }
    },
    [isMobile],
  );

  const handleCopyMessage = () => {
    navigator.clipboard.writeText(message.content);
    setIsContextMenuOpen(false);
  };

  const handleEditMessage = () => {
    setIsEditDialogOpen(true);
    setIsContextMenuOpen(false);
  };

  const handleReplyMessage = () => {
    if (onReply) {
      onReply(message);
      setIsContextMenuOpen(false);

      if (focusInputField) {
        setTimeout(() => {
          focusInputField();
        }, 0);
      }
    }
  };

  const handlePinMessage = () => {
    updateMessage.mutate({
      chatId,
      messageId: message.id,
      content: message.content,
      pinned: message.pinned !== undefined ? !message.pinned : true,
      replyToMessageId: message.replyToMessageId || undefined,
      poll: formatPollForUpdate(message.poll),
    });
    setIsContextMenuOpen(false);
  };

  const handleRemoveVote = () => {
    if (!message.poll || !currentUser) return;

    // Создаем объект для обновления опроса без голоса текущего пользователя
    const updatedPoll = {
      id: message.poll.id,
      title: message.poll.title,
      pollType: message.poll.pollType,
      options: message.poll.options.map((option) => ({
        id: option.id,
        title: option.title,
        // Удаляем голос текущего пользователя из списка голосов
        voters: option.voters
          .filter((voter) => voter.id !== currentUser.id)
          .map((voter) => voter.id),
      })),
    };

    updateMessage.mutate(
      {
        chatId,
        messageId: message.id,
        content: message.content,
        replyToMessageId: message.replyToMessageId || undefined,
        pinned: message.pinned !== undefined ? message.pinned : false,
        poll: updatedPoll,
      },
      {
        onSuccess: () => {
          setIsContextMenuOpen(false);
          toast.success("Голос отменен", {
            description: "Ваш голос был удален из результатов опроса",
          });
        },
        onError: (error) => {
          console.error("Ошибка при отмене голоса:", error);
          toast.error("Ошибка при отмене голоса", {
            description: "Не удалось отменить ваш голос. Попробуйте снова.",
          });
        },
      },
    );
  };

  const handleSaveEdit = () => {
    if (editedContent.trim() !== message.content) {
      updateMessage.mutate({
        chatId,
        messageId: message.id,
        content: editedContent.trim(),
        replyToMessageId: message.replyToMessageId || undefined,
        pinned: message.pinned !== undefined ? message.pinned : false,
        poll: formatPollForUpdate(message.poll),
      });
      setIsEditDialogOpen(false);
    } else {
      setIsEditDialogOpen(false);
    }
  };

  const handleDeleteMessage = () => {
    deleteMessage.mutate({ chatId, messageId: message.id });
    setIsContextMenuOpen(false);
  };

  const handleReplyClick = useCallback(() => {
    if (scrollToMessage && message.replyToMessageId) {
      scrollToMessage(message.replyToMessageId);
    }
  }, [message.replyToMessageId, scrollToMessage]);

  // Обработчик нажатия клавиш в поле ввода
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSaveEdit();
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

  return (
    <div
      ref={messageRef}
      className={`mb-4 flex items-start gap-3 ${isCurrentUser ? "flex-row-reverse" : "flex-row"}`}
      id={`message-${message.id}`}
    >
      <Avatar className="h-8 w-8 shrink-0">
        <AvatarImage src="" alt={message.author.name || ""} />
        <AvatarFallback>
          {message.author.name?.[0].toUpperCase() || ""}
        </AvatarFallback>
      </Avatar>

      <div
        className="group relative"
        onClick={isMobile ? handleMessageClick : undefined}
      >
        {renderReplyInfo()}
        <ContextMenu>
          <ContextMenuTrigger>
            <div
              className={`flex max-w-[400px] flex-col md:max-w-[500px] ${isCurrentUser ? "items-end" : "items-start"} `}
            >
              <div
                className={`rounded-lg px-3 py-2 ${
                  isCurrentUser
                    ? "bg-primary text-primary-foreground"
                    : "bg-muted text-muted-foreground"
                } `}
              >
                <p
                  className={
                    isShortMessage
                      ? "inline-block text-sm"
                      : "text-sm break-words whitespace-pre-wrap"
                  }
                  style={{ wordBreak: "break-all", overflowWrap: "anywhere" }}
                >
                  {message.content}
                </p>
              </div>
              {/* Отображаем опрос под сообщением, если он есть */}
              {message.poll && (
                <div className="mt-2 w-full">
                  <PollDisplay
                    poll={message.poll}
                    chatId={chatId}
                    messageId={message.id}
                    messageContent={message.content}
                    replyToMessageId={message.replyToMessageId}
                    pinned={message.pinned}
                  />
                </div>
              )}
              <div className="mt-1 flex items-center gap-1">
                <span className="text-muted-foreground text-xs">
                  {format(message.timestamp, "HH:mm")}
                </span>
                {message.pinned && (
                  <PinIcon className="text-muted-foreground h-3 w-3 shrink-0" />
                )}
              </div>
            </div>
          </ContextMenuTrigger>
          {!isMobile && (
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
              <ContextMenuItem onClick={handlePinMessage}>
                <PinIcon className="mr-2 h-4 w-4" />
                <span>{message.pinned ? "Открепить" : "Закрепить"}</span>
              </ContextMenuItem>
              {message.poll && hasVotedInPoll && (
                <ContextMenuItem onClick={handleRemoveVote}>
                  <XCircleIcon className="mr-2 h-4 w-4" />
                  <span>Отменить голос</span>
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
          )}
        </ContextMenu>
      </div>

      {isContextMenuOpen && isMobile && (
        <div
          ref={contextMenuRef}
          className="bg-popover text-popover-foreground fixed z-50 flex min-w-40 flex-col rounded-md border py-1 shadow-md"
          style={{
            top: `${clickPosition.y}px`,
            left: isCurrentUser ? "auto" : `${clickPosition.x}px`,
            right: isCurrentUser
              ? `${window.innerWidth - clickPosition.x}px`
              : "auto",
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
          <div
            className="hover:bg-accent hover:text-accent-foreground cursor-pointer px-2 py-1.5 text-sm"
            onClick={handlePinMessage}
          >
            <PinIcon className="mr-2 inline-block h-4 w-4" />
            <span>{message.pinned ? "Открепить" : "Закрепить"}</span>
          </div>
          {message.poll && hasVotedInPoll && (
            <div
              className="hover:bg-accent hover:text-accent-foreground cursor-pointer px-2 py-1.5 text-sm"
              onClick={handleRemoveVote}
            >
              <XCircleIcon className="mr-2 inline-block h-4 w-4" />
              <span>Отменить голос</span>
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
                className="hover:bg-accent hover:text-accent-foreground text-destructive cursor-pointer px-2 py-1.5 text-sm"
                onClick={handleDeleteMessage}
              >
                <TrashIcon className="mr-2 inline-block h-4 w-4" />
                <span>Удалить</span>
              </div>
            </>
          )}
        </div>
      )}

      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Изменить сообщение</DialogTitle>
          </DialogHeader>
          <div className="flex items-center">
            <Input
              value={editedContent}
              onChange={(e) => setEditedContent(e.target.value)}
              onKeyDown={handleKeyDown}
            />
          </div>
          <DialogFooter>
            <Button
              type="submit"
              onClick={handleSaveEdit}
              disabled={!editedContent.trim()}
            >
              Сохранить
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
