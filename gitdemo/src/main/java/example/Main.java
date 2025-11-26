package example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Add meg a játékos neved: ");
        String playerName = scanner.nextLine();

        Game game = new Game(10, 10, playerName);

        System.out.print("Szeretnél betölteni egy mentett játékot? (igen/nem): ");
        String loadChoice = scanner.nextLine().trim();

        if (loadChoice.equalsIgnoreCase("igen")) {
            game.loadGame();
        } else {
            game.start();
        }


        boolean finished = false;

        String input;
        while (!finished) {
            System.out.print("Add meg a lépést (pl. a1), vagy írd be: exit -> kilépés: ");
            input = scanner.nextLine().trim();

            if (input.length() < 2) {
                System.out.println("Érvénytelen formátum!");
                continue;
            }

            int col = input.charAt(0) - 'a';
            int row;
            try {
                row = Integer.parseInt(input.substring(1)) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Érvénytelen szám!");
                continue;
            }

            finished = game.lepes(row, col);
        }


        scanner.close();
    }
}
