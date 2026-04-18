/** Dec 22, 2024
 * 	Ethan Benaiah
 */
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import csta.ibm.pong.*;
public class Obstacle extends GameObject {
	private int dx;
	private JLabel image;
	/**
	 * Creates a Obstacle object
	 * @param dx is the dx of the object
	 */
	public Obstacle (int dx) {
		this.dx = dx;
		image = new JLabel();
	}
	/**
	 * Sets the image of the obstacle
	 * @param image is the new image of the obstacle
	 */
	public void setImage (ImageIcon image) {
		setVisible(false);
		this.image.setIcon(image);
	}
	/**
	 * Gets the label containing the image for the obstacle
	 * @return the label with the image
	 */
	public JLabel getImage () {
		return image;
	}
	/**
	 * Sets the size of the obstacle and image
	 * @param width is the new width of the obstacle
	 * @param height is the new height of the obstacle
	 */
	@Override
	public void setSize (int width, int height) {
		super.setSize(width, height);
		image.setSize(width, height);
	}
	/**
	 * Contains the actions of a obstacle each tick
	 */
	public void act() {
		setX(getX() + dx);
		image.setLocation(getX(), getY());
	}
}
