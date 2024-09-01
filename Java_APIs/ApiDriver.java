package cs1302.api;

import javafx.application.Application;

/**
 * Driver for the {@code ApiApp} class.
 */
public class ApiDriver {

    /**
     * Main entry-point into the application.
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        try {
            Application.launch(ApiApp.class, args);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            System.err.println();
            System.err.println(e);
            System.err.println("try again")
            System.exit(1);
        } catch (RuntimeException re) {
            re.printStackTrace();
            System.err.println();
            System.err.println(re);
            System.err.println("A runtime exception has occurred somewhere in the application,");
            System.err.println("and it propagated all the way up to the main method. Please");
            System.err.println("inspect the backtrace above for more information.");
            System.exit(1);
        } // try
    } // main

} // ApiDriver