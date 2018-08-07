/**
 * Final Project for 2326
 *  by Nami Akazawa, Eduardo Gonzalez
 * FIX: Need to run concurrently using Timer
 * 		Make it re-sizable
 * 		Have some presets
 */
import java.awt.EventQueue;
import java.util.Random;
import java.util.Scanner;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToolBar;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;

import java.util.Timer;
import java.util.TimerTask;
//import javax.swing.Timer;

//import java.awt.Image;
//import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseMotionAdapter;



public class GOLW {

	private JFrame frame;
	private final JPanel panel1 = new JPanel();
	private final JButton btnReset = new JButton("Reset");

	final int w = 70; //width
	final int h = 70; //height
	boolean[][] currMove = new boolean[h][w]; //Rows Cols, y - x
	boolean[][] nextMove = new boolean[h][w];  //True = live, False = dead
	boolean play; //For the timer
	
	BufferedImage offScreenImg; //Double buffer
	Graphics2D offScreenGrp;
	Random random = new Random();
	
	
	/**
	 * Launch the application.
	 * Main function
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GOLW window = new GOLW();
					//make it visible on the screen
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GOLW() {
		initialize();
		offScreenImg = new BufferedImage(panel1.getWidth(), panel1.getHeight(), BufferedImage.TYPE_INT_RGB);
		offScreenGrp = offScreenImg.createGraphics();
		
//		refreshScreen();
		Timer time = new Timer();
		TimerTask task = new TimerTask() {
			//The run method contains the code that performs the task.
			public void run() {
				if(play) { //If the game is currently playing
					for(int i=0; i< h; i++) {
						for(int j=0; j< w; j++) {
							//Look cell to determine cell lives or not
							nextMove[i][j] = CellDecision(i,j);;
						}
					}
					for(int i=0; i< h; i++) {
						for(int j=0; j< w; j++) {
							//Look cell to determine cell lives or not
							currMove[i][j] = nextMove[i][j];
						}
					}
				}
			}
		};
		//used to schedule the specified task for repeated fixed-rate execution, beginning after the specified delay.
//			time.scheduleAtFixedRate(task, 0, 10);
			time.schedule(task, 0,15);
			
		
		/*
		 * Mouse Dragged Event Handler
		 */
		panel1.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int i = e.getX() * w / panel1.getWidth() ;
				int j = e.getY() * h / panel1.getHeight();
				if(SwingUtilities.isLeftMouseButton(e)) {
					currMove[j][i] = true;
				}
				else{
					currMove[j][i] = false;
				}
				redraw();
			}
		});
		panel1.paint(offScreenGrp);
////		redraw();
	}

