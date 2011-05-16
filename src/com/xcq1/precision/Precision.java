package com.xcq1.precision;

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
		Engine engine = new Engine(display);
		engine.show();
		
		display.dispose();
	}
	
}
