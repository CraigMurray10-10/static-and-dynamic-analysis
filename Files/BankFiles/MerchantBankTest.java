package practical_16_solutions;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import practical_16_solutions.MerchantBank;

// Test Class for Practical 15

public class MerchantBankTest {

	MerchantBank bankOfAyrshire;

	// This setUp method is run before each individual test
	@Before
	public void beforeAllTests() {
		bankOfAyrshire = new MerchantBank();

		bankOfAyrshire.openAccount(123, 'K');
		bankOfAyrshire.openAccount(456, 'S');
		bankOfAyrshire.openAccount(789, 'B');

		bankOfAyrshire.deposit(123, 100);
		bankOfAyrshire.deposit(456, 200);
		bankOfAyrshire.deposit(789, 300);
	}

	@Test
	public void totalAssetsAtStart() {
		assertEquals(601.0, bankOfAyrshire.totalAssets(), 0.0);
	}

	@Test
	public void totalAssetsAtEnd() {
		// Kids
		assertTrue(bankOfAyrshire.withdraw(123, 101));
		assertEquals(0.0, bankOfAyrshire.balance(123), 0.0);
		// Savings
		assertTrue(bankOfAyrshire.withdraw(456, 1));
		assertEquals(199.0, bankOfAyrshire.balance(456), 0.0);
		// Basic
		assertTrue(bankOfAyrshire.withdraw(789, 300));
		assertEquals(0.0, bankOfAyrshire.balance(789), 0.0);
		// Savings
		bankOfAyrshire.deposit(456, 2500);
		assertEquals(2699.0, bankOfAyrshire.balance(456), 0.0);	
		// Kids
		bankOfAyrshire.deposit(123, 10);
		assertEquals(11.0, bankOfAyrshire.balance(123), 0.0);	
		assertEquals(2710.0, bankOfAyrshire.totalAssets(), 0.0);
	}

}