//	private void refreshScreen() {
//		Timer timer = new Timer(0, new ActionListener() {
//		      @Override
//		      public void actionPerformed(ActionEvent e) {
//		        redraw();
//		      }
//		    });
//		    timer.setRepeats(true);
//
//		    timer.setDelay(57);
//		    timer.start();
//	}

	/**
	 * Check all the eight directions and see how many live cells in the neighbor
	 * Will return True if cell lives or born
	 * Will return False if cell dies 
	 */
	private boolean CellDecision(int row, int col) {
		int neighbors = 0;
		//Look North-West
		if((row-1 >= 0) && (col-1 >= 0)) {
			neighbors = currMove[row-1][col-1] ? neighbors+1: neighbors;
		}
		//West
		if((row >= 0)&&(col -1>= 0)) {
			neighbors = currMove[row][col-1] ? neighbors+1: neighbors;
		}
		//South-West
		if((row+1 < h) &&(col -1 >= 0)) {
			neighbors = currMove[row+1][col-1] ? neighbors+1: neighbors;
		}
		//South
		if((row+1 < h)&& (col < w)) {
			neighbors = currMove[row+1][col] ? neighbors+1: neighbors;
		}
		//South-East
		if((row+1 < h) && (col+1< w)){
			neighbors = currMove[row+1][col+1] ? neighbors+1: neighbors;
		}
		//East
		if((row < h)&& (col+1<w)) {
			neighbors = currMove[row][col+1] ? neighbors+1: neighbors;
		}
		//North-East
		if((row-1 >=0 ) && (col+1 < w)) {
			neighbors = currMove[row-1][col+1] ? neighbors+1: neighbors;
		}
		//North
		if((row-1 >=0) && (col < w)){
			neighbors = currMove[row-1][col] ? neighbors+1: neighbors;
		}
		
		if(neighbors == 3) return true; //Cell born
		if(currMove[row][col] &&  neighbors == 2) return true; //Cell lives
		
			return false; //Else, Under-populated or Over-populated, so Cell dies
	}

	/**
	 * Re-drawing the panel
	 * Check the board and see if one cell is True, then fill it with red color
	 */
	private void redraw() {
		
		offScreenGrp.setColor(panel1.getBackground());
		offScreenGrp.fillRect(0, 0, panel1.getWidth(), panel1.getHeight());
		int x,y;
		for(int i = 0; i< h; i=i+1) {
			for(int j=0; j < w; j=j+1) {
				//If the cell is alive
				if(currMove[i][j]) {
					//Color picked randomly
					offScreenGrp.setColor(new Color(random.nextInt()));
//					offScreenGrp.setColor(Color.RED);
					y = (i * panel1.getHeight()) / h;
					x = (j * panel1.getWidth()) / w;
					offScreenGrp.fillRect(x, y, panel1.getWidth()/w, panel1.getHeight()/h);
				}
			}
		}
		//Drawing the grids
		drawLines();
		
		panel1.getGraphics().drawImage(offScreenImg, 0, 0, panel1);
	}
	
	/**
	 * Draw the lines for the board
	 */
	private void drawLines() {
		//Color of lines are black 
		offScreenGrp.setColor(Color.BLACK);
		
		//Horizontal 
		int x,y;
		for(int i = 1; i < h; i= i+1) {
			y = (i * panel1.getHeight()) / h;
			offScreenGrp.drawLine(0, y, panel1.getWidth(), y) ;
		}
		//Vertical line
		for(int j = 1; j < w; j= j+1) {					
			x = (j * panel1.getWidth()) / w;
			offScreenGrp.drawLine(x, 0, x, panel1.getHeight());
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(0, 191, 255));
		//Size of the window
		frame.setBounds(100, 100, 450, 347);
		//Close the window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Window Title
		frame.setTitle("Game Of Life Simulation");
		//Set the Window to the Center of the Screen
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		
		JButton btnStart = new JButton("Start");
		btnStart.setToolTipText("Start the game");
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//turn on/off the play button 
				play = !play;
				if(play) btnStart.setText("Pause");
				else btnStart.setText("Play");
				redraw();
			}
		});
		btnStart.setBounds(6, 290, 75, 29);
		frame.getContentPane().add(btnStart);
		
		/*
		 * Reset Button Clicked
		 */
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currMove = new boolean[h][w];
				btnStart.setText("Start"); //Set back the button to Start 
				play = true;
				redraw();
			}
		});
		btnReset.setBounds(349, 290, 87, 29);
		btnReset.setToolTipText("Reset the game");
		frame.getContentPane().add(btnReset);
		
		/*
		 * So that every time redrawing the screen it looks naturally dynamic
		 */
		panel1.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				offScreenImg = new BufferedImage(panel1.getWidth(), panel1.getHeight(), BufferedImage.TYPE_INT_RGB);
				offScreenGrp = offScreenImg.createGraphics();
				panel1.paint(offScreenGrp);
//				redraw();
			}
		});
		
		panel1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int i = e.getX() * w / panel1.getWidth() ;
				int j = e.getY() * h / panel1.getHeight();
				currMove[j][i] = !currMove[j][i];
				redraw();
			}
		});
		
		
		panel1.setBackground(new Color(224, 255, 255));
		panel1.setBounds(16, 5, 420, 283);
		frame.getContentPane().add(panel1);
		panel1.setLayout(new GridLayout(1, 0, 0, 0));
	}
}
