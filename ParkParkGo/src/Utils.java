import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
                System.out.println("Entrée invalide. Veuillez entrer un nombre valide.");
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
                System.out.println("Entrée invalide. Veuillez entrer un nombre valide.");
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
                System.out.println("Entrée invalide. Veuillez entrer une valeur non vide.");
                retries--;
            }
        }
        return input;
    }

    // Method to ensure CSV headers are present
    public static void ensureCsvHeaders(String fileName, String headers) {
        File file = new File(fileName);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(headers);
                writer.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
