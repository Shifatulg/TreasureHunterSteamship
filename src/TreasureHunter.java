import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;
    private static boolean easyMode = false;
    private static boolean samuraiMode = false;
    private  int timeDigGold =1;
    private static boolean lose = false;
    private static boolean win = false;
    private boolean easyGoldAdded = false;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    public static boolean isSamuraiMode() {
        return samuraiMode;
    }
    public static void lose() {
        lose = true;
    }
    public static void win() {
        win = true;
    }

    public static boolean isEasyMode() {
        return easyMode;
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        System.out.println("Welcome to "  + "TREASURE HUNTER" +  "!");
        System.out.println("Going hunting for the big treasure, eh?");
        System.out.print("What's your name, Hunter? ");
        String name = SCANNER.nextLine().toLowerCase();
        // Hunter object
        hunter = new Hunter(name, 10);
        System.out.print("Easy or hard mode?(n for normal): ");
        String hard = SCANNER.nextLine().toLowerCase();
        if (hard.equals("test")) {
            String[] items = {"water", "horse", "boat", "machete", "rope", "boots", "shovel"};
            hunter.testMode(items);
        }
        else if (hard.equals("hard") || hard.equals("h")) {
            hardMode = true;
        } else if (hard.equals("easy") || hard.equals("e")) {
            easyMode = true;
        } else if (hard.equals("s")) {
            samuraiMode = true;
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        } else if (easyMode) {
            markdown = 1;
            toughness = 0.2;
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
        if (easyMode && !easyGoldAdded) {
            hunter.changeGold(10);
            easyGoldAdded = true;
        }
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";

        while (!choice.equals("x") && !lose && !win) {
            System.out.println();
            System.out.println(currentTown.getLatestNews());
            System.out.println("***");
            System.out.println(hunter);
            System.out.println(currentTown);
            System.out.println("(B)uy something at the shop.");
            System.out.println("(S)ell something at the shop.");
            System.out.println("(M)ove on to a different town.");
            System.out.println("(L)ook for trouble!");
            System.out.println("(H)unt for treasure!");
            System.out.println("Give up the hunt and e(X)it.");
            System.out.println("(D)ig for gold!");
            System.out.println();
            System.out.print("What's your next move? ");
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
            if (lose) {
                System.out.println("You went into " + Colors.RED + "DEBT" + Colors.RESET);
                System.out.println(Colors.RED + "YOU LOSE" + Colors.RESET);
            } else if (win) {
                System.out.println("Congrats, you collected all the treasures");
                System.out.println("You " + Colors.YELLOW + "WIN!" + Colors.RESET);
            }
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                System.out.println(currentTown.getLatestNews());
                if (timeDigGold==0){
                    timeDigGold++;
                }
                enterTown();
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("h")) {
            currentTown.getTownTreasure();
        } else if (choice.equals("d")){
            if(timeDigGold==0){
                System.out.println("You already dug for gold in this town.");
            } else if(hunter.hasItemInKit("shovel")){
                double rnd = Math.random();
                timeDigGold--;
                if(rnd>.5){
                    int x = (int)(Math.random()*20)+1;
                    System.out.println("You dug up " + x +" gold!");
                    hunter.changeGold(x);
                }else{
                    System.out.println("You dug but only found dirt");
                }
            }else{
                System.out.println("You can't dig for gold without a shovel");
            }
        }else if (choice.equals("x")) {
            System.out.println("Fare thee well, " + hunter.getHunterName() + "!");
        } else {
            System.out.println("Yikes! That's an invalid option! Try again.");
        }
    }
}
