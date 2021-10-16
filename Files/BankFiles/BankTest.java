package practical_16_solutions;

import static org.junit.Assert.*;

import org.junit.Test;

public class BankTest {
	
	String s;

	@Test
	public void kidsAccTest() {
		// Create a Childrens' Account with Account Number 1,
		// opening balance 50 and a bonus of 3
		KidsAcc acc1 = new KidsAcc(1, 50, 3);
		acc1.deposit(10);
		// try and withdraw 64 pounds - should not allow only 60 pounds
		assertEquals(63, acc1.getBalance(), 0.0);
		assertFalse(acc1.withdraw(64));
		acc1.setBonus(4);
		acc1.deposit(20);
		// 3rd arg is the delta/difference allowed between the 1st and 2nd args - use when real nos
		assertEquals(87, acc1.getBalance(), 0.0);
		assertTrue(acc1.withdraw(17));
		assertEquals(70, acc1.getBalance(), 0.0);
	}
	
	@Test
	public void saveAccTest() {
		// Create a Savings Account with Account number 2, opening balance 100,
		// and interest rate 5
		SaveAcc acc2 = new SaveAcc(2, 100, 5);
		acc2.addInterest();
		// �100 + 5% interest
		assertEquals(105, acc2.getBalance(), 0.0);
		acc2.deposit(45);
		assertEquals(150, acc2.getBalance(), 0.0);
		assertFalse(acc2.withdraw(151));
		// Set Account number 2 interest rate to 6%
		acc2.setRate(6);
		// Add interest to Account number 2
		acc2.addInterest();
		assertEquals(159, acc2.getBalance(), 0.0);
		// Withdraw �59 from Account number 2
		// Should leave final balance of 100 pounds
		assertTrue(acc2.withdraw(59));
		assertEquals(100, acc2.getBalance(), 0.0);
	}
	
	@Test
	public void goldAccTest() {
		// Create a Gold Account with Account number 3, opening balance 500,
		// interest rate 10, and a minimum balance of 500 pounds.
		GoldAcc acc3 = new GoldAcc(3, 500, 10, 500);
		acc3.deposit(500);
		acc3.addInterest();
		// Should now have 1100 quid in it ...
		assertEquals(1100, acc3.getBalance(), 0.0);
		// Try and withdraw 601 pounds from Account number 3 should not allow
		assertFalse(acc3.withdraw(601));
		acc3.setMinBalance(400);
		acc3.setRate(8);
		acc3.addInterest();
		// Try and withdraw 788 pounds from Account number 3
		// Should permit, leaving final balance of 400 pounds
		assertTrue(acc3.withdraw(788));
		assertEquals(400, acc3.getBalance(), 0.0);
	}
}
