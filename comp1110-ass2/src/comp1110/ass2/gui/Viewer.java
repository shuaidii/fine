package comp1110.ass2.gui;

import GAME.Islands;
import GAME.Player;
import comp1110.ass2.BlueLagoon;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class Viewer extends Application {


    private static final double VIEWER_WIDTH = 1200;
    private static final double VIEWER_HEIGHT = 700;

    private final Group controls = new Group();
    private TextField stateTextField;

    static int rows = 13; // board Height in rows
    static int numberPlayers = 2; // number of players
    static int currentPlayerID = 0; // ID of current player
    static String currentPhase = "Exploration";
    static Islands[] islandArray = new Islands[0];
    static Player[] playerArray = new Player[0];
    static int[][] stoneCoords = {};
    static int[][] coconutCoords = {};
    static int[][] bambooCoords = {};
    static int[][] waterCoords = {};
    static int[][] preciousStoneCoords = {};
    static int[][] statuetteCoords = {};
    static boolean isIsland = false;
    static boolean isStone = false;
    static boolean isResource = false;
    static boolean isSettler = false;
    static boolean isVillage = false;
    static Color playerColour = Color.YELLOW;
    double startX = 60.0, startY = 60.0; // offsets for displaying tile grid on screen

    public double r = 30.0; // define radius of hexagon tile (from centre to corner)
    public double h = Math.sqrt(3) * r; // height of hexagon tile (from edge to edge)



    /**
     * Given a state string, draw a representation of the state
     * on the screen.
     * <p>
     * This may prove useful for debugging complex states.
     *
     * @param stateString a string representing a game state
     */

    // note potential issue where first number is two digits. for checking later
    // pull out variables into Viewer class
    static void displayState(String stateString) { // check string well-formed method will ensure that stateString is good before passed into function
        //gameArrangement ('a')
        int gameArrangementIndex = stateString.indexOf('a');
        int a1 = stateString.indexOf(" ", gameArrangementIndex + 2);
        int a2 = stateString.indexOf(";", gameArrangementIndex);
        rows = Integer.parseInt(stateString.substring(gameArrangementIndex + 2, a1));
        numberPlayers = Integer.parseInt(stateString.substring(a1 + 1, a2));
        System.out.println(rows);
        System.out.println(numberPlayers);

        // currentState ('c')
        int currentStateIndex = stateString.indexOf('c');
        int c1 = stateString.indexOf(" ", currentStateIndex + 2);
        int c2 = stateString.indexOf(";", currentStateIndex);
        currentPlayerID = Integer.parseInt(stateString.substring(currentStateIndex + 2, c1));
        switch (stateString.substring(c1 + 1, c2)) {
            case "E":
                currentPhase = "Exploration";
            case "S":
                currentPhase = "Settlement";
        }
        System.out.println(currentPlayerID);
        System.out.println(currentPhase);

        //Islands ('i')
        int countIslandsStateString = countCharStateString(stateString, 'i');
        islandArray = new Islands[countIslandsStateString];
        int ind = 0;
        for (int i = 0; i < countIslandsStateString; i++) {
            int islandIndex = i == 0 ? (stateString.indexOf('i')) : (stateString.indexOf('i', ind));
            int i1 = stateString.indexOf(" ", islandIndex + 2);
            int i2 = stateString.indexOf(";", islandIndex);
            int pointValue = Integer.parseInt(stateString.substring(islandIndex + 2, i1));
            int[][] islandCoords = StringtoIntArray(stateString.substring(i1 + 1, i2));
            ind = islandIndex + 1;

            Islands island = new Islands(pointValue, islandCoords);
            islandArray[i] = island;
        }

        //Stone Circles ('s')
        int stoneIndex = stateString.indexOf('s');
        int s1 = stateString.indexOf(";", stoneIndex);
        stoneCoords = StringtoIntArray(stateString.substring(stoneIndex + 2, s1));

        //Unclaimed Resources and Statuettes ('r')
        int resourceIndex = stateString.indexOf('r');
        int r1 = stateString.indexOf(";", resourceIndex);
        String resourceString = stateString.substring(resourceIndex + 2, r1);

        int coconutIndex = resourceString.indexOf('C');
        int bambooIndex = resourceString.indexOf('B');
        int waterIndex = resourceString.indexOf('W');
        int preciousStoneIndex = resourceString.indexOf('P');
        int statuetteIndex = resourceString.indexOf('S');

        String coconutString = bambooIndex - (coconutIndex + 2) > 0 ? (resourceString.substring(coconutIndex + 2, bambooIndex)) : ("");
        String bambooString = waterIndex - (bambooIndex + 2) > 0 ? (resourceString.substring(bambooIndex + 2, waterIndex)) : ("");
        String waterString = preciousStoneIndex - (waterIndex + 2) > 0 ? (resourceString.substring(waterIndex + 2, preciousStoneIndex)) : ("");
        String preciousStoneString = statuetteIndex - (preciousStoneIndex + 2) > 0 ? (resourceString.substring(preciousStoneIndex + 2, statuetteIndex)) : ("");
        String statuetteString = (resourceString.length() - 1) - (statuetteIndex + 2) > 0 ? (resourceString.substring(statuetteIndex + 2)) : ("");

        coconutCoords = !coconutString.equals("") ? (StringtoIntArray(coconutString)) : new int[0][0];
        bambooCoords = !bambooString.equals("") ? (StringtoIntArray(bambooString)) : new int[0][0];
        waterCoords = !waterString.equals("") ? (StringtoIntArray(waterString)) : new int[0][0];
        preciousStoneCoords = !preciousStoneString.equals("") ? (StringtoIntArray(preciousStoneString)) : new int[0][0];
        statuetteCoords = !statuetteString.equals("") ? (StringtoIntArray(statuetteString)) : new int[0][0];

        //Players ('p')
        int countPlayersStateString = countCharStateString(stateString, 'p');
        playerArray = new Player[countPlayersStateString];
        ind = 0;

        for (int i = 0; i < countPlayersStateString; i++) {
            int playerIndex = i == 0 ? (stateString.indexOf('p')) : (stateString.indexOf('p', ind));
            int p1 = stateString.indexOf(";", playerIndex);
            String playerString = stateString.substring(playerIndex + 2, p1);

            int playerID = Integer.parseInt(playerString.substring(0, 1));
            int playerScore = Integer.parseInt(playerString.substring(2, 3));
            int playerCoconut = Integer.parseInt(playerString.substring(4, 5));
            int playerBamboo = Integer.parseInt(playerString.substring(6, 7));
            int playerWater = Integer.parseInt(playerString.substring(8, 9));
            int playerPreciousStone = Integer.parseInt(playerString.substring(10, 11));
            int playerStatuette = Integer.parseInt(playerString.substring(12, 13));

            int settlerIndex = playerString.indexOf('S');
            int villageIndex = playerString.indexOf('T');
            String settlerString = (villageIndex - 1) - (settlerIndex + 2) > 0 ? (playerString.substring(settlerIndex + 2, villageIndex - 1)) : ("");
            String villageString = (playerString.length() - 1) - (villageIndex + 2) > 0 ? (playerString.substring(villageIndex + 2)) : ("");
            int[][] settlerCoords = !settlerString.equals("") ? (StringtoIntArray(settlerString)) : new int[0][0];
            int[][] villageCoords = !villageString.equals("") ? (StringtoIntArray(villageString)) : new int[0][0];

            Player player = new Player(playerID, playerScore, playerCoconut, playerBamboo, playerWater, playerPreciousStone, playerStatuette, settlerCoords, villageCoords);
            playerArray[i] = player;

            ind = playerIndex + 1;
        }
    }


        // FIXME Task 5

    //https://www.geeksforgeeks.org/java-program-to-convert-string-to-integer-array/
    public static int[][] StringtoIntArray(String string) {
        String [] str1 = string.split(" ");
        String str2 = Arrays.toString(str1);
        String str3 = str2.replaceAll(" ", "");
        String [] str4 = str3.split(",");

        int[][] arr = new int [str4.length / 2][2];
        int[] a = new int[2];
        for (int i = 0; i < str4.length; i++) {
            if (i % 2 == 0) {
                a = new int[2];
                String removeChar = str4[i].replaceAll("\\[", "");
                a[0] = Integer.parseInt(removeChar);
            } else {
                String removeChar = str4[i].replaceAll("]", "");
                a[1] = Integer.parseInt(removeChar);
                int ind = i == 1 ? (0) : ((i - 1) / 2);
                arr[(ind)] = a;
            }
        }
        return arr;
    }

    public static int countCharStateString (String stateString, char letter) {
        int count = 0;
        for (int i = 0; i < stateString.length(); i++) {
            if(stateString.charAt(i) == letter) {count++;}
        }
        return count;
    }

    /**
     * Create a basic text field for input and a refresh button.
     */
    private void makeControls() {
        Label playerLabel = new Label("Game State:");
        stateTextField = new TextField();
        stateTextField.setPrefWidth(200);
        Button button = new Button("Refresh");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                displayState(stateTextField.getText());
            }
        });
        HBox hb = new HBox();
        hb.getChildren().addAll(playerLabel, stateTextField, button);
        hb.setSpacing(10);
        hb.setLayoutX(50);
        hb.setLayoutY(VIEWER_HEIGHT - 50);
        controls.getChildren().add(hb);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        displayState(BlueLagoon.DEFAULT_GAME);
        AnchorPane root = new AnchorPane();
        primaryStage.setTitle("Blue Lagoon Viewer");
        Scene scene = new Scene(root, VIEWER_WIDTH, VIEWER_HEIGHT);
        primaryStage.setScene(scene);

        for (int i = 0; i < rows; i++) {
            int columns = i % 2 == 0 ? 12 : 13;
            double offset = i % 2 == 0 ? (offset = startX) : (offset = startX - h / 2);
            for (int j = 0; j < columns; j++) {
                double x = j * h + offset;
                double y = i * 1.5 * r + startY;
                isIsland = false;
                isSettler = false;
                isVillage = false;

                for (Islands islands : islandArray) {
                    for (int k = 0; k < islands.islandCoords.length; k++) {
                        isIsland = i == islands.islandCoords[k][0] && j == islands.islandCoords[k][1];
                        if (isIsland) {break;}
                    }
                    if (isIsland) {break;}
                }

                for (Player player : playerArray) {
                    for (int k = 0; k < player.settlerCoords.length; k++) {
                        isSettler = i == player.settlerCoords[k][0] && j == player.settlerCoords[k][1];
                        if (player.playerID == 0) {
                            playerColour = Color.YELLOW;
                        }
                        if (player.playerID == 1) {
                            playerColour = Color.RED;
                        }
                        if (isSettler) {break;}
                    }
                    if (isSettler) {break;}
                }

                for (Player player : playerArray) {
                    for (int k = 0; k < player.villageCoords.length; k++) {
                        isVillage = i == player.villageCoords[k][0] && j == player.villageCoords[k][1];
                        if (isVillage) {break;}
                    }
                    if (isVillage) {break;}
                }

                for (int[]coords : stoneCoords) {
                        isStone = i == coords[0] && j == coords[1];
                        if (isStone) {
                            break;
                        }
                    }

                String resource = "";

                if (!isResource) {
                    for (int[]coords : coconutCoords) {
                        isResource = i == coords[0] && j == coords[1];
                        if (isResource) {
                            resource = "C";
                            break;
                        }
                    }
                }
                if (!isResource) {
                    for (int[] coords : bambooCoords) {
                        isResource = i == coords[0] && j == coords[1];
                        if (isResource) {
                            resource = "B";
                            break;
                        }
                    }
                }

                if (!isResource) {
                    for (int[] coords : waterCoords) {
                        isResource = i == coords[0] && j == coords[1];
                        if (isResource) {
                            resource = "W";
                            break;
                        }
                    }
                }

                if (!isResource) {
                    for (int[] coords : preciousStoneCoords) {
                        isResource = i == coords[0] && j == coords[1];
                        if (isResource) {
                            resource = "P";
                            break;
                        }
                    }
                }

                if (!isResource) {
                    for (int[] coords : statuetteCoords) {
                        isResource = i == coords[0] && j == coords[1];
                        if (isResource) {
                            resource = "S";
                            break;
                        }
                    }
                }

                Polygon hexTile = new Viewer.hexTile(x, y, isIsland);
                root.getChildren().add(hexTile);

                if (isSettler) {
                    Circle circle = new Circle(x + h / 2, y - r / 2, 15);
                    circle.setFill(playerColour);
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);

                    Text settlerText = new Text(x + h / 2,y - r / 2,"S");
                    settlerText.setBoundsType(TextBoundsType.VISUAL);
                    settlerText.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
                    double width = settlerText.prefWidth(-1);
                    settlerText.setX((x + h / 2) - width / 2);
                    settlerText.setTextOrigin(VPos.CENTER);

                    root.getChildren().add(circle);
                    root.getChildren().add(settlerText);
                }

                if (isVillage) {
                    Circle circle = new Circle(x + h / 2, y - r / 2, 15);
                    circle.setFill(playerColour);
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);

                    Text villageText = new Text(x + h / 2,y - r / 2,"V");
                    villageText.setBoundsType(TextBoundsType.VISUAL);
                    villageText.setFont(Font.font("Veranda", FontWeight.BOLD, 15));
                    double width = villageText.prefWidth(-1);
                    villageText.setX((x + h / 2) - width / 2);
                    villageText.setTextOrigin(VPos.CENTER);

                    root.getChildren().add(circle);
                    root.getChildren().add(villageText);
                }

                if (isStone) {
                    Circle circle = new Circle(x + h / 2, y - r / 2, 20);
                    circle.setFill(Color.TRANSPARENT);
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);
                    root.getChildren().add(circle);
                }

                if (isResource) {
                    Text resourceText = new Text(x + h / 2, y - r / 2, resource);
                    resourceText.setBoundsType(TextBoundsType.VISUAL);
                    resourceText.setFont(Font.font("Times New Roman", 15));
                    double width = resourceText.prefWidth(-1);
                    resourceText.setX((x + h / 2) - width / 2);
                    resourceText.setTextOrigin(VPos.CENTER);
                    root.getChildren().add(resourceText);
                    isResource = false;
                }
            }
        }

        VBox gameInfo = new VBox(8);
        Label label = new Label("Game Information");
        Label players = new Label("Number of Players: " + numberPlayers);
        Label phase = new Label("Current Phase: " + currentPhase);
        Label currentPlayer = new Label("Current Player ID: " + currentPlayerID);
        gameInfo.getChildren().addAll(label, players, phase, currentPlayer);
        gameInfo.setLayoutX(VIEWER_WIDTH * (2.0 / 3.0));
        gameInfo.setLayoutY(50);

        root.getChildren().add(gameInfo);
        root.getChildren().add(controls);

        makeControls();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private class hexTile extends Polygon {

        hexTile(double x, double y, boolean isIsland) {
            getPoints().addAll(
                    x, y,
                    x + h / 2, y + r / 2,
                    x + h, y,
                    x + h, y - r,
                    x + h / 2, y - r - r / 2,
                    x, y - r);
            if (isIsland) {setFill(Color.GREEN);}
            else {setFill(Color.BLUE);}
            setStrokeWidth(1);
            setStroke(Color.BLACK);
        }
    }

}

//https://stackoverflow.com/questions/54165602/create-hexagonal-field-with-javafx
//https://www.redblobgames.com/grids/hexagons/