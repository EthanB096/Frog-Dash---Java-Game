
import csta.ibm.pong.GameObject;

public class Frog extends GameObject {
	/**
	 * Creates a Frog object
	 */
	public Frog () {
	}
	/**
	 * Contains the actions of a frog each tick
	 */
	public void act() {
		
	}
	/**
	 * Moves the frog up by distance
	 * @param distance is the amount to be moved
	 */
	public void moveUp (int distance) {
		setY(getY() - distance);
	}
	/**
	 * Moves the frog down by distance
	 * @param distance is the amount to be moved
	 */
	public void moveDown (int distance) {
		setY(getY() + distance);
	}
	/**
	 * Moves the frog right by distance
	 * @param distance is the amount to be moved
	 */
	public void moveRight (int distance) {
		setX(getX() + distance);
	}
	/**
	 * Moves the frog left by distance
	 * @param distance is the amount to be moved
	 */
	public void moveLeft (int distance) {
		setX(getX() - distance);
	}
}
