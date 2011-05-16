package com.xcq1.precision;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

import com.xcq1.precision.controller.Engine;

/**
 * Main class of precision
 * 
 * @author tobias_kuhn
 *
 */
public class Precision {

	/**
	 * Main method called during run
	 * @param args
	 */
	public static void main(String[] args) {
		
		// SWT Display
		Display display = new Display();
		
		// Game Engine
		final Engine engine = new Engine(display);
		
		Timer t = new Timer();
		TimerTask tt = new TimerTask() {			
			@Override
			public void run() {
				engine.tick();
			}
		};
		t.scheduleAtFixedRate(tt, 0L, 30L);				
		
		engine.show();
		t.cancel();
		t.purge();
				
		display.dispose();
	}
	
}
