import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import csta.ibm.pong.Game;

public class Frogger extends Game implements KeyListener {
	private Frog frog;
	private JLabel frogImage;
	private JLabel[] housedFrogs = new JLabel[5];
	private int lives = 6;
	private Frog[] livesDisplay = new Frog[lives];
	private Lane[] lanes = new Lane[14];
	private Lane timerDisplay;
	private DecimalFormat pointsFormat = new DecimalFormat("00000");
	private JLabel pointsScore;
	private JLabel timeText;
	private JLabel oneUpText;
	private JLabel hiScoreText;
	private JLabel hiScoreValue;
	private Menu mainMenu;
	private Menu hiScore;
	private Menu saver;
	private Menu tutorial;
	private int points;
	private int furthestFrog;
	private final int DELAY = 20;
	private int timer;
	private boolean noRelease = false;
	private boolean started = false;
	/**
	 * Sets up the game of Frogger
	 */
	public void setup() {
		File file = new File("highScore.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException f) {
		}
		// Creates menu tutorial pop up
		JLabel menuPopUp = new JLabel("W and S to navigate the menu; Space to interact.");
		menuPopUp.setForeground(Color.WHITE);
		menuPopUp.setFont(new Font("Calibri", Font.BOLD, 15));
		menuPopUp.setSize((int)(menuPopUp.getPreferredSize().getWidth() * 1.1), 15); // Ensures text fits in JLabel
		menuPopUp.setLocation((getFieldWidth() - menuPopUp.getWidth()) / 2, 20);
		add(menuPopUp);
		// Creates main menu
		mainMenu = new Menu(3, this);
		mainMenu.makeText(0, "START");
		mainMenu.makeText(1, "HI-SCORE");
		mainMenu.makeText(2, "TUTORIAL");
		mainMenu.giveAction(0, () -> {
			started = true;
			remove(menuPopUp);
			addAll();
			mainMenu.close();
		});
		mainMenu.giveAction(1, () -> {
			mainMenu.close();
			remove(menuPopUp);
			hiScore.open();
		});
		mainMenu.giveAction(2, () -> {
			remove(menuPopUp);
			mainMenu.close();
			tutorial.open();
		});
		mainMenu.open();
		// Creates high score menu
		hiScore = new Menu(6, this);
		for (int i = 0; i < 5; i++) {
			if (sc.hasNextLine()) {
				hiScore.makeText(i, sc.nextLine());
			} 
			else {
				hiScore.makeText(i, "00000 - ???"); // Default score if there is not a score
			}
		}
		hiScore.setStart(40);
		hiScore.makeText(5, "EXIT");
		hiScore.setHighlight(5);
		hiScore.giveAction(5, () -> {
			hiScore.close();
			add(menuPopUp);
			mainMenu.open();
		});
		// Creates tutorial menu
		tutorial = new Menu(11, this);
		tutorial.setFont(new Font("Calibri", Font.BOLD, 15));
		tutorial.makeText(0, "Use WASD to move the frog");
		tutorial.makeText(1, "Get the frog into the homes at the end");
		tutorial.makeText(2, "Avoid the obstacles");
		tutorial.makeText(3, "Get a point bonus by being fast");
		tutorial.makeText(4, "Set a high score!");
		tutorial.makeText(5, "POINT SCORING:");
		tutorial.makeText(6, "10 Points - New furthest distance this life");
		tutorial.makeText(7, "50 Points - Put frog into home");
		tutorial.makeText(8, "1000 Points - Get all homes filled");
		tutorial.makeText(9, "10 Points for every extra second when saving a frog");
		tutorial.makeText(10, "EXIT");
		tutorial.setStart(40);
		tutorial.setHighlight(10);
		tutorial.setFont(new Font("Calibri", Font.BOLD, 15));
		tutorial.setSpacing(25);
		tutorial.giveAction(10, () -> {
			tutorial.close();
			add(menuPopUp);
			mainMenu.open();
		});
		// Creates the "HI-SCORE" text
		hiScoreText = new JLabel("HI-SCORE");
		hiScoreText.setForeground(Color.WHITE);
		hiScoreText.setFont(new Font("Calibri", Font.BOLD, 25));
		hiScoreText.setBounds(160, 0, 100, 25);
		// Creates the high score number text
		hiScoreValue = new JLabel(pointsFormat.format(Integer.parseInt(hiScore.getText(0).split(" ")[0])));
		hiScoreValue.setForeground(Color.WHITE);
		hiScoreValue.setFont(new Font("Calibri", Font.BOLD, 25));
		hiScoreValue.setBounds(160, 18, 100, 25);
		// Creates "1-UP" text
		oneUpText = new JLabel("1-UP");
		oneUpText.setForeground(Color.WHITE);
		oneUpText.setFont(new Font("Calibri", Font.BOLD, 25));
		oneUpText.setBounds(60, 0, 60, 25);
		// Creates points text
		pointsScore = new JLabel(pointsFormat.format(points));
		pointsScore.setForeground(Color.RED);
		pointsScore.setFont(new Font("Calibri", Font.BOLD, 25));
		pointsScore.setBounds(60, 18, 70, 25);
		// Creates "Time" text
		timeText = new JLabel("Time");
		timeText.setForeground(Color.WHITE);
		timeText.setFont(new Font("Calibri", Font.BOLD, 20));
		timeText.setBounds(getFieldWidth() - 45, getFieldHeight() - 15, 80, 20);
		// Creates and initializes the frog with an image
		frog = new Frog();
		frog.setSize(20, 20);
		frog.setX(200);
		frog.setY(getFieldHeight() - 40);
		frog.setVisible(false);
		frogImage = new JLabel(getImage("Frog-Up.png"));
		frogImage.setSize(20, 20);
		frogImage.setLocation(frog.getLocation());
		furthestFrog = frog.getY(); // Sets furthestFrog value to beginning frog y value (Used for point scoring)
		// Creates frogs in the frog homes and makes them invisible (Visible when scored)
		for (int i = 0; i < housedFrogs.length; i++) {
			housedFrogs[i] = new JLabel(getImage("Frog-Up.png"));
			housedFrogs[i].setSize(20, 20);
			housedFrogs[i].setLocation(25 + 80 * i, 60);
			housedFrogs[i].setVisible(false);
		}
		// Creates the lives display
		for (int i = 0; i < livesDisplay.length; i++) {
			livesDisplay[i] = new Frog();
			livesDisplay[i].setSize(10, 15);
			livesDisplay[i].setX(5 + i * 15);
			livesDisplay[i].setY(getFieldHeight() - 15);
			livesDisplay[i].setColor(Color.GREEN);
		}
		// Creates the bar timer
		timerDisplay = new Lane();
		timerDisplay.setX(115);
		timerDisplay.setY(getFieldHeight() - 10);
		timerDisplay.setColor(Color.GREEN);
		timerDisplay.setSize(225, 10);
		// Makes the lanes; 184-196 non-obstacle lanes, 197-245 obstacle lanes 
		lanes[0] = makeEmptyLane(0, getFieldHeight() - 40, new Color(107, 7, 181));
		lanes[6] = makeEmptyLane(0, getFieldHeight() - 160, new Color(107, 7, 181));
		lanes[12] = new Lane(5, 0);
		// Special "frog home" lane; Uses obstacles to create the "frog home" visual
		lanes[12].setX(0);
		lanes[12].setY(60);
		lanes[12].setSize(getFieldWidth(), 20);
		lanes[12].setColor(Color.GREEN);
		lanes[12].setObstacleWidth(30);
		lanes[12].setSpacing(50);
		lanes[12].setObstacleStart(20);
		lanes[12].setObstacleColor(Color.BLUE);
		lanes[13] = makeEmptyLane(0, 40, Color.GREEN);
		lanes[1] = new Lane(3, -2);
		lanes[1].setSpacing(60);
		lanes[1].setObstacleImage(getImage("Lane1-Car.png"));
		lanes[2] = new Lane(3, 1);
		lanes[2].setSpacing(50);
		lanes[2].setObstacleImage(getImage("Lane2-Car.png"));
		lanes[3] = new Lane(3, -2);
		lanes[3].setSpacing(60);
		lanes[3].setObstacleStart(getFieldWidth() + 70);
		lanes[3].setObstacleImage(getImage("Lane3-Car.png"));
		lanes[4] = new Lane(1, 5);
		lanes[4].setObstacleImage(getImage("Lane4-Car.png"));
		lanes[5] = new Lane(2, -3);
		lanes[5].setObstacleWidth(40);
		lanes[5].setSpacing(100);
		lanes[5].setObstacleImage(getImage("Lane5-Car.png"));
		lanes[7] = new Lane(4, -3);
		lanes[7].setSpacing(40);
		lanes[7].setObstacleWidth(60);
		lanes[7].setObstacleImage(getImage("Lake-Turtle.png"));
		lanes[8] = new Lane(3, 1);
		lanes[8].setSpacing(50);
		lanes[8].setObstacleWidth(60);
		lanes[8].setObstacleImage(getImage("Lake-LogSmall.png"));
		lanes[9] = new Lane(2, 4);
		lanes[9].setSpacing(50);
		lanes[9].setObstacleStart(-20);
		lanes[9].setObstacleWidth(160);
		lanes[9].setObstacleImage(getImage("Lake-LogLarge.png"));
		lanes[10] = new Lane(4, -3);
		lanes[10].setSpacing(40);
		lanes[10].setObstacleWidth(40);
		lanes[10].setObstacleImage(getImage("Lake-Turtle.png"));
		lanes[11] = new Lane(3, 2);
		lanes[11].setSpacing(40);
		lanes[11].setObstacleWidth(100);
		lanes[11].setObstacleImage(getImage("Lake-LogMedium.png"));
		for (int i = 1; i < 6; i++) {
			lanes[i].setY(getFieldHeight() - 40 - i * 20);
			lanes[i].setX(0);
			lanes[i].setSize(getFieldWidth(), 20);
			lanes[i].setColor(Color.BLACK);
		}
		for (int i = 7; i < 12; i++) {
			lanes[i].setY(getFieldHeight() - 40 - i * 20);
			lanes[i].setX(0);
			lanes[i].setSize(getFieldWidth(), 20);
			lanes[i].setColor(Color.BLUE);
		}
		// Adds keyListener to detect input
		addKeyListener(this);
		// Sets delay of program
		setDelay(DELAY);
	}
	/**
	 * Contains the actions of Frogger each tick
	 */
	public void act() {
		if (!started) {
			return;
		}
		// Only runs car collision code when frog is within car lanes
		if (frog.getY() >= 200 && frog.getY() <= 280) {
			// Checks if a car hits the frog; reduces lives and resets the frog if so
			for (int i = 1; i < 6; i++) {
				if (lanes[i].collides(frog)) {
					reduceLives();
					resetFrog();
				}
			}
		}
		// Only runs log/turtle collision if car is within water lanes
		if (frog.getY() >= 80 && frog.getY() <= 160) {
			boolean offLog = true; 
			// Checks if the frog is standing on any logs/turtles; updates frog position if so
			for (int i = 7; i < 12; i++) {
				if (lanes[i].collides(frog)) {
					offLog = false;
					frog.setX(frog.getX() + lanes[i].getDX());
					frogImage.setLocation(frog.getLocation());
					break;
				}
			}
			// Reduces lives and resets frog when frog is
			// - not on a log/turtle
			// - carried into screen border by a log/turtle
			if (offLog || frog.getX() <= 0 || frog.getX() + frog.getWidth() >= getFieldWidth()) {
				reduceLives();
				resetFrog();
			}
		}
		// Only runs frog home code when frog is within frog home lane
		if (frog.getY() == 60) {
			int midpoint = frog.getX() + frog.getWidth() / 2; // Gets midpoint of the frog
			// Checks if frog is going into a frog home that is empty
			if (midpoint >= 10 && midpoint <= 60 && !housedFrogs[0].isVisible()) {
				// Makes frog home visible
				housedFrogs[0].setVisible(true);
				// Increases points by 50 (for getting a frog into a home) + remaining time * 10 (time bonus)
				points += 50;
				points += timerDisplay.getWidth() / 5 * 10;
				pointsScore.setText(pointsFormat.format(points));
				// Updates hiScore text if current points is greater
				hiScoreValue.setText(pointsFormat.format(
							Math.max(Integer.parseInt(hiScore.getText(0).split(" ")[0]), points)));
			}
			else if (midpoint >= 90 && midpoint <= 140 && !housedFrogs[1].isVisible()) {
				housedFrogs[1].setVisible(true);
				points += 50;
				points += timerDisplay.getWidth() / 5 * 10;
				pointsScore.setText(pointsFormat.format(points));
				hiScoreValue.setText(pointsFormat.format(
						Math.max(Integer.parseInt(hiScore.getText(0).split(" ")[0]), points)));
			}
			else if (midpoint >= 170 && midpoint <= 220 && !housedFrogs[2].isVisible()) {
				housedFrogs[2].setVisible(true);
				points += 50;
				points += timerDisplay.getWidth() / 5 * 10;
				pointsScore.setText(pointsFormat.format(points));
				hiScoreValue.setText(pointsFormat.format(
						Math.max(Integer.parseInt(hiScore.getText(0).split(" ")[0]), points)));
			}
			else if (midpoint >= 250 && midpoint <= 300 && !housedFrogs[3].isVisible()) {
				housedFrogs[3].setVisible(true);
				points += 50;
				points += timerDisplay.getWidth() / 5 * 10;
				pointsScore.setText(pointsFormat.format(points));
				hiScoreValue.setText(pointsFormat.format(
						Math.max(Integer.parseInt(hiScore.getText(0).split(" ")[0]), points)));
			}
			else if (midpoint >= 330 && midpoint <= 380 && !housedFrogs[4].isVisible()) {
				housedFrogs[4].setVisible(true);
				points += 50;
				points += timerDisplay.getWidth() / 5 * 10;
				pointsScore.setText(pointsFormat.format(points));
				hiScoreValue.setText(pointsFormat.format(
						Math.max(Integer.parseInt(hiScore.getText(0).split(" ")[0]), points)));
			}
			// If frog misses all homes (or hit a non-empty home) reduces lives
			else {
				reduceLives();
			}
			resetFrog();
			// If all frog homes are filled, empty all of them and add 1000 to points (Full homes points)
			if (housedFrogs[0].isVisible() && housedFrogs[1].isVisible() && housedFrogs[2].isVisible() && 
				housedFrogs[3].isVisible() && housedFrogs[4].isVisible()) {
				points += 1000;
				// Updates hiScore text if current points is greater
				hiScoreValue.setText(pointsFormat.format(
						Math.max(Integer.parseInt(hiScore.getText(0).split(" ")[0]), points)));
				pointsScore.setText(pointsFormat.format(points));
				for (int i = 0; i < housedFrogs.length; i++) {
					housedFrogs[i].setVisible(false);
				}
			}
		}
		// Timer code
		timer++;
		// When timer is equal to game ticks per second, update timer
		if (timer == 1000 / DELAY) {
			// Reduces timer bar time from left to right
			timerDisplay.setX(timerDisplay.getX() + 5); // Moves timer to the right
			timerDisplay.setSize(timerDisplay.getWidth() - 5, 10); // Reduces timer width
			timer = 0; // Resets internal timer
			// If timer bar is empty, reduce lives and reset frog
			if (timerDisplay.getWidth() == 0) {
				reduceLives();
				resetFrog();
			}
		}
	}
	
