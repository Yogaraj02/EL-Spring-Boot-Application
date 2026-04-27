package util;

import java.util.InputMismatchException;
import java.util.Scanner;

import exceptions.InvalidInputException;

public class InputHandler {

    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = sc.nextInt();
                sc.nextLine(); // consume newline
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid integer.");
                sc.nextLine(); // clear the invalid input
            }
        }
    }

    public static String readString(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    public static String readDatePattern(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String date = sc.nextLine().trim();
            if (date.matches("\\d{2}-\\d{2}-\\d{4}")) {
                try {
                    String[] parts = date.split("-");
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);

                    if (day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 2000 && year <= 2100) {
                        return date;
                    } else {
                        throw new InvalidInputException("Date values are out of bounds.");
                    }
                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Invalid date format! Please enter in DD-MM-YYYY format.");
            }
        }
    }
}
