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
}
