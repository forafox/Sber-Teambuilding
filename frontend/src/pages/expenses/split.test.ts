import { describe, it, expect } from "vitest";
import { calculateBalances } from "./logic";
import { Task } from "@/api/get-tasks";
import { User } from "@/api/get-users";

describe("calculateBalances", () => {
  // Test helpers
  const createUser = (username: string): User => ({
    id: 1,
    name: username,
    username,
    email: `${username}@example.com`,
    role: "USER",
  });

  const createTask = (assignee: string, expenses: number): Task => ({
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
    assignee: {
      id: 2,
      name: assignee,
      username: assignee,
      email: assignee,
      role: "USER",
    },
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
  });

  it("returns empty array if equal expenses", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 200 },
      { username: "user2", amount: 200 },
    ]);

    const result = calculateBalances(tasks, participants);

    expect(result).toEqual([]);
  });

  it("returns one transaction if one participant owes money", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 200 },
      { username: "user2", amount: 100 },
    ]);

    const result = calculateBalances(tasks, participants);

    expect(result).toEqual([{ from: "user2", to: "user1", amount: 50 }]);
  });

  it("handles multiple transactions with three participants", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 300 },
      { username: "user2", amount: 150 },
      { username: "user3", amount: 0 },
    ]);

    const result = calculateBalances(tasks, participants);

    // Expected: Each should pay 150, so user2 owes nothing, user3 owes 150
    expect(result).toHaveLength(1);
    expect(result).toContainEqual({ from: "user3", to: "user1", amount: 150 });
  });

  it("handles complex scenario with multiple participants and transactions", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 500 },
      { username: "user2", amount: 0 },
      { username: "user3", amount: 100 },
      { username: "user4", amount: 200 },
    ]);

    const result = calculateBalances(tasks, participants);

    // Expected fair share: 800 / 4 = 200 per person
    // user1 paid 300 more than fair share
    // user2 owes 200, user3 owes 100
    expect(result).toHaveLength(2);

    // Verify amounts are correct, but don't depend on specific order
    const totalToUser1 = result
      .filter((tx) => tx.to === "user1")
      .reduce((sum, tx) => sum + tx.amount, 0);

    expect(totalToUser1).toBe(300);

    // Verify each debtor is paying the correct amount
    const user2Paying = result.find((tx) => tx.from === "user2")?.amount;
    const user3Paying = result.find((tx) => tx.from === "user3")?.amount;

    expect(user2Paying).toBe(200);
    expect(user3Paying).toBe(100);
  });

  it("handles one participant paying for everything", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 900 },
      { username: "user2", amount: 0 },
      { username: "user3", amount: 0 },
    ]);

    const result = calculateBalances(tasks, participants);

    // Fair share: 300 per person, user1 paid 600 more
    expect(result).toHaveLength(2);

    const totalToUser1 = result
      .filter((tx) => tx.to === "user1")
      .reduce((sum, tx) => sum + tx.amount, 0);

    expect(totalToUser1).toBe(600);
    expect(result.every((tx) => tx.to === "user1")).toBe(true);
    expect(result.some((tx) => tx.from === "user2")).toBe(true);
    expect(result.some((tx) => tx.from === "user3")).toBe(true);
  });

  it("rounds transaction amounts to 2 decimal places", () => {
    const { tasks, participants } = setupTestScenario([
      { username: "user1", amount: 100 },
      { username: "user2", amount: 0 },
      { username: "user3", amount: 0 },
    ]);

    const result = calculateBalances(tasks, participants);

    // Fair share: 33.33 per person
    result.forEach((transaction) => {
      // Check that amount has at most 2 decimal places
      const decimalPlaces = (transaction.amount.toString().split(".")[1] || "")
        .length;
      expect(decimalPlaces).toBeLessThanOrEqual(2);
    });
  });

  it("returns empty array when there are no tasks", () => {
    const participants = [createUser("user1"), createUser("user2")];
    const result = calculateBalances([], participants);

    expect(result).toEqual([]);
  });
});
