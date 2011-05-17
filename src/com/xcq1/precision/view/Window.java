package com.xcq1.precision.view;

import java.util.Observable;
import java.util.Observer;

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
public class Window implements Observer {
	
	/**
	 * width & height of the window in pixels
	 */
	public final static int SIZE = 600;
	public final static int INFO_SIZE = 160;
	
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
		this.engine.addObserver(this);
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.NO_BACKGROUND);
				
		shell.setText("Precision");
		shell.setSize(SIZE, SIZE + INFO_SIZE);
			
		Font f = new Font(display, "Calibri", 14, SWT.BOLD);
		shell.setFont(f);
		
		final int COLUMN_WIDTH = (SIZE - 20) / 2;
		
		roundLabel = setupLabel("Round: 0", f);
		scoreLabel = setupLabel("Score: 0", f);
		timeLabel = setupLabel("Time: 0", f);
		missedLabel = setupLabel("Missed: 0", f);
		hitsLabel = setupLabel("Hits: 0", f);
		overdueLabel = setupLabel("Overdue: 0", f);
		accuracyLabel = setupLabel("Accuracy: 0 %", f);
		
		placeLabel(roundLabel, 10, SIZE + 10, COLUMN_WIDTH, 20);
		placeLabel(scoreLabel, 10, SIZE + 40, COLUMN_WIDTH, 20);
		placeLabel(timeLabel, 10, SIZE + 70, COLUMN_WIDTH, 20);
		placeLabel(missedLabel, 10, SIZE + 100, COLUMN_WIDTH, 20);
		placeLabel(hitsLabel, 10 + COLUMN_WIDTH, SIZE + 10, COLUMN_WIDTH, 20);
		placeLabel(overdueLabel, 10 + COLUMN_WIDTH, SIZE + 40, COLUMN_WIDTH, 20);
		placeLabel(accuracyLabel, 10 + COLUMN_WIDTH, SIZE + 70, COLUMN_WIDTH, 20);
		
		center();
		
		shell.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				engine.clicked(e.x, e.y);
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
	 * Sets up a Label with the desired caption and font.
	 * @param caption
	 * @param f
	 * @return
	 */
	protected Label setupLabel(String caption, Font f) {
		Label result = new Label(shell, SWT.LEFT);
		result.setText(caption);
		result.setFont(f);
		return result;
	}
	
	protected void placeLabel(Label l, int x, int y, int width, int height) {
		l.setBounds(x, y, width, height);
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

	/**
	 * All information flow from the engine to the window
	 * must occur via the Observer pattern.
	 */
	@Override
	public void update(Observable arg0, Object arg1) {	
		shell.getDisplay().syncExec(new Runnable() {
			
			@Override
			public void run() {
				long round = 1 + (engine.getRoundTime() / 15000);
				int score = engine.getCredits();
				long time = (engine.getRoundTime() / 1000);
				int missed = engine.getMisses();
				int hits = engine.getShots() - engine.getMisses();
				int overdue = engine.getOverdue();
				float accuracy = 100f * hits / (float) engine.getShots();
				
				roundLabel.setText("Round: " + round);
				scoreLabel.setText("Score: " + score);
				timeLabel.setText("Time: " + time);
				missedLabel.setText("Missed: " + missed);
				hitsLabel.setText("Hits: " + hits);
				overdueLabel.setText("Overdue: " + overdue);
				accuracyLabel.setText("Accuracy: " + accuracy);				
			}
		});
	}
	
}