	/**
	 * Reduces lives; Ends/restarts game and checks for high score if out of lives
	 */
	public void reduceLives () {
		// If lives are already 0 then this ends the game
		if (lives == 0) {
			started = false;
			lives = livesDisplay.length;
			deleteAll();
			repaint();
			getHighScore();
			return;
		}
		// Reduces lives and visual lives by one
		lives -= 1;
		livesDisplay[lives].setVisible(false);
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// Makes sure no keys are being held (Prevents hold the button)
		if (noRelease) {
			return;
		}
		noRelease = true;
		// Moves frog in corresponding direction when game is running; 398, 408, 413, 422
		if (e.getKeyChar() == 'w' && started) {
			frog.moveUp(20);
			frogImage.setIcon(getImage("Frog-Up.png"));
			frogImage.setLocation(frog.getLocation());
		} 
		// Controls main menu when game is not running; 404, 418
		else if (e.getKeyChar() == 'w' && !mainMenu.isClosed()) {
			mainMenu.previous();
			return;
		}
		if (e.getKeyChar() == 'a' && frog.getX() > 0 && started) {
			frog.moveLeft(20);
			frogImage.setIcon(getImage("Frog-Left.png"));
			frogImage.setLocation(frog.getLocation());
		}
		if (e.getKeyChar() == 's' && frog.getY() < getFieldHeight() - 40 && started) {
			frog.moveDown(20);
			frogImage.setIcon(getImage("Frog-Down.png"));
			frogImage.setLocation(frog.getLocation());
		}
		else if (e.getKeyChar() == 's' && !mainMenu.isClosed()) {
			mainMenu.next();
			return;
		}
		if (e.getKeyChar() == 'd' && frog.getX() < getFieldWidth() - 40 && started) {
			frog.moveRight(20);
			frogImage.setIcon(getImage("Frog-Right.png"));
			frogImage.setLocation(frog.getLocation());
		}
		// Runs current main menu action when main menu is open
		if (e.getKeyChar() == ' ' && !mainMenu.isClosed()) {
			mainMenu.runAction(mainMenu.getIndex());
			return;
		}
		// Runs hiScore action if hiScore is open
		else if (e.getKeyChar() == ' ' && !hiScore.isClosed()) {
			hiScore.runAction(5);
			return;
		}
		// Runs tutorial action if tutorial is open
		else if (e.getKeyChar() == ' ' && !tutorial.isClosed()) {
			tutorial.runAction(10);
			return;
		}
		// Increases score when the frog reaches a new lowest y-value (excluding 180 as that is an empty lane)
		if (frog.getY() < furthestFrog && frog.getY() != 180) {
			furthestFrog = frog.getY(); // Sets furthestFrog distance to new y-value
			// Updates points by 10 (furthest square points)
			points += 10;
			pointsScore.setText(pointsFormat.format(points));
			// Updates hiScore text if current points is greater
			hiScoreValue.setText(pointsFormat.format(
					Math.max(Integer.parseInt(hiScore.getText(0).split(" ")[0]), points)));
			return;
		}
		// Runs score saving actions when score save screen is open and initials aren't complete
		if (saver != null && !saver.isClosed() && saver.getText(1).length() < 3) {
			// If key is delete remove most recent letter if text is longer than 0
			if (e.getKeyChar() == '' && saver.getText(1).length() > 0) {
				saver.makeText(1, saver.getText(1).substring(0, saver.getText(1).length() - 1));
			}
			// Don't do anything if delete is pressed when text are empty
			else if (e.getKeyChar() == '') {
			}
			// Adds the letter to the end
			else {
				saver.makeText(1, saver.getText(1) + Character.toUpperCase(e.getKeyChar()));
			}
			// If initials are complete write into file
			if (saver.getText(1).length() == 3) {
				PrintWriter pw = null;
				try {
					pw = new PrintWriter("highScore.txt");
				} catch (FileNotFoundException f) {
				}
				// Only increments index when pre-established scores is selected
				// so that it shifts down scores properly
				int index = 0;
				for (int i = 0; i < 5; i++) { 
					if (points > Integer.parseInt(hiScore.getText(index).split(" ")[0])) {
						pw.println(pointsFormat.format(points) + " - " + saver.getText(1));
						points = 0;
					}
					else {
						pw.println(hiScore.getText(index));
						index++;
					}
				}
				pw.close();
				saver.close();
				setup();
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// Changes noRelease to false when key is released
		noRelease = false;
	}
	
	public static void main (String[] args) {
		Frogger frogger = new Frogger();
		frogger.setResizable(false);
		frogger.setVisible(true);
		frogger.setTitle("Frogger");
		if (frogger.getFieldWidth() > 386 || frogger.getFieldHeight() > 340) {
			frogger.setSize(400 - (frogger.getFieldWidth() - 386), 400 - (frogger.getFieldHeight() - 340));
		}
		frogger.initComponents();
	}
	
	/**
	 * Resets the frog to the beginning; Resets timer
	 */
	private void resetFrog () {
		frog.setX(200);
		frog.setY(getFieldHeight() - 40);
		frogImage.setLocation(frog.getLocation());
		furthestFrog = frog.getY(); // Sets furthestFrog back to the beginning
		// Resets timer
		timerDisplay.setX(115);
		timerDisplay.setSize(225, 10);
		
	}
	
	/**
	 * Checks if it is a high score; Creates high score screen if so; Returns to menu if not
	 */
	private void getHighScore () {
		// Checks if current points are <= than lowest high score
		if (points <= Integer.parseInt(hiScore.getText(4).split(" ")[0])) {
			points = 0;
			setup();
			return;
		}
		saver = new Menu(3, this);
		saver.makeText(0, "HI-SCORE! TYPE YOUR INITIALS");
		saver.makeText(2, "YOUR SCORE: " + pointsFormat.format(points));
		saver.setStart(80);
		saver.removeHighlight();
		saver.open();
	}
	
	/**
	 * Makes an empty lane
	 * @param x is the x-value of the lane
	 * @param y is the y-value of the lane
	 * @param color is the color of the lane
	 * @return the empty lane
	 */
	private Lane makeEmptyLane (int x, int y, Color color) {
		Lane lane = new Lane();
		lane.setY(y);
		lane.setX(x);
		lane.setSize(getFieldWidth(), 20);
		lane.setColor(color);
		return lane;
	}
	/**
	 * Adds all components to screen
	 */
	private void addAll () {
		add(frogImage);
		add(frog);
		for (int i = 0; i < housedFrogs.length; i++) {
			add(housedFrogs[i]);
		}
		for (int i = 0; i < lives; i++) {
			add(livesDisplay[i]);
		}
		for (int i = 0; i < lanes.length; i++) {
			lanes[i].draw(this);
		}
		add(timerDisplay);
		add(pointsScore);
		add(timeText);
		add(oneUpText);
		add(hiScoreText);
		add(hiScoreValue);
	}
	/**
	 * Removes all components from screen
	 */
	private void deleteAll () {
		remove(frogImage);
		remove(frog);
		for (int i = 0; i < housedFrogs.length; i++) {
			remove(housedFrogs[i]);
		}
		for (int i = 0; i < lives; i++) {
			remove(livesDisplay[i]);
		}
		for (int i = 0; i < lanes.length; i++) {
			lanes[i].delete(this);
		}
		remove(timerDisplay);
		remove(pointsScore);
		remove(timeText);
		remove(oneUpText);
		remove(hiScoreText);
		remove(hiScoreValue);
	}
	/**
	 * Gets embedded image with file name
	 * @param file is the name of the image file
	 * @return the image with file name
	 */
	private ImageIcon getImage (String file) {
		try {
			BufferedImage image = ImageIO.read(getClass().getResourceAsStream(file));
			return new ImageIcon(image);
		} catch (IOException e) {
			return new ImageIcon();
		}
	}
}
