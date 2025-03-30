import { useState, useEffect } from "react";
import { Poll, Option } from "@/api/get-chat";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { Progress } from "@/components/ui/progress";
import { Button } from "@/components/ui/button";
import { getMeQueryOptions } from "@/api/get-me";
import { useQuery } from "@tanstack/react-query";
import { useUpdateMessage } from "@/api/update-message";
import { toast } from "sonner";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogClose,
} from "@/components/ui/dialog";
import { UserIcon } from "lucide-react";

interface PollDisplayProps {
  poll: Poll;
  chatId?: number;
  messageId?: number;
  messageContent?: string;
  replyToMessageId?: number | null;
  pinned?: boolean;
}

export function PollDisplay({
  poll,
  chatId,
  messageId,
  messageContent = "",
  replyToMessageId = null,
  pinned = false,
}: PollDisplayProps) {
  const [selectedOptions, setSelectedOptions] = useState<number[]>([]);
  const [hasVoted, setHasVoted] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const [isVoting, setIsVoting] = useState(false);
  const [showVotersDialog, setShowVotersDialog] = useState(false);
  const [selectedOptionForVoters, setSelectedOptionForVoters] =
    useState<Option | null>(null);
  const { data: currentUser } = useQuery(getMeQueryOptions());
  const updateMessage = useUpdateMessage();

  const isSingleChoicePoll =
    poll?.pollType === "SINGLE_CHOICE" || poll?.pollType === "SINGLE";

  useEffect(() => {
    if (!poll?.options || !currentUser) return;

    // Проверяем, голосовал ли текущий пользователь в любом из вариантов ответа
    const userHasVoted = poll.options.some((option) =>
      option.voters.some((voter) => voter.id === currentUser.id),
    );

    if (userHasVoted) {
      setHasVoted(true);
      setShowResults(true);

      // Если пользователь уже проголосовал, устанавливаем выбранные опции
      const votedOptions = poll.options
        .filter((option) =>
          option.voters.some((voter) => voter.id === currentUser.id),
        )
        .map((option) => option.id);

      setSelectedOptions(votedOptions);
    } else {
      // Если пользователь не голосовал или отменил голос
      setHasVoted(false);
      setShowResults(false);
      setSelectedOptions([]);
    }
  }, [poll, currentUser]);

  // Получаем общее количество голосов (пользователи могут голосовать за несколько вариантов)
  const getTotalVoters = () => {
    if (!poll?.options) return 0;

    // Получаем уникальных проголосовавших пользователей
    const uniqueVoterIds = new Set<number>();

    poll.options.forEach((option) => {
      option.voters.forEach((voter) => {
        uniqueVoterIds.add(voter.id);
      });
    });

    return uniqueVoterIds.size;
  };

  const totalVoters = getTotalVoters();

  const handleVote = () => {
    if (
      !poll ||
      selectedOptions.length === 0 ||
      !chatId ||
      !messageId ||
      !currentUser
    )
      return;

    setIsVoting(true);

    const updatedPoll = {
      id: poll.id,
      title: poll.title,
      pollType: poll.pollType,
      options: poll.options.map((option) => {
        const voterIds = option.voters.map((voter) => voter.id);

        if (selectedOptions.includes(option.id)) {
          if (!voterIds.includes(currentUser.id)) {
            voterIds.push(currentUser.id);
          }
        } else {
          // Для невыбранных опций удаляем текущего пользователя, если он там есть
          // (это важно для случаев, когда пользователь изменил свой выбор)
          const currentUserIndex = voterIds.indexOf(currentUser.id);
          if (currentUserIndex !== -1) {
            voterIds.splice(currentUserIndex, 1);
          }
        }

        return {
          id: option.id,
          title: option.title,
          voters: voterIds,
        };
      }),
    };

    updateMessage.mutate(
      {
        chatId,
        messageId,
        content: messageContent,
        poll: updatedPoll,
        replyToMessageId: replyToMessageId ?? undefined,
        pinned,
      },
      {
        onSuccess: () => {
          setHasVoted(true);
          setShowResults(true);
          setIsVoting(false);
          toast.success("Голос учтен", {
            description: "Ваш голос был успешно учтен",
          });
        },
        onError: (error) => {
          setIsVoting(false);
          console.error("Ошибка при голосовании:", error);
          toast.error("Ошибка при голосовании", {
            description: "Не удалось отправить ваш голос. Попробуйте снова.",
          });
        },
      },
    );
  };

  const handleOptionSelect = (optionId: number) => {
    if (isSingleChoicePoll) {
      setSelectedOptions([optionId]);
    } else {
      // Для множественного выбора добавляем/удаляем опцию из массива
      if (selectedOptions.includes(optionId)) {
        setSelectedOptions(selectedOptions.filter((id) => id !== optionId));
      } else {
        setSelectedOptions([...selectedOptions, optionId]);
      }
    }
  };

  const getPercentage = (optionId: number) => {
    if (!poll?.options || totalVoters === 0) return 0;

    const option = poll.options.find((opt) => opt.id === optionId);
    if (!option) return 0;

    return Math.round((option.voters.length / totalVoters) * 100);
  };

  const handleShowVoters = (option: Option) => {
    setSelectedOptionForVoters(option);
    setShowVotersDialog(true);
  };

  if (!poll) return null;

  return (
    <>
      <div className="bg-card border-border mt-2 overflow-hidden rounded-md border shadow-sm">
        <div className="bg-secondary p-3">
          <h3 className="text-secondary-foreground text-base font-semibold">
            {poll.title}
          </h3>
          <p className="text-secondary-foreground/70 mt-1 text-xs">
            {isSingleChoicePoll ? "Опрос" : "Опрос с множественным выбором"}
          </p>
        </div>

        <div className="p-4">
          {showResults ? (
            // Показываем результаты опроса
            <div className="space-y-4">
              {poll.options?.map((option) => (
                <div key={option.id} className="space-y-2">
                  <div className="flex justify-between font-medium">
                    <span className="text-foreground">{option.title}</span>
                    <span className="text-primary font-bold">
                      {getPercentage(option.id)}%
                    </span>
                  </div>
                  <Progress
                    value={getPercentage(option.id)}
                    className="h-2.5"
                  />
                  <div className="flex items-center justify-between">
                    <div className="text-muted-foreground text-xs">
                      {option.voters.length}{" "}
                      {option.voters.length === 1
                        ? "голос"
                        : option.voters.length >= 2 && option.voters.length <= 4
                          ? "голоса"
                          : "голосов"}
                    </div>
                    {option.voters.length > 0 && (
                      <Button
                        size="sm"
                        variant="ghost"
                        className="h-6 px-2 text-xs"
                        onClick={() => handleShowVoters(option)}
                      >
                        <UserIcon className="mr-1 h-3 w-3" />
                        Кто голосовал
                      </Button>
                    )}
                  </div>
                </div>
              ))}
              <div className="text-muted-foreground pt-2 text-center text-sm">
                {totalVoters > 0
                  ? `Всего: ${totalVoters} ${
                      totalVoters === 1
                        ? "участник"
                        : totalVoters >= 2 && totalVoters <= 4
                          ? "участника"
                          : "участников"
                    }`
                  : "Нет голосов"}
              </div>
            </div>
          ) : (
            // Показываем форму для голосования
            <div className="space-y-4">
              <div className="text-foreground mb-2 font-medium">
                Выберите вариант ответа:
              </div>
              {isSingleChoicePoll ? (
                <RadioGroup
                  value={selectedOptions[0]?.toString() || ""}
                  onValueChange={(value) => handleOptionSelect(Number(value))}
                  className="space-y-3"
                >
                  {poll.options?.map((option) => (
                    <div
                      key={option.id}
                      className="hover:bg-accent/10 border-muted flex items-center space-x-3 rounded border p-2"
                    >
                      <RadioGroupItem
                        value={option.id.toString()}
                        id={`option-${option.id}`}
                      />
                      <Label
                        htmlFor={`option-${option.id}`}
                        className="text-foreground cursor-pointer font-medium"
                      >
                        {option.title}
                      </Label>
                    </div>
                  ))}
                </RadioGroup>
              ) : (
                <div className="space-y-3">
                  {poll.options?.map((option) => (
                    <div
                      key={option.id}
                      className="hover:bg-accent/10 border-muted flex items-center space-x-3 rounded border p-2"
                    >
                      <Checkbox
                        id={`option-${option.id}`}
                        checked={selectedOptions.includes(option.id)}
                        onCheckedChange={() => handleOptionSelect(option.id)}
                      />
                      <Label
                        htmlFor={`option-${option.id}`}
                        className="text-foreground cursor-pointer font-medium"
                      >
                        {option.title}
                      </Label>
                    </div>
                  ))}
                </div>
              )}

              <Button
                type="button"
                className="mt-4 w-full"
                size="sm"
                disabled={selectedOptions.length === 0 || isVoting}
                onClick={handleVote}
              >
                {isVoting ? "Отправка голоса..." : "Голосовать"}
              </Button>
            </div>
          )}

          {hasVoted && (
            <div className="text-muted-foreground mt-4 flex items-center justify-between border-t pt-4">
              <span className="text-xs">Вы проголосовали</span>
              <Button
                size="sm"
                variant="outline"
                className="text-xs"
                onClick={() => setShowVotersDialog(true)}
              >
                Результаты
              </Button>
            </div>
          )}

          {!hasVoted && showResults && (
            <div className="text-muted-foreground mt-4 flex items-center justify-between border-t pt-4">
              <Button
                size="sm"
                variant="outline"
                className="text-xs"
                onClick={() => setShowResults(false)}
              >
                Проголосовать
              </Button>
              <Button
                size="sm"
                variant="outline"
                className="text-xs"
                onClick={() => setShowVotersDialog(true)}
              >
                Результаты
              </Button>
            </div>
          )}
        </div>
      </div>

      {/* Модальное окно с результатами голосования */}
      <Dialog open={showVotersDialog} onOpenChange={setShowVotersDialog}>
        <DialogContent className="sm:max-w-lg">
          <DialogHeader>
            <DialogTitle>
              {selectedOptionForVoters
                ? `Проголосовавшие за "${selectedOptionForVoters.title}"`
                : "Результаты голосования"}
            </DialogTitle>
          </DialogHeader>

          <div className="mt-2 max-h-[60vh] overflow-y-auto">
            {selectedOptionForVoters ? (
              // Показываем проголосовавших за конкретный вариант
              <div className="space-y-2">
                {selectedOptionForVoters.voters.length > 0 ? (
                  selectedOptionForVoters.voters.map((voter) => (
                    <div
                      key={voter.id}
                      className="flex items-center rounded-md border p-2"
                    >
                      <div className="bg-primary text-primary-foreground mr-3 flex h-8 w-8 items-center justify-center rounded-full">
                        {voter.name?.[0].toUpperCase() || "?"}
                      </div>
                      <div>
                        <div className="font-medium">{voter.name}</div>
                        <div className="text-muted-foreground text-xs">
                          {voter.username}
                        </div>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="text-muted-foreground py-8 text-center">
                    Нет голосов за этот вариант
                  </div>
                )}
              </div>
            ) : (
              // Показываем все варианты и проголосовавших
              <div className="space-y-6">
                {poll.options.map((option) => (
                  <div key={option.id} className="space-y-2">
                    <div className="flex justify-between font-medium">
                      <span>{option.title}</span>
                      <span className="text-primary">
                        {getPercentage(option.id)}%
                      </span>
                    </div>
                    <Progress
                      value={getPercentage(option.id)}
                      className="h-2.5"
                    />

                    <div className="mt-2 space-y-1 pl-2">
                      {option.voters.length > 0 ? (
                        option.voters.map((voter) => (
                          <div
                            key={voter.id}
                            className="flex items-center py-1"
                          >
                            <div className="bg-muted text-muted-foreground mr-2 flex h-6 w-6 items-center justify-center rounded-full text-xs">
                              {voter.name?.[0].toUpperCase() || "?"}
                            </div>
                            <div className="text-sm">{voter.name}</div>
                          </div>
                        ))
                      ) : (
                        <div className="text-muted-foreground py-1 text-xs">
                          Нет голосов
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {selectedOptionForVoters && (
            <Button
              variant="outline"
              onClick={() => {
                setSelectedOptionForVoters(null);
              }}
              className="mt-2"
            >
              Показать все результаты
            </Button>
          )}

          <DialogClose asChild>
            <Button type="button" variant="secondary" className="mt-2">
              Закрыть
            </Button>
          </DialogClose>
        </DialogContent>
      </Dialog>
    </>
  );
}
