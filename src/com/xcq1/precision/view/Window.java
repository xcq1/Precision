package com.xcq1.precision.view;

import java.util.Observable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
	public final static int INFO_SIZE = 200;
	
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
	@SuppressWarnings("unused")
	private final Engine engine;
	
	/**
	 * informative labels
	 */
	private Label roundLabel, scoreLabel, timeLabel, missedLabel,
				  hitsLabel, overdueLabel, accuracyLabel;
			
	/**
	 * Initializes a new window
	 * @param display
	 * @param engine 
	 */
	public Window(Display display, final Engine engine) {
		this.engine = engine;
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.NO_BACKGROUND);
				
		shell.setText("Precision");
		shell.setSize(SIZE, SIZE + INFO_SIZE);
		
		GridLayout gl = new GridLayout();
		gl.marginTop = SIZE;
		gl.horizontalSpacing = (SIZE / 2) - 50;
		gl.verticalSpacing = 8;
		gl.numColumns = 2;
		shell.setLayout(gl);
		
		Font f = new Font(display, "Calibri", 14, SWT.BOLD);
		shell.setFont(f);
		
		roundLabel = new Label(shell, SWT.LEFT);		
		roundLabel.setText("Round: 0");
		roundLabel.setFont(f);
		
		scoreLabel = new Label(shell, SWT.LEFT);
		scoreLabel.setText("Score: 0");
		scoreLabel.setFont(f);
		
		timeLabel = new Label(shell, SWT.LEFT);
		timeLabel.setText("Time: 0");
		timeLabel.setFont(f);
		
		missedLabel = new Label(shell, SWT.LEFT);
		missedLabel.setText("Missed: 0");
		missedLabel.setFont(f);
		
		hitsLabel = new Label(shell, SWT.LEFT);
		hitsLabel.setText("Hits: 0");
		hitsLabel.setFont(f);
		
		overdueLabel = new Label(shell, SWT.LEFT);
		overdueLabel.setText("Missed: 0");
		overdueLabel.setFont(f);
		
		accuracyLabel = new Label(shell, SWT.LEFT);
		accuracyLabel.setText("Accuracy: 0");
		accuracyLabel.setFont(f);
		
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
				
				Image bufferImage = new Image(shell.getDisplay(), shell.getBounds());
				GC bufferGC = new GC(bufferImage);
				
				// background = black
				Color black = new Color(e.display, 0, 0, 0);
				bufferGC.setBackground(black);
				bufferGC.fillRectangle(e.gc.getClipping());
				
				// net of vertical & horizontal gray lines
				Color gray = new Color(e.display, 128, 128, 128);
				bufferGC.setForeground(gray);
				for (int i = 0; i <= SIZE / LINE_DISTANCE; i++) {
					bufferGC.drawLine(i * LINE_DISTANCE, 0, i * LINE_DISTANCE, SIZE);
					bufferGC.drawLine(0, i * LINE_DISTANCE, SIZE, i * LINE_DISTANCE);
				}
				
				// info background = white
				Color white = new Color(e.display, 255, 255, 255);
				bufferGC.setBackground(white);
				bufferGC.fillRectangle(0, SIZE, SIZE, INFO_SIZE);		
				
				bufferGC.setLineWidth(5);
				bufferGC.drawRectangle(0, SIZE, SIZE, INFO_SIZE);
							
				black.dispose();
				gray.dispose();
				white.dispose();
				engine.draw(bufferGC, e.display);
				
				// play double buffer
				e.gc.drawImage(bufferImage, 0, 0);
				bufferGC.dispose();
				bufferImage.dispose();
							
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

	/**
	 * Repaints the window. Can be called from any thread.
	 */
	public void repaint() {
		if (shell != null) {
			shell.getDisplay().syncExec(new Runnable() {			
				@Override
				public void run() {
					shell.redraw();			
				}
			});
			shell.getDisplay().wake();
		}
	}
	
}
