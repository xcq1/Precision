package com.xcq1.precision;

import org.eclipse.swt.widgets.Display;

import com.xcq1.precision.view.Window;

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
		
		Display display = new Display();
		
		Window window = new Window(display);
		window.show();
		
		display.dispose();
	}
	
}
