package app.utils;

import java.util.Scanner;

public class Input {

    public static char read_char(Scanner keyboard_input) {

        String input = "not_set";

        while (input.length() > 1) {

            input = keyboard_input.nextLine();
        }

        return input.charAt(0);
    }


}
