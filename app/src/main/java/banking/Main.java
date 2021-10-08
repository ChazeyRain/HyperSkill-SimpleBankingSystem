package banking;

import banking.cardManagement.CardDataBase;
import banking.controllers.Controller;

public class Main {
    public static void main(String[] args) {

        String fileName = "sample.db";

        for (int i = 0; i < args.length; i = i + 2) {
            if ("-fileName".equals(args[i])) {
                fileName = args[i + 1];
            }
        }

        CardDataBase.getAccessToDataBase(fileName);

        Controller.start();
    }
}