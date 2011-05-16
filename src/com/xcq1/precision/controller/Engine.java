package com.xcq1.precision.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import com.xcq1.precision.model.Target;
import com.xcq1.precision.view.Window;

/**
 * Engine of Precision. Observes the View for further changes.
 * 
 * @author tobias_kuhn
 *
 */
public class Engine implements Observer {

	/**
	 * Maximum duration of one round. 
	 */
	private static final long MAX_ROUND_TIME = 180000L;
	
	/**
	 * Amount of targets always present in one round.
	 */
	private static final int TARGET_BASE_COUNT = 10;
	
	/**
	 * Amount of targets added per 15 seconds.
	 */
	private static final int TARGET_ADD_COUNT = 5;
	
	/**
	 * Storage of the targets currently in use
	 */
	private List<Target> targets;
	
	/**
	 * Time on which this round began (in ms) 
	 */
	private long roundStart;
	
	/**
	 * Points scored in this round
	 */
	private int credits;
	
	/**
	 * Amount of shots fired & missed during this round.
	 */
	private int shots, misses;
	
	/**
	 * Whether a round is currently running.
	 */
	private boolean running;
	
	/**
	 * Current window to be displayed 
	 */
	private Window window;
	
	/**
	 * Initializes a new Engine.
	 * @param display 
	 */
	public Engine(Display display) {
		running = false;
		targets = new ArrayList<Target>();		
		window = new Window(display, this);
		window.addObserver(this);
	}
	
	/**
	 * Displays the Engine.
	 */
	public void show() {
		window.show();
	}
	
	/**
	 * Starts a new round.
	 */
	public void newRound() {
		roundStart = System.currentTimeMillis();
		credits = 0;
		shots = 0;
		misses = 0;
		
		targets.clear();
		generateTargets();
		
		running = true;
	}
	
	/**
	 * Generates all the targets for one round.
	 */
	private void generateTargets() {
		for (int phase = 0; phase < 12; phase++) {
			
			long phaseTime = roundStart + phase * 15000L;
			long randTime = 15000L - Target.FADING_TIME;
			int targetCount = TARGET_BASE_COUNT + phase * TARGET_ADD_COUNT;
			
			for (int target = 0; target < targetCount; target++) {
				long targetTime = phaseTime + (long) (randTime * Math.random());
				targets.add(new Target(targetTime));
			}
		}		
	}

	/**
	 * Called whenever action should be done.
	 * Regularly called from a different thread at approximately 33 fps.
	 */
	public synchronized void tick() {
		if (getRoundTime() >= MAX_ROUND_TIME) {
			running = false;
		}
		
		// remove overdue targets
		for (int i = 0; i < targets.size(); i++) {
			if (targets.get(i).checkOverdue()) {
				targets.remove(i);
			}
		}
		
		window.repaint();
	}
	
	/**
	 * Called whenever a click on (x,y) occurred.
	 * @param x
	 * @param y
	 */
	protected void clicked(int x, int y) {
		shots++;
		
		boolean hitSomething = false;
		
		// remove hit targets & award credits
		for (int i = 0; i < targets.size(); i++) {
			if (targets.get(i).checkHit(x, y)) {
				credits += 1;
				hitSomething = true;
				targets.remove(i);
			}
		}
		
		if (!hitSomething) {
			misses++;
		}
	}
	
	/**
	 * Draws all necessary stuff.
	 */
	public void draw(GC bufferGC, Display display) {
		for (Target t : targets) {
			t.draw(bufferGC, display);
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public int getCredits() {
		return credits;		
	}
	
	public int getShots() {
		return shots;
	}
	
	public int getMisses() {
		return misses;
	}
	
	public long getRoundTime() {
		return System.currentTimeMillis() - roundStart;
	}

	/**
	 * All information flow from the Window to the Engine is handled
	 * in this method.
	 */
	@Override
	public void update(Observable o, Object arg) {
		// mouse click
		if (arg instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) arg;
			
			if (!running) {
				newRound();
			} else {
				clicked(me.x, me.y);
			}
		}
	}
	
}
