import java.util.Scanner;

public class Utils {
    // Method to get integer input with retries
    public static int getIntInputWithRetries(Scanner scanner, String prompt, int retries) {
        int input = -1;
        while (retries > 0) {
            System.out.print(prompt);
            try {
                input = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                retries--;
            }
        }
        return input;
    }

    // Method to get double input with retries
    public static double getDoubleInputWithRetries(Scanner scanner, String prompt, int retries) {
        double input = -1;
        while (retries > 0) {
            System.out.print(prompt);
            try {
                input = Double.parseDouble(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                retries--;
            }
        }
        return input;
    }

    // Method to get string input with retries
    public static String getInputWithRetries(Scanner scanner, String prompt, int retries) {
        String input = "";
        while (retries > 0) {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (!input.trim().isEmpty()) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a non-empty value.");
                retries--;
            }
        }
        return input;
    }
}
