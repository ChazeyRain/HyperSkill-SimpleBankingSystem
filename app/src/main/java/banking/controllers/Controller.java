package banking.controllers;

import banking.cardManagement.CardOperations;
import banking.cardManagement.Customer;

import java.util.Scanner;

public class Controller {
    private static final Scanner scanner = new Scanner(System.in);


    public static void start() {

        String input;

        while (true) {
            System.out.println("1. Create an account\n2. Log into account\n0. Exit");
            input = scanner.nextLine();

            switch (input) {
                case "0":
                    System.out.println("Bye!");
                    System.exit(0);
                    break;
                case "1":
                    createAccount();
                    break;
                case "2":
                    login();
                    break;
                default:
                    System.out.println("Incorrect command");
            }
        }
    }

    private static void createAccount() {
        long[] data = CardOperations.createCard();
        System.out.println("\nYour card has been created");
        System.out.printf("Your card number:\n%016d%n", data[0]);
        System.out.printf("Your card PIN:%n%04d%n%n", data[1]);
    }

    private static void login() {
        String cardNumberInput;
        String pinInput;

        System.out.println("Enter your card number:");
        cardNumberInput = scanner.nextLine();

        System.out.println("Enter your PIN:");
        pinInput = scanner.nextLine();

        Customer customer = CardOperations.authorize(cardNumberInput, pinInput);

        if (customer == null) {
            System.out.println("Wrong card number or PIN!\n");
            return;
        }

        CustomerController.customerMenu(customer);
    }
}
