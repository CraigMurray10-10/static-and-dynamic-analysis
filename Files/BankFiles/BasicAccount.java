package practical_16_solutions;

/**
 * Simple bank account (base class for accounts inheritance hierarchy)
 **/

public class BasicAccount {
	
	private double balance;
	private int accNumber;

	public BasicAccount(int acc, double bal) {
		accNumber = acc;
		balance = bal;
	}

	public double getBalance() {
		return balance;
	}

	public void deposit(double deposit) {
		balance = balance + deposit;
	}

	public boolean withdraw(double amount) {
		if (balance >= amount) {
			balance = balance - amount;
			return true;
		} else
			return false;
	}

	public int getAccNumber() {
		return accNumber;
	}
	
}