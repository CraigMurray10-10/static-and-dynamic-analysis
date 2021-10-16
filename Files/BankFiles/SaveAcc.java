package practical_16_solutions;

public class SaveAcc extends BasicAccount {
	
	private int intRate;

	public SaveAcc(int acc, double bal, int rate) {
		super(acc, bal);
		intRate = rate;
	}

	public void setRate(int newRate) {
		intRate = newRate;
	}

	public int getRate() {
		return intRate;
	}

	public void addInterest() {
		double interest = super.getBalance() * intRate / 100;
		super.deposit(interest);
	}
	
}
