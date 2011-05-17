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
public class Engine extends Observable {

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
	 * Scores rewarded for hitting / punished for missing
	 */
	private static final int SCORE_MISS_PUNISH = 5000;
	private static final int SCORE_HIT_REWARD = 1000;
	
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
	 * Amount of overdue (and therefore lost) targets this round
	 */
	private int overdue;
	
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
	}
	
	/**
	 * Displays the Engine.
	 */
	public void show() {
		window.show();
	}
	
	protected void changedNotify() {
		setChanged();
		notifyObservers();
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
		if ((getRoundTime() >= MAX_ROUND_TIME) || (overdue > 5)) {
			setRunning(false);
		}
		
		// remove overdue targets
		for (int i = 0; i < targets.size(); i++) {
			if (targets.get(i).checkOverdue()) {
				targets.remove(i);
				
				overdue++;
				changedNotify();
			}
		}
		
		window.repaint();
	}
	
	/**
	 * Called whenever a click on (x,y) occurred.
	 * @param x
	 * @param y
	 */
	public void clicked(int x, int y) {
		if (!running) {
			setRunning(true);
			return;
		}
		
		shots++;
		
		boolean hitSomething = false;
		
		// remove hit targets & award credits
		for (int i = 0; i < targets.size(); i++) {
			if (targets.get(i).checkHit(x, y)) {
				credits += SCORE_HIT_REWARD;
				hitSomething = true;
				targets.remove(i);
				break;
			}
		}
		
		if (!hitSomething) {
			misses++;
			credits -= SCORE_MISS_PUNISH;
		}
		
		changedNotify();
	}
	
	/**
	 * Draws all necessary stuff.
	 */
	public void draw(GC bufferGC, Display display) {
		for (Target t : targets) {
			t.draw(bufferGC, display);
		}
	}
	
	/**
	 * Set running on or off.
	 * 
	 * @param running true, if you want to start a new round,
	 *                false, if you want to abort the game.
	 */
	protected void setRunning(boolean running) {
		// start new round
		if (running) {
			roundStart = System.currentTimeMillis();
			credits = 0;
			overdue = 0;
			shots = 0;
			misses = 0;
			changedNotify();
			
			targets.clear();
			generateTargets();
			
			this.running = true;
		
		// stop game
		} else {
			targets.clear();
			this.running = false;
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public int getCredits() {
		return credits;		
	}
	
	public int getOverdue() {
		return this.overdue;
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

}
