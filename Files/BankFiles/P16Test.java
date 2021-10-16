package practical_16_solutions;

import practical_16_solutions.MerchantBank;

public class P16Test {
	   public static void main(String[] args) {
		MerchantBank mb = new MerchantBank();
		mb.loadBank();
		System.out.println(mb.totalAssets());		// Writes to Eclipse Console
		mb.howMuchDosh();
	    }
}
