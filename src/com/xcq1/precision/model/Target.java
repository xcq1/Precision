package com.xcq1.precision.model;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import com.xcq1.precision.view.Window;


public class Target {
	
	/**
	 * The time necessary to fade in and out, respectively.
	 */
	public static final long FADING_TIME = 4000;
	
	/**
	 * The maximum radius the target reaches after FADING_TIME.
	 */
	private static final double MAX_RADIUS = 32f;

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
	 * Creates a new target now.
	 */
	public Target() {
		this(System.currentTimeMillis());
	}
	
	/**
	 * Creates a new target.
	 * @created when the target will be created
	 */
	public Target(long created) {
		this.created = created;
		
		double centerX = MAX_RADIUS + Math.random() * (Window.SIZE - MAX_RADIUS);
		double centerY = MAX_RADIUS + Math.random() * (Window.SIZE - MAX_RADIUS);
		center = new Point((int) (centerX),
						   (int) (centerY));
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
	protected double getSize() {
		long exists = getExistingMillis();
		
		// not yet existing
		if (exists <= 0) {		
			return 0f;
			
		// increasing in size
		} else if (exists <= FADING_TIME) {
			return (exists / (double) FADING_TIME) * MAX_RADIUS;
			
		// decreasing in size
		} else if (exists <= 2 * FADING_TIME) {
			return (2f - (exists / (double) FADING_TIME)) * MAX_RADIUS;
			
		// overdue
		} else {
			return 0f;
		}
	}
	
	/**
	 * Draws the target on the canvas.
	 * @param bufferGC paint event
	 */
	public void draw(GC bufferGC, Display display) {
		int radius = (int) getSize();
		if (radius == 0) {
			return;
		}
		
		Color white = new Color(display, 255, 255, 255);
		Color green = new Color(display, 0, 0, 255);
		
		bufferGC.setForeground(green);
		bufferGC.setBackground(white);
		bufferGC.setLineWidth(5);
		bufferGC.setAlpha(128);
		
		bufferGC.fillOval(center.x - radius, center.y - radius,
					      2 * radius, 2 * radius);
		
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
		double size = getSize();
		
		shot |= (deltaX * deltaX + deltaY * deltaY) <= size * size;
		
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
