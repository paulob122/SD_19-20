package app.utils;

import java.util.Scanner;

public class Input {

    public static char read_char(Scanner keyboard_input) {

        String input = "";

        while (true) {

            input = keyboard_input.nextLine();

            if (input.length() == 1) {

                break;

            } else {

                System.out.print("Insert only one char: ");
            }
        }

        return input.charAt(0);
    }

    public static String read_string(Scanner keyboard_input) {

        String input = "";

        while (true) {

            input = keyboard_input.nextLine();

            if (input.length() > 0) {

                break;
            } else {

                System.out.print("Insert a valid string: ");
            }

        }

        return input;
    }

    public static int read_year(Scanner keyboard_input) {

        String input = "";
        int year = -1;

        while (true) {

            input = keyboard_input.nextLine();

            try {

                year = Integer.parseInt(input);

                if (year >= 0 && year <= 2020) break;
                else System.out.print("Insert a valid year: ");


            } catch (NumberFormatException e) {

                System.out.print("Insert a valid year: ");
            }
        }

        return year;
    }

    public static int read_integer(Scanner keyboard_input) {

        String input = "";

        int res = -1;

        while (true) {

            input = keyboard_input.nextLine();

            try {

                res = Integer.parseInt(input);

                break;

            } catch (NumberFormatException e) {

                System.out.print("Invalid ID, try to insert a number (>=0): ");
            }

            if (input.length() > 0) {

                break;

            } else {

                System.out.print("Insert a valid string: ");
            }

        }

        return res;
    }
}
