package com.tim.maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.tim.maze.MazePaintPanel.MazeDirection;

public class MazeShowPanel extends JPanel implements KeyEventDispatcher  {
	
	//private final int mpsX=40;
	//private final int mpsY=35;
	private final int mpsWidth=700;
	private final int mpsHeight=600;
	private FlowLayout showflow;
	private MazePaintPanel paintPanel;
	private RunInMaze run;
	private ControlPanel pInMaze;
	private int score;
	private int costTime;
	private MazeFrame mf;
	
	
	/* Creator: Tim Xu
	 * Name: MazeShowPanel类
	 * Function: 迷宫绘制的入口，同时是迷宫的panel
	 * Create by: 04/22/2016
	 */
	public MazeShowPanel(MazeFrame mf) {
		super();
		
		this.mf = mf;
		this.setPreferredSize(new Dimension(mpsWidth,  mpsHeight));
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2),"MAZE SHOW"));
		this.setBackground(new Color(233, 233, 233));
		
		showflow=new FlowLayout();
		this.setLayout(showflow);
		showflow.setAlignment(FlowLayout.CENTER);
		showflow.setVgap(30);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
		
		paintPanel=new MazePaintPanel();
		this.add(paintPanel);//MazePaintPanel负责初始化迷宫
		
		run=new RunInMaze(paintPanel);//RunInMaze负责生成随机MST
	}
	
	public MazeShowPanel(MazeFrame mf, String maze) {
		super();
		
		this.mf = mf;
		this.setPreferredSize(new Dimension(mpsWidth,  mpsHeight));
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2),"MAZE SHOW"));
		this.setBackground(new Color(233, 233, 233));
		
		showflow=new FlowLayout();
		this.setLayout(showflow);
		showflow.setAlignment(FlowLayout.CENTER);
		showflow.setVgap(30);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
		
		paintPanel=new MazePaintPanel();
		this.add(paintPanel);//MazePaintPanel负责初始化迷宫
		
		run=new RunInMaze(paintPanel);//RunInMaze负责生成随机MST
	}
	
	//随机生成迷宫
	public void setMazeSize(int maze_size)
	{
		paintPanel.setMazeSize(maze_size);
		run.runAgain();
	}
	
	public void setMazeSize(String maze, int maze_size)
	{
		paintPanel.unserialize(maze,maze_size);
		run.runAgain();
	}
	
	public void computerRun()
	{
		run.runByComputer();	
	}
	
	public void getControlPanel(ControlPanel p)
	{
		pInMaze = p;
	}
	
	public void completeGame()
	{
		score = run.getScore();
		mf.finished(score, pInMaze.resetButton());
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if(e.getID() == KeyEvent.KEY_RELEASED)
            return true;
        switch (e.getKeyCode()) {
        case KeyEvent.VK_DOWN: case KeyEvent.VK_S:
            //System.out.println(e.getKeyCode());
        	if (run.runByMan(MazeDirection.down)==true)
        		completeGame();
            break;
        case KeyEvent.VK_UP: case KeyEvent.VK_W:
        	//System.out.println(e.getKeyCode());
        	if (run.runByMan(MazeDirection.up)==true)
        		completeGame();
            break;
        case KeyEvent.VK_RIGHT: case KeyEvent.VK_D:
        	//System.out.println(e.getKeyCode());
        	if (run.runByMan(MazeDirection.right)==true)
        		completeGame();
            break;
        case KeyEvent.VK_LEFT: case KeyEvent.VK_A:
        	//System.out.println(e.getKeyCode());
        	if (run.runByMan(MazeDirection.left)==true)
        		completeGame();
            break;
        }
        
        return true;
	}
	

}
