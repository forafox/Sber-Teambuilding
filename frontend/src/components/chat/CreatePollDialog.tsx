import { useState, useRef, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import { XIcon } from "lucide-react";
import { PollRequest } from "@/api/api.gen";

interface CreatePollDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onCreatePoll: (poll: PollRequest) => void;
}

export function CreatePollDialog({
  open,
  onOpenChange,
  onCreatePoll,
}: CreatePollDialogProps) {
  const [pollTitle, setPollTitle] = useState("");
  const [pollType, setPollType] = useState<"SINGLE" | "MULTIPLE">("SINGLE");
  const [options, setOptions] = useState<{ title: string }[]>([
    { title: "" },
    { title: "" },
  ]);
  const [titleError, setTitleError] = useState<string | null>(null);
  const [optionsError, setOptionsError] = useState<string | null>(null);
  const lastInputRef = useRef<HTMLInputElement>(null);
  const currentFocusedIndex = useRef<number | null>(null);

  useEffect(() => {
    if (
      options.length > 0 &&
      lastInputRef.current &&
      currentFocusedIndex.current === null
    ) {
      lastInputRef.current.focus();
    }
  }, [options.length]);

  const handleTitleChange = (value: string) => {
    setPollTitle(value);
    if (value.trim() !== "") {
      setTitleError(null);
    }
  };

  const handleAddOption = () => {
    if (options.length < 10) {
      setOptions([...options, { title: "" }]);
    }
  };

  const handleRemoveOption = (index: number) => {
    if (options.length > 2) {
      setOptions(options.filter((_, i) => i !== index));
    }
  };

  const handleOptionChange = (index: number, value: string) => {
    const updatedOptions = [...options];
    updatedOptions[index].title = value;
    setOptions(updatedOptions);

    const filledOptions = updatedOptions.filter(
      (opt) => opt.title.trim() !== "",
    ).length;
    if (filledOptions >= 2) {
      setOptionsError(null);
    }

    if (
      index === options.length - 1 &&
      value.trim() !== "" &&
      options.length < 10
    ) {
      setOptions([...updatedOptions, { title: "" }]);
    }
  };

  const handleOptionKeyDown = (
    index: number,
    e: React.KeyboardEvent<HTMLInputElement>,
  ) => {
    if (
      e.key === "Enter" &&
      index === options.length - 1 &&
      options[index].title.trim() !== "" &&
      options.length < 10
    ) {
      e.preventDefault();
      handleAddOption();
    }
  };

  const handleOptionFocus = (index: number) => {
    currentFocusedIndex.current = index;
  };

  const handleOptionBlur = () => {
    currentFocusedIndex.current = null;
  };

  const handleSubmit = () => {
    let hasError = false;

    if (pollTitle.trim() === "") {
      setTitleError("Необходимо указать название опроса");
      hasError = true;
    }

    const validOptions = options.filter((opt) => opt.title.trim() !== "");

    if (validOptions.length < 2) {
      setOptionsError(
        "Необходимо задать название хотя бы для двух вариантов ответа",
      );
      hasError = true;
    }

    if (!hasError) {
      onCreatePoll({
        title: pollTitle,
        pollType: pollType,
        options: validOptions,
      });

      setPollTitle("");
      setPollType("SINGLE");
      setOptions([{ title: "" }, { title: "" }]);
      setTitleError(null);
      setOptionsError(null);

      onOpenChange(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Создать опрос</DialogTitle>
        </DialogHeader>

        <div className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="poll-title">Вопрос</Label>
            <Input
              id="poll-title"
              placeholder="Введите вопрос опроса"
              value={pollTitle}
              onChange={(e) => handleTitleChange(e.target.value)}
              className={titleError ? "border-red-500" : ""}
            />
            {titleError && (
              <p className="mt-1 text-xs text-red-500">{titleError}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label>Тип опроса</Label>
            <RadioGroup
              value={pollType}
              onValueChange={(value) =>
                setPollType(value as "SINGLE" | "MULTIPLE")
              }
              className="flex space-x-4"
            >
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="SINGLE" id="single" />
                <Label htmlFor="single">Одиночный выбор</Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="MULTIPLE" id="multiple" />
                <Label htmlFor="multiple">Множественный выбор</Label>
              </div>
            </RadioGroup>
          </div>

          <div className="space-y-2">
            <Label>Варианты ответа</Label>
            <div className="space-y-2">
              {options.map((option, index) => (
                <div key={index} className="flex items-center space-x-2">
                  <Input
                    ref={index === options.length - 1 ? lastInputRef : null}
                    placeholder={`Вариант ${index + 1}`}
                    value={option.title}
                    onChange={(e) => handleOptionChange(index, e.target.value)}
                    onKeyDown={(e) => handleOptionKeyDown(index, e)}
                    onFocus={() => handleOptionFocus(index)}
                    onBlur={handleOptionBlur}
                    className={
                      optionsError && index < 2 ? "border-red-500" : ""
                    }
                  />
                  {index > 1 && (
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon"
                      onClick={() => handleRemoveOption(index)}
                    >
                      <XIcon className="h-4 w-4" />
                    </Button>
                  )}
                </div>
              ))}

              {optionsError && (
                <p className="mt-1 text-xs text-red-500">{optionsError}</p>
              )}

              {options.length < 10 && (
                <div className="text-muted-foreground text-xs">
                  Можно добавить еще {10 - options.length} вариантов ответа.
                </div>
              )}
            </div>
          </div>
        </div>

        <DialogFooter>
          <Button type="button" onClick={handleSubmit}>
            Создать опрос
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
