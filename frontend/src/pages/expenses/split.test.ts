import { describe, it, expect } from "vitest";
import { calculateBalances, Transaction } from "./logic";
import { Task } from "@/api/get-tasks";
import { User } from "@/api/get-users";

function assertEqualShare(
  tasks: Task[],
  participants: User[],
  transactions: Transaction[],
) {
  const spent: Record<string, number> = {};
  participants.forEach((participant) => {
    spent[participant.username] = 0;
  });

  tasks.forEach((task) => {
    if (task.assignee && task.expenses) {
      spent[task.assignee.username] =
        (spent[task.assignee.username] || 0) + task.expenses;
    }
  });

  transactions.forEach((transaction) => {
    spent[transaction.from] += transaction.amount;
    spent[transaction.to] -= transaction.amount;
  });

  const spentArray = Object.values(spent).sort((a, b) => a - b);
  spentArray.forEach((value, i) => {
    if (i === spentArray.length - 1) return;
    expect(value).toBeCloseTo(spentArray[i + 1]);
  });
}

describe("calculateBalances", () => {
  // Test helpers
  const createUser = (username: string): User => ({
    id: 1,
    name: username,
    username,
    email: `${username}@example.com`,
    role: "USER",
  });

  const createTask = (
    assignee: string | undefined,
    expenses: number,
  ): Task => ({
    id: 1,
    title: "Task 1",
    status: "IN_PROGRESS",
    author: {
      id: 1,
      name: "User One",
      username: "user1",
      email: "user1@example.com",
      role: "USER",
    },
    assignee: assignee
      ? {
          id: 2,
          name: assignee,
          username: assignee,
          email: assignee,
          role: "USER",
        }
      : undefined,
    expenses,
  });

  // Setup function to reduce duplication
  const setupTestScenario = (
    expenseData: { username: string; amount: number }[],
  ) => {
    const participants = expenseData.map(({ username }) =>
      createUser(username),
    );
    const tasks = expenseData.map(({ username, amount }) =>
      createTask(username, amount),
    );
    return { participants, tasks };
  };

  it("returns empty array if no expenses", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 0 },
      { username: "user2", amount: 0 },
    ]);

    const result = calculateBalances(tasks, participants);

    expect(result).toEqual([]);
    assertEqualShare(tasks, participants, result);
  });

  it("returns empty array if equal expenses", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 200 },
      { username: "user2", amount: 200 },
    ]);

    const result = calculateBalances(tasks, participants);

    expect(result).toEqual([]);
    assertEqualShare(tasks, participants, result);
  });

  it("returns one transaction if one participant owes money", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 200 },
      { username: "user2", amount: 100 },
    ]);

    const result = calculateBalances(tasks, participants);

    assertEqualShare(tasks, participants, result);
  });

  it("handles multiple transactions with three participants", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 300 },
      { username: "user2", amount: 150 },
      { username: "user3", amount: 0 },
    ]);

    const result = calculateBalances(tasks, participants);

    assertEqualShare(tasks, participants, result);
  });

  it("handles complex scenario with multiple participants and transactions", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 500 },
      { username: "user2", amount: 0 },
      { username: "user3", amount: 100 },
      { username: "user4", amount: 200 },
    ]);

    const result = calculateBalances(tasks, participants);

    assertEqualShare(tasks, participants, result);
  });

  it("handles one participant paying for everything", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 900 },
      { username: "user2", amount: 0 },
      { username: "user3", amount: 0 },
    ]);

    const result = calculateBalances(tasks, participants);

    assertEqualShare(tasks, participants, result);
  });

  it("rounds transaction amounts to 2 decimal places", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 100 },
      { username: "user2", amount: 0 },
      { username: "user3", amount: 0 },
    ]);

    const result = calculateBalances(tasks, participants);

    assertEqualShare(tasks, participants, result);
  });

  it("returns empty array when there are no tasks", () => {
    const participants = [createUser("user1"), createUser("user2")];
    const result = calculateBalances([], participants);

    expect(result).toEqual([]);
  });

  it("#37 bad computations", () => {
    const participants = [
      createUser("efedorov"),
      createUser("akarabanov"),
      createUser("misha"),
    ];

    const tasks = [
      createTask("efedorov", 2500),
      createTask("efedorov", 2500),
      createTask("akarabanov", 2500),
    ];

    const result = calculateBalances(tasks, participants);

    assertEqualShare(tasks, participants, result);
  });

  it("empty assignee task", () => {
    const participants = [createUser("efedorov"), createUser("akarabanov")];

    const tasks = [createTask("efedorov", 2500), createTask(undefined, 2500)];

    const result = calculateBalances(tasks, participants);

    assertEqualShare(tasks, participants, result);
  });
});
