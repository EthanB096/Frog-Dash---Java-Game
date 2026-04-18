import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

import csta.ibm.pong.Game;

public class Menu {
	private Game screen;
	private JLabel[] choices;
	private Action[] actions;
	private int currentText;
	private boolean closed = true;
	/**
	 * Creates a Menu object
	 * @param numTexts is the amount of text labels
	 * @param g is the Game object that the Menu will draw on
	 */
	public Menu (int numTexts, Game g) {
		screen = g;
		choices = new JLabel[numTexts];
		actions = new Action[choices.length];
		int spacing = screen.getFieldHeight() / 10 + 20;
		// Sets initial y-values, font and size of labels
		for (int i = 0; i < choices.length; i++) {
			choices[i] = new JLabel();
			choices[i].setForeground(Color.WHITE);
			choices[i].setFont(new Font("Calibri", Font.BOLD, 20));
			choices[i].setLocation(0, 100 + i * spacing);
		}
		choices[currentText].setForeground(Color.YELLOW);
	}
	/**
	 * Sets the starting point for the first label
	 * @param start is the y-value of the first label
	 */
	public void setStart (int start) {
		int spacing = screen.getFieldHeight() / 10 + 20;
		for (int i = 0; i < choices.length; i++) {
			choices[i].setLocation(choices[i].getX(), start + i * spacing);
		}
	}
	/**
	 * Sets the font of the labels
	 * @param font is the new font of the labels
	 */
	public void setFont (Font font) {
		for (int i = 0; i < choices.length; i++) {
			choices[i].setFont(font);
			makeText(i, getText(i));
		}
	}
	/**
	 * Sets the spacing between labels
	 * @param spacing is the new spacing between labels
	 */
	public void setSpacing (int spacing) {
		int start = choices[0].getY();
		for (int i = 0; i < choices.length; i++) {
			choices[i].setLocation(choices[i].getX(), start + i * spacing);
		}
	}
	/**
	 * Opens the menu and makes it visible
	 */
	public void open() {
		for (int i = 0; i < choices.length; i++) {
			screen.add(choices[i]);
		}
		screen.repaint();
		closed = false;
	}
	/**
	 * Makes the text at specified index
	 * @param index is the position of the text to be changed
	 * @param text is the new text at index
	 */
	public void makeText (int index, String text) {
		choices[index].setText(text);
		Dimension size = new Dimension(choices[index].getPreferredSize());
		size.setSize((int)(size.getWidth() * 1.1), (int)(size.getHeight() * 1.1));
		choices[index].setSize(size);
		choices[index].setLocation(screen.getFieldWidth() / 2 - choices[index].getWidth() / 2,
							   choices[index].getY());
	}
	/**
	 * Gets the text from the label at index
	 * @param index is the position of the text to be gotten
	 * @return the text from the label at index
	 */
	public String getText(int index) {
		if (choices[index].getText() == null) {
			return "";
		}
		return choices[index].getText();
	}
	/**
	 * Moves down the menu
	 */
	public void next () {
		choices[currentText].setForeground(Color.WHITE);
		currentText += 1;
		if (currentText == choices.length) {
			currentText = 0;
		}
		choices[currentText].setForeground(Color.YELLOW);
		screen.repaint();
	}
	/**
	 * Moves up the menu
	 */
	public void previous () {
		choices[currentText].setForeground(Color.WHITE);
		currentText -= 1;
		if (currentText < 0) {
			currentText = choices.length - 1;
		}
		choices[currentText].setForeground(Color.YELLOW);
		screen.repaint();
	}
	/**
	 * Closes the menu and removes it from screen
	 */
	public void close () {
		for (int i = 0; i < choices.length; i++) {
			screen.remove(choices[i]);
		}
		screen.repaint();
		closed = true;
	}
	/**
	 * Returns whether or not the menu is closed
	 * @return true if the menu is closed
	 */
	public boolean isClosed () {
		return closed;
	}
	/**
	 *	Returns the current index of the menu
	 * @return the current index
	 */
	public int getIndex () {
		return currentText;
	}
	/**
	 * Sets the highlight on to a label; This does not change the index
	 * @param index is the position of the label to be highlighted
	 */
	public void setHighlight (int index) {
		choices[currentText].setForeground(Color.WHITE);
		choices[index].setForeground(Color.YELLOW);
	}
	/**
	 * Removes the highlight from the menu
	 */
	public void removeHighlight () {
		choices[currentText].setForeground(Color.WHITE);
	}
	/**
	 * Gives the action for a label to run
	 * @param index is the index of the label to be given the action
	 * @param action is the action that is given to the label at index
	 */
	public void giveAction (int index, Action action) {
		actions[index] = action;
	}
	/**
	 * Runs the action of the label at index; Does not run if menu is closed
	 * @param index is the index of the label to be run
	 */
	public void runAction (int index) {
		if (closed) {
			return;
		}
		actions[index].run();
	}
}
