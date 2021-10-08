package banking.cardManagement;

import java.util.Random;

public class CardOperations {

    private static final String BIN = "400000";
    private static final int BankID = 400000;
    private static final CardDataBase cardDataBase = CardDataBase.getCurrentDataBase();

    public static long[] createCard() {
        Random random = new Random();

        long cardNumber;
        int pin;

        do {
            cardNumber = random.nextInt(100_0000_000);
            //cardNumber = 485684569;
            cardNumber =  (long) (BankID * 1E10  + cardNumber * 10);
            cardNumber += getLuhnSum(cardNumber);
            pin = random.nextInt(10000);
        } while (!cardDataBase.createNewCard(cardNumber + "", String.format("%04d", pin)));

        return new long[]{cardNumber, pin};
    }


    public static int getLuhnSum(long number) {
        int sum = 0;

        for (int i = 0; i < 15; i++) {
            number = number / 10;
            if (i % 2 == 0) {
                sum += (number % 10) * 2 > 9 ? (number % 10) * 2 - 9 : (number % 10) * 2;
            } else {
                sum += number % 10;
            }
        }

        return sum % 10 == 0 ? 0 : 10 - sum % 10;
    }


    public static Customer authorize(String cardNumberInput, String pinInput) {
        if (cardNumberInput.matches(BIN + "[0-9]{10}") && pinInput.matches("[0-9]{4}")) {
            long cardNumber = Long.parseLong(cardNumberInput);

            if (cardNumber % 10 == getLuhnSum(cardNumber)) {
                return cardDataBase.getCustomerData(cardNumberInput, pinInput);
            }
        }
        return null;
    }
}
