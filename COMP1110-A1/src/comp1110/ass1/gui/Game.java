package comp1110.ass1.gui;


import comp1110.ass1.AppleTwist;
import comp1110.ass1.Position;
import comp1110.ass1.caterpillar.Caterpillar;
import comp1110.ass1.caterpillar.Direction;
import comp1110.ass1.caterpillar.Colour;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Game extends Application {

    /*
    The root group is the data structure that holds all the visual assets in
    the JavaFX window. These assets are known as Nodes. You might notice
    that in the `start()` method, we create a Scene and pass this `root` through
    as a parameter. This is because we want everything inside this Group to be
    displayed in the Scene.
     */
    private final Group root = new Group();

    // The width and height of the window
    private static final int WINDOW_WIDTH = 1600;
    private static final int WINDOW_HEIGHT = 900;

    // The distance between two neighbouring spots on the apple
    private static final double SPOT_DISTANCE_Y = 75;
    private static final double SPOT_DISTANCE_X = 80;

    // The height of the portion of the apple above all the spots
    private static final double APPLE_TOP_HEIGHT = 120;

    // The margins used for all visual assets
    private static final int MARGIN_X = 100;
    private static final int MARGIN_Y = 50;

    // The start of the apple in the y-direction (ie: y = 0)
    private static final double START_Y = APPLE_TOP_HEIGHT + MARGIN_Y;

    // The start of the apple in the x-direction (ie: x = 0)
    private static final double START_X = 110.0;

    // The centre of the apple, this is needed to create the board
    private static final double APPLE_CENTRE = START_X + 2.5 * SPOT_DISTANCE_X - 10;

    /*
     Distance to leave from a button to the right - used for setting up
     all the buttons at the bottom of the window.
     */
    private static final double BUTTON_BUFFER = 100.0;

    /*
     Distance to leave from a slider to the right - the slider is a bit
     longer than the buttons, hence the need for differing lengths.
     */
    private static final double SLIDER_BUFFER = 200.0;

    /*
     The base of the filepath for all the assets in the game. This points to
     the "assets" directory within the directory containing this class.
     */
    private static final String URI_BASE = "assets/";

    // Group containing all the assets corresponding to pieces of the apple.
    private final Group apple = new Group();

    // Group containing all the buttons, sliders and text used in this application.
    private final Group controls = new Group();

    /*
     Group containing all the requirements of the game (the transparent
     caterpillar heads on the apple).
     */
    private final Group requirements = new Group();

    // Group containing the caterpillar assets used in this game
    private final Group caterpillars = new Group();

    /*
     An array storing the three caterpillar pieces used in this game, useful
     for rotating all the caterpillars from an event handler inside one of the
     caterpillars.
     */
    private final CaterpillarPiece[] caterpillarPieces = new CaterpillarPiece[3];

    // The direction all caterpillars off the board should face (to economise on space)
    private Direction caterpillarDirection = Direction.UP;

    // A slider for how difficult the challenge should be (from 1 to 4)
    private final Slider difficulty = new Slider(1, 4, 1);

    // Instruction text for the game
    private final static String INSTRUCTIONS = """
            Try to fit all three caterpillars on the apple!
            You can drag and rotate each caterpillar.
            You can also rotate individual joints, marked with a cross on them.
            The transparent caterpillar heads correspond to where caterpillar heads must sit.
            Gray caterpillar heads represent wild cards, that is, any caterpillar head can be placed there.
            Good luck!
            """;

    // Text describing controls in the game
    private final static String CONTROLS = """
            Drag and drop a caterpillar onto the apple to place it there.
            Right click a caterpillar's head to rotate the entire caterpillar.
            Right click a caterpillar's pivot, marked with a cross, to rotate just that pivot.
            """;

    // The instance of the game being played
    AppleTwist appleTwist;

    // Text describing which challenge is being played
    private final Text challengeNumber = new Text("Challenge number: ");

    /*
    A class defining a caterpillar piece in the front end. It extends the
    "Group" class, meaning that it can contain several Nodes. In this case,
    each CaterpillarPiece contains several SegmentPieces, corresponding to the
    caterpillars having several segments in the backend.

    This class also defines functionality for dragging and rotating all the
    segments together.
     */
    class CaterpillarPiece extends Group {
        Caterpillar caterpillar; // the corresponding caterpillar in the backend
        SegmentPiece[] segments; // the segment pieces making up this caterpillar in the frontend

        double mouseX, mouseY; // these coordinates are useful for drag-and-drop

        /*
         Where the caterpillar lives off the board (depending on the direction
         each off-board caterpillar should be facing: see `caterpillarDirection`).
         */
        double homeX;
        double homeY;

        /**
         * Constructor for a CaterpillarPiece. It only needs the information
         * from its corresponding caterpillar in the backend to determine its
         * location off the board.
         *
         * This is also where all the drag-and-drop and rotation behaviour is
         * defined, using event handlers.
         *
         * @param caterpillar the corresponding caterpillar in the backend
         */
        public CaterpillarPiece(Caterpillar caterpillar) {
            // Off-board locations for each colour
            this.homeX = WINDOW_WIDTH - MARGIN_X * 5 - caterpillar.getColour().ordinal() * SPOT_DISTANCE_X * 2;
            this.homeY = MARGIN_Y * 8;

            // Link the front-end caterpillar to the back-end caterpillar
            this.caterpillar = caterpillar;
            this.segments = new SegmentPiece[caterpillar.getLength()];
            for (int i = 0; i < this.segments.length; i++) {
                this.segments[i] = new SegmentPiece(
                        caterpillar.getColour(),
                        i == 0,
                        caterpillar.isPivot(i));
                if (i == 0) { // Caterpillar head

                    // Event handler for if user clicks on this specific segment
                    this.segments[i].setOnMouseClicked(event -> {

                        // MouseButton.SECONDARY = right-click
                        if (event.getButton() == MouseButton.SECONDARY && !this.caterpillar.isPlaced()) {
                            this.caterpillar.rotate();
                            this.syncSegments();
                        }
                    });
                } else if (caterpillar.isPivot(i)) {

                    /*
                     Lambda expressions (ie. the `event -> {...}` code) need
                     unchanging variables, hence why we assign `segmentIndex`
                     the value of `i` and use that instead of just using `i`.
                     */
                    int segmentIndex = i;

                    // Event handler for if user clicks on this specific segment
                    this.segments[i].setOnMouseClicked(event -> {

                        // MouseButton.SECONDARY = right-click
                        if (event.getButton() == MouseButton.SECONDARY && !this.caterpillar.isPlaced()) {

                            // Rotate the pivot and update the front-end caterpillar
                            this.caterpillar.nextPivotAngle(segmentIndex);
                            this.syncSegments();
                        }
                    });
                }

                // Add this individual segment piece to the caterpillar
                this.getChildren().add(this.segments[i]);
            }

            /*
             Event handler for if the user presses the mouse on this
             specific caterpillar.
             */
            this.setOnMousePressed(event -> {

                // Set these values to prepare for drag-and-drop
                this.mouseX = event.getSceneX();
                this.mouseY = event.getSceneY();
            });

            /*
             Event handler for if the user drags the mouse over this
             specific caterpillar.
             */
            this.setOnMouseDragged(event -> {

                /*
                 Move the caterpillar by the difference in mouse position
                 since the last drag.
                 */
                double diffX = event.getSceneX() - mouseX;
                double diffY = event.getSceneY() - mouseY;
                this.setLayoutX(this.getLayoutX() + diffX);
                this.setLayoutY(this.getLayoutY() + diffY);

                /*
                 Update `mouseX` and `mouseY` and repeat the process.
                 */
                this.mouseX = event.getSceneX();
                this.mouseY = event.getSceneY();
            });

            /*
             Event handler for if the user releases the mouse after
             dragging this specific caterpillar.
             */
            this.setOnMouseReleased(event -> {
                Position p = this.getSnapPosition();
                boolean onX = p.getX() >= 0 && p.getX() <= 4;
                boolean onY = p.getY() >= 0 && p.getY() <= 4;

                /*
                 If placed, have to remove before can be moved to now spot.
                 In some cases might be able to drag caterpillar between two valid
                 spots on Apple. This allows that.
                 */
                if (this.caterpillar.isPlaced()) {
                    appleTwist.getBoard().removeCaterpillar(this.caterpillar);
                }
                if (onX && onY) {
                    appleTwist.getBoard().tryPlaceCaterpillar(this.caterpillar, p);
                }
                Position[] positions = this.caterpillar.getPositions();
                this.setLocation(positions[0]);

                if (appleTwist.isPuzzleSolved()) {
                    Alert solved = new Alert(Alert.AlertType.INFORMATION);
                    solved.setTitle("Congratulations!");
                    solved.setHeaderText("You solved the puzzle!");
                    solved.setContentText("The caterpillars can now all eat together.");
                    solved.show();
                }
            });

            // Place the caterpillar in its starting location
            this.snapToHome();
        }

        /**
         * A method which updates all the front-end caterpillar segments. This
         * method is called after the corresponding back-end caterpillar has
         * been moved or rotated in some way.
         */
        public void syncSegments() {
            var positions = this.caterpillar.getPositions(new Position(0, 0));
            var directions = this.caterpillar.getDirections();
            for (int i = 0; i < this.segments.length; i++) {

                /*
                 Rotate and move graphical segments so that they reflect their
                 counterparts in the back-end.
                 */
                this.segments[i].setRotation(directions[i].angle);
                this.segments[i].setLocation(positions[i]);
            }
        }

        /**
         * Place the caterpillar at a certain location on the board.
         *
         * @param position the location on the board
         */
        public void setLocation(Position position) {

            // Position is not on the board
            if (position.getX() == -1 || position.getY() == -1) {
                this.snapToHome();
            } else {
                this.setLayoutX(START_X + position.getX() * SPOT_DISTANCE_X);
                this.setLayoutY(START_Y + position.getY() * SPOT_DISTANCE_Y);
            }
        }

        /**
         * @return the closest position on the board to where this caterpillar
         *         is currently positioned
         */
        public Position getSnapPosition() {
            int x = (int) Math.round((this.getLayoutX() - START_X) / SPOT_DISTANCE_X);
            int y = (int) Math.round((this.getLayoutY() - START_Y) / SPOT_DISTANCE_Y);
            return new Position(x, y);
        }

        /**
         * Snaps this caterpillar to its home position and rotation off the board.
         */
        public void snapToHome() {
            /*
             Place the caterpillar at the location corresponding to the home position.
             */
            this.setLayoutX(this.homeX);
            this.setLayoutY(this.homeY);
            this.syncSegments();
        }
    }

    /*
     Each caterpillar piece is composed of segment pieces. These pieces extend
     the `ImageView` class, meaning they display one of the images in the
     "assets" directory.
     */
    class SegmentPiece extends ImageView {
        boolean isPivot; // if this segment piece corresponds to a pivot joint
        boolean isHead; // if this segment piece corresponds to a head

        /**
         * Constructor for a SegmentPiece. Given a colour and information about
         * the type of segment, it takes on the corresponding asset.
         *
         * @param colour  the colour of the segment
         * @param isHead  whether the segment corresponds to a head
         * @param isPivot whether the segment corresponds to a pivot joint
         */
        public SegmentPiece(Colour colour,
                            boolean isHead,
                            boolean isPivot) {
            this.isPivot = isPivot;
            this.isHead = isHead;
            String cName = colour.name().toLowerCase(); // the asset names are lower-case
            String path = URI_BASE + cName + "/";
            if (isHead)       path += "head";
            else if (isPivot) path += "pivot";
            else              path += "fixed";
            path += ".png";

            /*
             NB: if you want to use assets in your own GUI, this is useful code
             to remember!
             */
            Image image = new Image(Game.class.getResource(path).toString());
            this.setImage(image);
        }

        /**
         * Sets the location of this individual segment on the apple.
         *
         * @param p the position where this segment should go on the apple
         */
        public void setLocation(Position p) {
            this.setLayoutX(p.getX() * SPOT_DISTANCE_X);
            this.setLayoutY(p.getY() * SPOT_DISTANCE_Y);
        }

        /**
         * Sets the rotation of this individual segment.
         *
         * @param angle the angle at which it should be rotated
         */
        public void setRotation(double angle) {
            this.setRotate(angle + 90.0); // correct for image orientation
        }
    }

    /**
     * A method that interfaces with the backend of this project to create a
     * new conceptual "game".
     */
    private void newGame() {

        /* Generate a new instance of the game Apple Twist using the
           `AppleTwist` constructor. The difficulty is obtained from the Slider
           provided when playing the game.
        */
        this.appleTwist = new AppleTwist((int) difficulty.getValue() - 1);
        this.challengeNumber.setText("Challenge number: " + this.appleTwist.challenge.getChallengeNumber());

        /* Calls to other methods that help set up the game. If you wish
           to inspect the source code of these methods, you can right-click
           on the method's name. Then, in the pop-up menu, click "Go To" ->
           "Declaration or Usages". (You can also left-click on the method
           name and then press "Ctrl" + "B" or "Cmd" + "B" for Windows/Mac
           respectively)
         */
        this.reset();
        this.makeBoard();
        this.makePieces();
    }

    /**
     * Creates the board for the application.
     */
    private void makeBoard() {

        /* We need the initial state of the board to find out what sides of the
           apple are being used. */
        String appleLayout = this.appleTwist.challenge.getLayout();
        double currentY = MARGIN_Y;

        /* This loop will iterate over the first five characters in the state
           string - the ones corresponding to the sides of the apple. */
        for (int i = 0; i < 5; i++) {
            char appleSide = appleLayout.charAt(i);
            String path = URI_BASE + "board/" + appleSide + ".png";

            /* These two lines of code load images from a provided path - very
               useful if you're making your own GUI! */
            Image appleImage = new Image(Game.class.getResource(path).toString());
            ImageView apple = new ImageView(appleImage);

            /* These two lines set the location of the image in Cartesian
               coordinates - except y moves from top to bottom, not bottom to
               top. */
            apple.setLayoutX(APPLE_CENTRE - (appleImage.getWidth()) / 2.0);
            apple.setLayoutY(currentY);

            /* Add the apple segment to our board Group, and by extension, our
               root Group. */
            this.apple.getChildren().add(apple);

            // Increment y and x (indirectly) so that the apple segments sit neatly
            currentY += appleImage.getHeight();
        }

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                Position p = new Position(x, y);
                Colour requirement = this.appleTwist.getBoard().spotRequiredHeadColour(p);
                if (requirement != null) { // if there is a requirement at this spot
                    // Create a separate segment piece corresponding to the requirement
                    SegmentPiece requirementPiece = new SegmentPiece(requirement, true, false);
                    requirementPiece.setOpacity(0.5);
                    requirementPiece.setLayoutX(START_X + p.getX() * SPOT_DISTANCE_X);
                    requirementPiece.setLayoutY(START_Y + p.getY() * SPOT_DISTANCE_Y);
                    this.requirements.getChildren().add(requirementPiece);
                }
            }
        }
    }

    /**
     * A method that creates the caterpillar pieces for the game.
     */
    private void makePieces() {
        for (int i = 0; i < 3; i++) {
            Caterpillar caterpillar = this.appleTwist.getCaterpillars()[i];
            CaterpillarPiece cp = new CaterpillarPiece(caterpillar);
            this.caterpillarPieces[i] = cp;
            this.caterpillars.getChildren().add(cp);
        }
    }

    /**
     * Method that creates the buttons, sliders and text for the game.
     */
    public void makeControls() {
        // Settings for a Button instance
        Button restart = new Button();
        restart.setLayoutX(MARGIN_X);
        restart.setLayoutY(WINDOW_HEIGHT - MARGIN_Y);
        restart.setOnAction(event -> this.restart()); // Lambda expression
        restart.setText("Restart");
        this.controls.getChildren().add(restart);

        Button newGame = new Button();
        newGame.setLayoutX(restart.getLayoutX() + BUTTON_BUFFER);
        newGame.setLayoutY(WINDOW_HEIGHT - MARGIN_Y);
        newGame.setOnAction(event -> this.newGame());
        newGame.setText("New Game");
        this.controls.getChildren().add(newGame);

        Button help = new Button();
        help.setLayoutX(newGame.getLayoutX() + BUTTON_BUFFER);
        help.setLayoutY(WINDOW_HEIGHT - MARGIN_Y);
        help.setOnAction(event -> {
            // Creates an alert, providing it with a type and with body text
            Alert alert = new Alert(Alert.AlertType.INFORMATION, INSTRUCTIONS);
            alert.setTitle("Instructions");
            alert.setHeaderText("Instructions");
            alert.show();
        });
        help.setText("Instructions");
        this.controls.getChildren().add(help);

        Button controlsButton = new Button();
        controlsButton.setLayoutX(help.getLayoutX() + BUTTON_BUFFER);
        controlsButton.setLayoutY(WINDOW_HEIGHT - MARGIN_Y);
        controlsButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, CONTROLS);
            alert.setTitle("Controls");
            alert.setHeaderText("Controls");
            alert.show();
        });
        controlsButton.setText("Controls");
        this.controls.getChildren().add(controlsButton);

        // Settings for a Slider instance
        this.difficulty.setShowTickLabels(true);
        this.difficulty.setShowTickMarks(true);
        this.difficulty.setMajorTickUnit(1);
        this.difficulty.setMinorTickCount(0);
        this.difficulty.setSnapToTicks(true);
        this.difficulty.setLayoutX(controlsButton.getLayoutX() + BUTTON_BUFFER);
        this.difficulty.setLayoutY(WINDOW_HEIGHT - MARGIN_Y);
        this.controls.getChildren().add(this.difficulty);

        // Settings for a Text instance
        this.challengeNumber.setFont(javafx.scene.text.Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 14));
        this.challengeNumber.setLayoutX(this.difficulty.getLayoutX() + SLIDER_BUFFER);
        this.challengeNumber.setLayoutY(WINDOW_HEIGHT - 30.0);
        this.controls.getChildren().add(challengeNumber);
    }

    /**
     * Removes the existing caterpillar pieces, apple segments and requirements
     * associated with the current game.
     */
    public void reset() {
        this.caterpillars.getChildren().clear();
        this.apple.getChildren().clear();
        this.requirements.getChildren().clear();
    }

    /**
     * Restart the game, using the same challenge.
     */
    public void restart() {
        this.appleTwist = new AppleTwist(this.appleTwist.challenge);
        this.reset();
        this.makeBoard();
        this.makePieces();
    }

    /**
     * Method that is necessary to run any JavaFX class. This is equivalent
     * to `public static void main(String[] args)` in Java classes.
     *
     * @param stage the class which holds all data related to the application
     * @throws Exception if there is an error in the code - hopefully this doesn't happen!
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Apple Twist");

        /*
          A "Scene" is the window corresponding to the JavaFX application.
          Notice that the last two parameters of a Scene are the width and
          height of the window. The other (first) parameter is a Group,
          which holds all the assets (images, buttons, et cetera) in the application.
          Chat to your lecturer/tutor about any of this if it's unclear.
        */
        Scene scene = new Scene(this.root, WINDOW_WIDTH, WINDOW_HEIGHT);

        /*
          Here we add smaller Groups of assets to our main Group. The `board`
          Group contains board-related assets, `solution` contains solution-related
          assets, `controls` contains buttons, text fields et cetera, and `caterpillars`
          contains the caterpillar pieces.
         */
        this.root.getChildren().add(this.apple);
        this.root.getChildren().add(this.requirements);
        this.root.getChildren().add(this.controls);
        this.root.getChildren().add(this.caterpillars);

        this.newGame();
        this.makeControls();

        stage.setScene(scene);
        stage.show();
    }



}
