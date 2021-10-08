package banking.controllers;

import banking.cardManagement.CardDataBase;
import banking.cardManagement.CardOperations;
import banking.cardManagement.Customer;

import java.util.Scanner;

public class CustomerController {
    private static final Scanner scanner = new Scanner(System.in);
    private static Customer customer;

    static void customerMenu(Customer customer) {
        CustomerController.customer = customer;

        String input;

        System.out.println("\nYou have successfully logged in!");

        while (true) {
            System.out.println("\n1. Balance\n2. Add income\n3. Do transfer\n4. Close account\n5. Log out\n0. Exit");
            input = scanner.nextLine();

            switch (input) {
                case "0":
                    System.out.println("Bye!");
                    System.exit(0);
                case "1":
                    System.out.println("\nBalance: " + customer.getBalance());
                    break;
                case "2":
                    addIncome();
                    break;
                case "3":
                    doTransfer();
                    break;
                case "4":
                    closeAccount();
                    return;
                case "5":
                    System.out.println("\nYou have successfully logged out!\n");
                    return;
                default:
                    System.out.println("\nIncorrect command\n");
            }
        }
    }

    private static void addIncome() {
        System.out.println("Enter income:");
        String income = scanner.nextLine().trim();

        if (income.matches("[0-9]+")) {
            System.out.println(customer.addIncome(Long.parseLong(income)) ?
                    "Income was added!\n" :
                    "Income wasn't added!\n");
            return;
        }
        System.out.println("Wrong input!\n");
    }

    private static void doTransfer() {
        String number;
        String amount;

        System.out.println("Transfer");
        System.out.println("Enter card number:");

        number = scanner.nextLine().trim();
        if (!number.matches("[0-9]{16}")) {
            System.out.println("Probably you made a mistake in the card number. Please try again!\n");
            return;
        }

        int checkSum = Integer.parseInt(number.substring(15));
        if (!(checkSum == CardOperations.getLuhnSum(Long.parseLong(number)))) {
            System.out.println("Probably you made a mistake in the card number. Please try again!\n");
            return;
        }

        if (number.equals(customer.getNumber())) {
            System.out.println("You can't transfer money to the same account!");
            return;
        }

        if (!CardDataBase.getCurrentDataBase().isCardExist(number)) {
            System.out.println("Such a card does not exist.\n");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        amount = scanner.nextLine().trim();

        if (!amount.matches("[0-9]+")) {
            System.out.println("Wrong amount!");
            return;
        }

        if (customer.doTransfer(number, Long.parseLong(amount))) {
            System.out.println("Success!\n");
        } else {
            System.out.println("Not enough money!\n");
        }
    }

    private static void closeAccount() {
        customer.closeAccount();
        System.out.println("Account closed!\n");
    }

}
