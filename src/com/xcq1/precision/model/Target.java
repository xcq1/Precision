package com.xcq1.precision.model;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.xcq1.precision.view.Window;


public class Target {
	
	/**
	 * The time necessary to fade in and out, respectively.
	 */
	private static final long FADING_TIME = 4000;
	
	/**
	 * The maximum size the target reaches after FADING_TIME.
	 */
	private static final float MAX_SIZE = 32f;

	/**
	 * The center of the target on screen
	 */
	private Point center;
	
	/**
	 * The time on which it was created
	 */
	private long created;
	
	/**
	 * Whether it was successfully shot.
	 */
	private boolean shot;
	
	/**
	 * Creates a new target.
	 */
	public Target() {
		created = System.currentTimeMillis();
		center.x = (int) (Math.random() * Window.SIZE);
		center.y = (int) (Math.random() * Window.SIZE);
		shot = false;
	}
	
	/**
	 * 
	 * @return the amount of milliseconds since this target exists.
	 */
	protected long getExistingMillis() {
		return (System.currentTimeMillis() - created);
	}
	
	/**
	 * Gets the size of the target.
	 * @return the radius from center in pixels.
	 */
	protected float getSize() {
		long exists = getExistingMillis();
		
		// should never happen
		if (exists <= 0) {		
			return 0f;
			
		// increasing in size
		} else if (exists <= FADING_TIME) {
			return (exists / FADING_TIME) * MAX_SIZE;
			
		// decreasing in size
		} else if (exists <= 2 * FADING_TIME) {
			return (1f - (exists / FADING_TIME)) * MAX_SIZE;
			
		// overdue
		} else {
			return 0f;
		}
	}
	
	/**
	 * Draws the target on the canvas.
	 * @param e paint event
	 */
	public void draw(PaintEvent e) {
		Color white = new Color(e.display, 255, 255, 255);
		Color green = new Color(e.display, 0, 0, 255);
		
		e.gc.setForeground(green);
		e.gc.setBackground(white);
		e.gc.setLineWidth(5);
		e.gc.setAlpha(128);
		
		int radius = (int) getSize();
		e.gc.drawOval(center.x - radius, center.y - radius,
					  center.x + radius, center.y + radius);
		
		white.dispose();
		green.dispose();		
	}
	
	/**
	 * Checks whether the coordinates hit the target.
	 * 	
	 * @param x
	 * @param y
	 * @return true if target is now shot, false otherwise
	 */
	public boolean checkHit(int x, int y) {
		float deltaX = x - center.x;
		float deltaY = y - center.y;
		
		shot |= (deltaX * deltaX + deltaY * deltaY) <= getSize();
		
		return shot;
	}
	
	/**
	 * Checks whether the target is no longer visible and therefore missed.
	 * @return true if target is overdue, false otherwise
	 */
	public boolean checkOverdue() {
		return getExistingMillis() > 2 * FADING_TIME;
	}
	
}
