import java.awt.Color;
import javax.swing.ImageIcon;
import csta.ibm.pong.Game;
import csta.ibm.pong.GameObject;

public class Lane extends GameObject {
	private Obstacle[] obstacles;
	private int obstacleWidth = 20;
	private int spacing;
	private int startingPoint;
	private int dx;
	/**
	 * Creates an empty Lane object
	 */
	public Lane () {
		obstacles = new Obstacle[0];
	}
	/**
	 * Creates a Lane object that contains obstacles
	 * @param numObstacles is the number of obstacles in the lane
	 * @param dx is the dx of the obstacles in the lane
	 */
	public Lane (int numObstacles, int dx) {
		obstacles = new Obstacle[numObstacles];
		this.dx = dx;
		for (int i = 0; i < obstacles.length; i++) {
			obstacles[i] = new Obstacle(dx);
		}
	}
	/**
	 * Sets the starting point for the obstacles
	 * @param start is the starting point for the obstacles
	 */
	public void setObstacleStart (int start) {
		startingPoint = start;
	}
	/**
	 * Sets the spacing between multiple obstacles
	 * @param spacing is the spacing between obstacles
	 */
	public void setSpacing (int spacing) {
		// Makes spacing negative if dx is negative
		if (dx > 0) {
			this.spacing = spacing;
		}
		else {
			this.spacing = -spacing;
		}
	}
	/**
	 * Sets the width of the obstacles
	 * @param width is the width of the obstacles
	 */
	public void setObstacleWidth (int width) {
		obstacleWidth = width;
	}
	/**
	 * Returns the dx value of the lane
	 * @return the dx value of the lane
	 */
	public int getDX () {
		return dx;
	}
	/**
	 * Sets the obstacles to a new color
	 * @param color is the new color of the obstacles
	 */
	public void setObstacleColor (Color color) {
		for (int i = 0; i < obstacles.length; i++) {
			obstacles[i].setColor(color);
		}
	}
	/**
	 * Sets the obstacles to a new image
	 * @param image is the new image of the obstacles
	 */
	public void setObstacleImage (ImageIcon image) {
		for (int i = 0; i < obstacles.length; i++) {
			obstacles[i].setImage(image);
		}
	}
	/**
	 * Draws the Lane object with all its obstacles onto the screen
	 * @param g is the Game object that the Lane will be drawn on
	 */
	public void draw (Game g) {
		for (int i = 0; i < obstacles.length; i++) {
			obstacles[i].setSize(obstacleWidth, getHeight());
			obstacles[i].setX(startingPoint - (spacing + obstacleWidth * (int)Math.signum(spacing)) * i);
			obstacles[i].setY(getY());
			g.add(obstacles[i].getImage());
			g.add(obstacles[i]);
		}
		g.add(this);
	}
	/**
	 * Removes the lane object and its obstacles from the screen
	 * @param g is the Game object that the Lane will be removed from
	 */
	public void delete (Game g) {
		for (int i = 0; i < obstacles.length; i++) {
			g.remove(obstacles[i].getImage());
			g.remove(obstacles[i]);
		}
		g.remove(this);
	}
	/**
	 * Checks if a GameObject collides with any obstacles
	 * @param o is the GameObject to be checked
	 */
	@Override
	public boolean collides (GameObject o) {
		for (int i = 0; i < obstacles.length; i++) {
			if (obstacles[i].collides(o)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Contains the actions of a lane each tick
	 */
	public void act() {
		// If dx is positive move obstacles to 0 when they leave the screen
		if (dx > 0) {
			for (int i = 0; i < obstacles.length; i++) {
				if (obstacles[i].getX() > getWidth()) {
					obstacles[i].setX(-obstacles[i].getWidth());
				}
			}
		}
		// If dx is negative move obstacle to width of the Lane object when they leave the screen
		else if (dx < 0){
			for (int i = 0; i < obstacles.length; i++) {
				if (obstacles[i].getX() + obstacles[i].getWidth() < 0) {
					obstacles[i].setX(getWidth());
				}
			}
		}
		
	}
}
