package com.xcq1.precision.model;

import java.util.ArrayList;
import java.util.List;


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
	 * Initializes a new Engine.
	 */
	public Engine() {
		running = false;
		targets = new ArrayList<Target>();
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
