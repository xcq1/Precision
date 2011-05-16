package com.xcq1.precision.view;

import java.util.Observable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.xcq1.precision.controller.Engine;

/**
 * Main window of Precision. Is observed by the Engine.
 * 
 * @author tobias_kuhn
 *
 */
public class Window extends Observable {
	
	/**
	 * width & height of the window in pixels
	 */
	public final static int SIZE = 600;
	
	/**
	 * distance between net lines
	 */
	private final static int LINE_DISTANCE = 5;
	
	/**
	 * Shell associated with this window
	 */	
	private final Shell shell;
	
	/**
	 * Engine associated with this Window
	 */
	private final Engine engine;
			
	/**
	 * Initializes a new window
	 * @param display
	 * @param engine 
	 */
	public Window(Display display, Engine engine) {
		this.engine = engine;
		shell = new Shell(display, SWT.DIALOG_TRIM);
				
		shell.setText("Precision");
		shell.setSize(SIZE, SIZE);
		
		center();
		
		shell.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				setChanged();
				notifyObservers(e);
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {				
			}
		});
		
		shell.addPaintListener(new PaintListener() {			
			@Override
			public void paintControl(PaintEvent e) {
				// background = black
				Color black = new Color(e.display, 0, 0, 0);
				e.gc.setBackground(black);
				e.gc.fillRectangle(e.gc.getClipping());
				
				// net of vertical & horizontal gray lines
				Color gray = new Color(e.display, 128, 128, 128);
				e.gc.setForeground(gray);
				for (int i = 0; i <= SIZE / LINE_DISTANCE; i++) {
					e.gc.drawLine(i * LINE_DISTANCE, 0, i * LINE_DISTANCE, SIZE);
					e.gc.drawLine(0, i * LINE_DISTANCE, SIZE, i * LINE_DISTANCE);
				}
				
				black.dispose();
				gray.dispose();
				setChanged();
				notifyObservers(e);
				
			}
		});		
	}
	
	/**
	 * Shows this window and executes the MEL. 
	 */
	public void show() {
		shell.open();
		
		// main event loop
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}	
	}

	/**
	 * Centers this window.
	 */
	protected void center() {
		Rectangle screenSize = shell.getMonitor().getBounds();
		Point shellSize = shell.getSize();
		
		int newLeft = (screenSize.width - shellSize.x) / 2;
		int newTop = (screenSize.height - shellSize.y) / 2;
		
		shell.setBounds(newLeft, newTop, shellSize.x, shellSize.y);
	}
	
}
