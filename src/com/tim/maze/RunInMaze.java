package com.tim.maze;

import java.awt.geom.Point2D;
import java.util.Stack;

import com.tim.maze.MazePaintPanel.MazeDirection;

public class RunInMaze {
	
	private int locX;
	private int locY;
	private int endX;
	private int endY;
	private MazePaintPanel relatedMaze;
	private Stack<Point2D> pathStack;
	private Stack<MazeDirection> directionStack;
	
	public RunInMaze(MazePaintPanel maze) {
		relatedMaze = maze;
		locX=relatedMaze.getStartX();
		locY=relatedMaze.getStartY();
		endX=relatedMaze.getEndX();
		endY=relatedMaze.getEndY();
		pathStack=new Stack<Point2D>();
		directionStack=new Stack<MazePaintPanel.MazeDirection>();
	}
	
	public void runAgain()
	{
		locX=relatedMaze.getStartX();
		locY=relatedMaze.getStartY();
		endX=relatedMaze.getEndX();
		endY=relatedMaze.getEndY();
		relatedMaze.setPerson(locX, locY);
		pathStack.clear();
		pathStack.clear();
		relatedMaze.clearList();
	}
	
	public void runByComputer()
	{
		int line=0;
		MazeDirection dire;
		Point2D temp;
		while(locX!=endX || locY!=endY)
		{
			if( line<MazePaintPanel.MAX_Directions )
			{
				dire=MazeDirection.changeToDirection(line);
				if(relatedMaze.canRun(locX, locY,dire)==true)
				{
					relatedMaze.addPathPoint(new Point2D.Float((float)locX,(float)locY));
					relatedMaze.setAvailable(locX, locY);
					pathStack.push(new Point2D.Float((float)locX, (float)locY));
					directionStack.push(dire);
					locX= locX + MazeDirection.moveX(dire);
					locY= locY + MazeDirection.moveY(dire);
					relatedMaze.setPerson(locX, locY);
					line = 0;
				}
				else{
					line++;
				}
			}
			else
			{
				
				relatedMaze.deleteLastPath();
				relatedMaze.addProhibitPoint(new Point2D.Float(locX, locY));
				temp=pathStack.pop();
				dire=directionStack.pop();
				locX = (int)temp.getX();
				locY = (int)temp.getY();
				relatedMaze.setPerson(locX, locY);
				line = dire.getNum();
				line++;
			}
		}
		relatedMaze.addPathPoint(new Point2D.Float((float)locX,(float)locY));
		relatedMaze.setPerson(locX+MazeDirection.moveX(relatedMaze.getEndD()),
				locY+MazeDirection.moveY(relatedMaze.getEndD()));
	}
	

	public boolean runByMan(MazeDirection dire)
	{
		relatedMaze.addPathPoint(new Point2D.Float((float)locX,(float)locY));
		if(relatedMaze.canRun(locX, locY, dire)==true)
		{
			relatedMaze.setVisited(locX, locY);
			locX= locX + MazeDirection.moveX(dire);
			locY= locY + MazeDirection.moveY(dire);
			relatedMaze.addPathPoint(new Point2D.Float((float)locX,(float)locY));
			relatedMaze.setPerson(locX, locY);
		}
	
		if (locX==endX && locY==endY) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public int getScore() {
		return relatedMaze.getScore();
	}

}
