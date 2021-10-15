package practical_16_solutions;

public class GoldAcc extends SaveAcc {
	
	private double minBalance;

	public GoldAcc(int acc, double bal, int rate, double min) {
		super(acc, bal, rate);
		minBalance = min;
	}

	public void setMinBalance(double minB) {
		minBalance = minB;
	}

	public double getMinBalance() {
		return minBalance;
	}

	public boolean withdraw(double amount) {
		if (super.getBalance() - amount >= minBalance) {
			return super.withdraw(amount);
		} else
			return false;
	}

}
