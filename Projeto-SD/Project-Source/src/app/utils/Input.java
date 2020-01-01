package app.utils;

import java.util.Scanner;

/**
 * Implements method to read standard Java types, like strings, ints,...
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class Input {

    /**
     * Read a char from a given scanner.
     * @param keyboard_input keyboard scanner.
     * @return the char
     */
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

    /**
     * Reads a string from a scanner.
     * @param keyboard_input Scanner to read from system.in
     * @return the string
     */
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

    /**
     * Reads an year from the system.in as a VALID year (> 0, < 2021)
     * @param keyboard_input system.in scanner
     * @return the year
     */
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

    /**
     * Reads an integer from the system.in
     * @param keyboard_input system.in scanner
     * @return the int
     */
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
