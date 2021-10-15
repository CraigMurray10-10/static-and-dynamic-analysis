package practical_16_solutions;

public class KidsAcc extends BasicAccount {
	
	private int bonus;

	public KidsAcc(int acc, double bal, int b) {
		super(acc, bal);
		bonus = b;
	}

	public void setBonus(int b) {
		bonus = b;
	}

	public int getBonus() {
		return bonus;
	}

	public void deposit(double dep) {
		super.deposit(dep + bonus);
	}
	
}
