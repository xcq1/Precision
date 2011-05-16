package com.xcq1.precision.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Display;

import com.xcq1.precision.model.Target;
import com.xcq1.precision.view.Window;


public class Engine {

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
		window = new Window(display);
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
		running = true;
	}
	
	/**
	 * Called whenever action should be done.
	 * (TODO think of a timer or something
	 */
	public void tick() {
		// TODO find a fancy way to add new Targets
		
		// remove overdue targets
		for (int i = 0; i <= targets.size(); i++) {
			if (targets.get(i).checkOverdue()) {
				targets.remove(i);
			}
		}
	}
	
	/**
	 * Called whenever a click on (x,y) occurred.
	 * @param x
	 * @param y
	 */
	public void clicked(int x, int y) {
		shots++;
		
		boolean hitSomething = false;
		
		// remove hit targets & award credits
		for (int i = 0; i <= targets.size(); i++) {
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
	public void draw(PaintEvent e) {
		for (Target t : targets) {
			t.draw(e);
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
	
}
