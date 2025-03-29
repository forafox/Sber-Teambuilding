export function getAvatarFallback(fullName: string) {
  return fullName
    .split(" ")
    .map((name) => name[0])
    .join("");
}
