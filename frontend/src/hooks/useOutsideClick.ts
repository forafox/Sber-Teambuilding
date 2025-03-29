import { useEffect, RefObject } from "react";

/**
 * Хук для обработки кликов вне указанного элемента
 * @param ref Реф элемента
 * @param callback Функция, которая будет вызвана при клике вне элемента
 */
export function useOutsideClick<T extends HTMLElement>(
  ref: RefObject<T | null>,
  callback: () => void,
): void {
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        callback();
      }
    }

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [ref, callback]);
}
