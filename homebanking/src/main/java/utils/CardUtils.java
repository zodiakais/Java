package utils;

public class CardUtils {
    public static int cvvCard(){
        int cvv = (int) ((Math.random() * (999 - 100)) + 100);
        return cvv;
    }
    public static String cardNumber(){
        String cardNumber = (int) ((Math.random() * (9999 - 1000)) + 1000) + "-" + (int) ((Math.random() * (9999 - 1000)) + 1000) + "-" + (int) ((Math.random() * (9999 - 1000)) + 1000) + "-" + (int) ((Math.random() * (9999 - 1000)) + 1000);
        return cardNumber;
    }
    public static String accountNumber(){
        String accountNumber = "VIN" + (int) (Math.random() * 10000000 - 1) + 1;
        return accountNumber;
    }
}
