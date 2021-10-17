package practical_16_solutions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// Class to represent a bank.
<<<<<<< HEAD
// Holds multiple accounts and has facilities to open accounts,
=======
// Holds multiple accounts and has facilities to open accounts, 
>>>>>>> LCOM1
// deposit and withdraw from accounts.

public class MerchantBank {

	private ArrayList<BasicAccount> accounts;

	public MerchantBank() {
		accounts = new ArrayList<BasicAccount>();
	}

	public void openAccount(int n, char t) {
		BasicAccount acc;
		if (t == 'K')
			acc = new KidsAcc(n, 0.0, 1);
		else if (t == 'S')
			acc = new SaveAcc(n, 0.0, 3);
		else
			acc = new BasicAccount(n, 0.0);
		accounts.add(acc);
	}

	public void deposit(int n, double d) {
		BasicAccount acc = findAcc(n);
		acc.deposit(d);
	}

	public boolean withdraw(int n, double d) {
		BasicAccount acc = findAcc(n);
		return acc.withdraw(d);
	}

	public double balance(int n) {
		BasicAccount acc = findAcc(n);
		return acc.getBalance();
	}

	public double totalAssets() {
		double total = 0;
		for (BasicAccount acc : accounts) {
			total = total + acc.getBalance();
		}
		return total;
	}

	private BasicAccount findAcc(int n) {
		for (BasicAccount acc : accounts) {
			if (acc.getAccNumber() == n)
				return acc;
		}
		return null;
	}

	public void loadBank() {
		try {
			Scanner reader = new Scanner(new File("practical_16_solutions/merchantbank.txt"));
			while (reader.hasNext()) {
				int accNo = reader.nextInt();
				char c = reader.next().charAt(0);
				int amount = reader.nextInt();
				openAccount(accNo, c);
				deposit(accNo, amount);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Aww naw ... file not found");
		}
	}

	public void howMuchDosh() {
		try {
			FileWriter w = new FileWriter("practical_16_solutions/dosh.txt");
			BufferedWriter bw = new BufferedWriter(w);
			bw.write("Bank's total assets are: �" + totalAssets());
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.out.println("Exception writing to dosh.txt");
		}
	}

}
