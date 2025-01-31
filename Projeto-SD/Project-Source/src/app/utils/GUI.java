
package app.utils;

/**
 * Methods to show GUI interfaces to client.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class GUI {

    /**
     * Shows the main menu.
     */
    public static void main_menu () {

        StringBuilder mainmenuSB = new StringBuilder();

        mainmenuSB.append("--------------------------------------------------\n");
        mainmenuSB.append("                     Main Menu                    \n");
        mainmenuSB.append("--------------------------------------------------\n");

        mainmenuSB.append("e: Exit the system | a: Authenticate | r: Register\n");

        mainmenuSB.append("--------------------------------------------------\n");

        System.out.println(mainmenuSB.toString());
    }

    /**
     * Shows user menu.
     */
    public static void menu_after_login (String username) {

        StringBuilder mainmenuSB = new StringBuilder();

        mainmenuSB.append("-----------------------------------------------------------\n");
        mainmenuSB.append("User: " + username + "\n");
        mainmenuSB.append("-----------------------------------------------------------\n");

        mainmenuSB.append("s: Search | u: Upload | d: Download | o: Logout | h: help  \n");

        mainmenuSB.append("-----------------------------------------------------------\n");

        System.out.println(mainmenuSB.toString());
    }

    public static void clear_screen() {

        System.out.println("\033[H\033[2J");
        System.out.flush();
    }
}
