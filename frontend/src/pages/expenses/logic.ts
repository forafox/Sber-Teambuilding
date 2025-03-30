import { Task } from "@/api/get-tasks";
import { User } from "@/api/get-users";

export interface Transaction {
  from: string;
  to: string;
  amount: number;
}

export function calculateBalances(
  tasks: Task[],
  participants: User[],
  transactionsHistory: Transaction[] = [],
): Transaction[] {
  // Calculate total expenses from all tasks
  const totalExpenses = tasks
    .filter((task) => task.assignee)
    .reduce((sum, task) => sum + (task.expenses || 0), 0);

  // Calculate fair share per participant
  const fairShare = totalExpenses / participants.length;

  // Calculate how much each person has already spent
  const spent: Record<string, number> = {};

  // Initialize all participants with zero spent
  participants.forEach((participant) => {
    spent[participant.username] = 0;
  });

  // Add up expenses for each assignee
  tasks.forEach((task) => {
    if (task.assignee && task.expenses) {
      spent[task.assignee.username] =
        (spent[task.assignee.username] || 0) + task.expenses;
    }
  });

  // Calculate balance for each participant (positive means they owe money, negative means they are owed)
  const balances: Record<string, number> = {};

  participants.forEach((participant) => {
    balances[participant.username] =
      fairShare - (spent[participant.username] || 0);
  });

  transactionsHistory.forEach((transaction) => {
    balances[transaction.from] -= transaction.amount;
    balances[transaction.to] += transaction.amount;
  });

  // Calculate transactions directly
  const transactions: Transaction[] = [];

  // Identify debtors (owe money) and creditors (are owed money)
  const debtors = Object.entries(balances)
    .filter(([, amount]) => amount > 0)
    .map(([username, amount]) => ({ username, amount }))
    .sort((a, b) => b.amount - a.amount);

  const creditors = Object.entries(balances)
    .filter(([, amount]) => amount < 0)
    .map(([username, amount]) => ({ username, amount: Math.abs(amount) }))
    .sort((a, b) => b.amount - a.amount);

  let debtorIndex = 0;
  let creditorIndex = 0;

  // Create transactions until all balances are settled
  while (debtorIndex < debtors.length && creditorIndex < creditors.length) {
    const debtor = debtors[debtorIndex];
    const creditor = creditors[creditorIndex];

    // Determine transaction amount (minimum of what's owed and what's due)
    const amount = Math.min(debtor.amount, creditor.amount);

    if (amount > 0) {
      transactions.push({
        from: debtor.username,
        to: creditor.username,
        amount: amount,
      });
    }

    // Update remaining balances
    debtor.amount -= amount;
    creditor.amount -= amount;

    // Move to next debtor/creditor if their balance is settled
    if (Math.abs(debtor.amount) < 0.01) debtorIndex++;
    if (Math.abs(creditor.amount) < 0.01) creditorIndex++;
  }

  return transactions;
}
