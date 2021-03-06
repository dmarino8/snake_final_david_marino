package marino.david_snake_javafx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import marino.david_snake_javafx.barriers.Walls;
import marino.david_snake_javafx.enemys.Enemy;
import marino.david_snake_javafx.enemys.EnemyFactory;
import marino.david_snake_javafx.fruits.Fruit;
import marino.david_snake_javafx.fruits.FruitFactory;
import marino.david_snake_javafx.levels.LevelCreation;
import marino.david_snake_javafx.snakes.BasicSnake;

import java.io.File;
import java.util.Random;

import static marino.david_snake_javafx.Collision.hitbox;
import static marino.david_snake_javafx.CreateNewFood.newFood;

public class Main_Snake extends Application {
    static int speed = 0;
    static int speedTemp = 5;
    static String foodcolor = "white";
    static String colorTemp = "white";
    static int width = 20;
    static int height = 20;
    static int foodX = 0;
    static int foodY = 0;
    static int cornersize = 25;
    static boolean gameOver = false;
    static Random rand = new Random();
    static FruitFactory fruitFactory = new FruitFactory();
    static EnemyFactory factory = new EnemyFactory();
    static Enemy enemy = factory.getEnemy("BASIC");
    static int count = 0;
    static int score = 0;
    static int life = 0;
    static BasicSnake snake = new BasicSnake();
    static LevelCreation level = new LevelCreation(new File("C:\\Users\\david\\snake_final_david_marino\\snake_final\\snake_final\\david_snake_JavaFX\\src\\main\\java\\marino\\david_snake_javafx\\level1.png"));
    static Walls walls = new Walls(level.getWallLayout());
    static boolean[][] wallPositions = walls.getWallPositions();

    public void start(Stage primaryStage) {
        try {
            VBox root = new VBox();
            Canvas c = new Canvas(width * cornersize, height * cornersize);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer() {
                long lastTick = 0;

                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(gc);
                        return;
                    }
                    if (now - lastTick > 1) { //fix this so it's not weird
                        lastTick = now;
                        tick(gc);
                    }
                }
            }.start();
            Scene scene = new Scene(root, width * cornersize, height * cornersize);
            //Input Listener
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode() == KeyCode.UP) {
                    snake.setVelocity(6);
                }
                if (key.getCode() == KeyCode.LEFT) {
                    snake.setDir(snake.getDir() + 90);
                }
                if (key.getCode() == KeyCode.DOWN) {
                    snake.setVelocity(2);
                }
                if (key.getCode() == KeyCode.RIGHT) {
                    snake.setDir(snake.getDir() - 90);
                }
            });
            scene.addEventFilter(KeyEvent.KEY_RELEASED, key -> {
                if (key.getCode() == KeyCode.UP) {
                    snake.setVelocity(4);
                }
                if (key.getCode() == KeyCode.DOWN) {
                    snake.setVelocity(4);
                }
            });
            primaryStage.setScene(scene);
            primaryStage.setTitle("SNAKE GAME");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        level.getPixelString(0, 0);
    }

    public static void tick(GraphicsContext gc) {
        count++;
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 100, 250);
            return;
        }
        //set snake image snake speed
        if (count % (10 - snake.getVelocity()) == 0) {
            snake.move();
        }


        //food hit detection
        if (foodX == snake.getParts().get(0).x && foodY == snake.getParts().get(0).y) {
            Fruit fruit = fruitFactory.getFruit(randFruit());

            speed = speedTemp;
            foodcolor = colorTemp;
            if (fruit.getName().equalsIgnoreCase("inverse")) {

            }
            if (snake.getParts().size() == 5) {
                life++;
            } else {
                snake.getParts().add(new Corner(-1, -1));
            }

            newFood();


            speedTemp = fruit.getSpeed();
            foodcolor = fruit.getColor();
        }
        //self death condition
        for (int i = 1; i < snake.getParts().size(); i++) {
            if (snake.getParts().get(0).x == snake.getParts().get(i).x && snake.getParts().get(0).y == snake.getParts().get(i).y) {
                gameOver = false;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (wallPositions[y][x] == true) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(y,x,25, 25);
                } else {
                    gc.setFill(Color.WHITE);
                    gc.fillRect(y,x,25, 25);
                }

            }
        }

        //enemy hit condition
        if (hitbox(snake.getParts().get(0).x * 25, snake.getParts().get(0).y * 25, enemy.getX(), enemy.getY())) {
            score++;
            //conn.connect();
        }

        //set score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 30));
        gc.fillText("Score:" + score, 10, 30);

        //set food color and image
        Color cc = Color.WHITE;
        switch (foodcolor.toLowerCase()) {
            case "purple":
                cc = Color.PURPLE;
                break;
            case "green":
                cc = Color.GREEN;
                break;
            case "yellow":
                cc = Color.YELLOW;
                break;
        }
        gc.setFill(cc);
        gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

        //set snake image
        for (Corner c : snake.getParts()) {
            gc.setFill(Color.GREEN);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);
        }

        //set enemy image
        gc.setFill(Color.RED);
        gc.fillRect(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());

        //enemy speed
        if (count % (10 - enemy.getSpeed()) == 0) {
            enemy.update();
        }


    }

    public static String randFruit() {
        int randNum = rand.nextInt(2);
        switch (randNum) {
            case 0:
                return "SPEED";
            case 1:
                return "SLOW";
            case 2:
                return "SPEED";
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
