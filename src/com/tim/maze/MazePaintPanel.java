package com.tim.maze;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/* Creator: Tim Xu
 * Name: MazePaintPanel类
 * Function: 迷宫绘制的具体方法 包括迷宫的合理性和MST
 * Create by: 04/22/2016
 */
public class MazePaintPanel extends JPanel {

	public final int paintWidth = 500;
	public final int paintHeight = 500;
	public static final int MAX_Directions = 4;

//	//通过定义enum类 绑定ten=50,twenty=20,fifty=10,none=0, 下面同理
//	public enum MazeSize {
//		ten(50), twenty(20), fifty(10), none(0);
//
//		private int size;
//
//		private MazeSize(int s) {
//			size = s;
//		}
//	};
	
	//上0,右1,下2,左3
	public enum MazeDirection{
		up(0),right(1),down(2),left(3);
		
		private int num;
		
		private MazeDirection(int n) {
			num=n;
		}
		public int getNum()
		{
			return num;
		}
		public static MazeDirection changeToDirection(int i)
		{
			switch(i)
			{
				case 0:
					return up;
				case 1:
					return right;
				case 2:
					return down;
				case 3: default:
					return left;
			}
		}
		
		public static int moveX(MazeDirection m)
		{
			switch(m)
			{
			case left:
				return -1;
			case right:
				return 1;
			default:
				return 0;
			}
		}
		
		public static int moveY(MazeDirection m)
		{
			switch(m)
			{
			case up:
				return -1;
			case down:
				return 1;
			default:
				return 0;
			}
		}
	}

	private int max_birds; //有效范围值
	private int cellSize;  //单元格长宽用于绘画
	private MazeGird[][] mazeArray; //矩阵
	private int personX;      //玩家X
	private int personY;      //玩家Y
	private int visited;
	/*private ImageIcon personImg;   //玩家图标
	private String person_img_name;  //玩家图标图片名字*/
	private LinkedList<Point2D> prohibitList;  //禁止列表
	private ImageIcon prohibitImg;    //禁止图标
	private String prohibit_img_name;  //禁止图标图片名字
	private LinkedList<Point2D> pathPointList; //路线
	
	private UnionFindTree set; //
	private int startX;
	private int startY;
	private MazeDirection startWALL;
	private int endX;
	private int endY;
	private MazeDirection endWALL;

	//初始化迷宫面板
	public MazePaintPanel() {
		this.setPreferredSize(new Dimension(paintWidth, paintHeight));
		this.setBackground(Color.white);
		
		personX=personY=0;
		/*person_img_name=new String("res/蓝兔子.png");
		personImg=new ImageIcon(person_img_name);*/
		prohibitList=new LinkedList<Point2D>();
		prohibit_img_name=new String("res/禁止通行.png");
		prohibitImg=new ImageIcon(prohibit_img_name);
		
		pathPointList=new LinkedList<Point2D>();
		
		setMazeSize(0);
	}
	
	public int getStartX()
	{
		return startX;
	}
	
	public int getStartY()
	{
		return startY;
	}
	
	public MazeDirection getStartD()
	{
		return startWALL;
	}
	
	public int getEndX()
	{
		return endX;
	}
	
	public int getEndY()
	{
		return endY;
	}
	
	public MazeDirection getEndD()
	{
		return endWALL;
	}
	
	 
	//初始化起点
	public void setPerson(int x,int y)
	{
		personX=x;
		personY=y;
		repaint();
	}
	
	//设置路线
	public void addPathPoint(Point2D p)
	{
		pathPointList.add(p);
		repaint();
	}
	
	//删除错误路线
	public void deleteLastPath()
	{
		pathPointList.removeLast();
		repaint();
	}
	
	
	//增加禁止点
	public void addProhibitPoint(Point2D p)
	{
		prohibitList.add(p);
		repaint();
	}
	
	//对于某一个点 判断d方向是否可行
	public boolean canRun(int x,int y,MazeDirection d)
	{
		return mazeArray[y][x].wall[d.num] 
				&& mazeArray[y+MazeDirection.moveY(d)][x+MazeDirection.moveX(d)].getAvailable();
	}
	

	//反序列化 等价于setMazeSize
	public void unserialize(String maze, int size)
    {
		String[] mazeArr = maze.split("/");
		int k = 0;
		cellSize=size;
		clearList();   //删除路线
		if(size!=0)
		{
			max_birds = paintWidth / size;

			mazeArray = new MazeGird[max_birds][];
			
			for (int i = 0; i < max_birds; i++) 
			{
				mazeArray[i] = new MazeGird[max_birds];
				for (int j = 0; j < max_birds; j++) 
				{
					mazeArray[i][j] = new MazeGird();
					for (int s = 0; s < mazeArr[k].length(); s++){
						switch(mazeArr[k].charAt(s))
						{
							case '0':
								mazeArray[i][j].wall[0] = true;
								break;
							case '1':
								mazeArray[i][j].wall[1] = true;
								break;
							case '2':
								mazeArray[i][j].wall[2] = true;
								break;
							case '3':
								mazeArray[i][j].wall[3] = true;
								break;
							case 'S':
								startY = i;
								startX = j;
								break;
							case 'T':
								endY = i;
								endX = j;
								break;
						}
					}
					k++;
				}
			}
			mazeArray[0][0].setAvailable(false);

			for (int i = 0; i < max_birds; i++) {
				mazeArray[max_birds - 1][i].setAvailable(false);
				mazeArray[0][i].setAvailable(false);
				mazeArray[i][0].setAvailable(false);
				mazeArray[i][max_birds - 1].setAvailable(false);
			}

			personX=startX;
			personY=startY;
		}
		else{
			max_birds=1;
			mazeArray=new MazeGird[1][];
			mazeArray[0]=new MazeGird[1];
			mazeArray[0][0]=new MazeGird();
			for(int i=0;i<4;i++)
				mazeArray[0][0].wall[i]=true;
			personX=personY=0;
		}
		repaint();
    	
    }

	//对于某个迷宫中的像素点 设置可行
	public void setAvailable(int x,int y)
	{
		mazeArray[y][x].setAvailable(false);
	}
	
	public void setVisited(int x, int y)
	{
		mazeArray[y][x].setVisited(true);
	}
	
	//访问某个迷宫中的像素点 是否可行
	public boolean getAvailable(int x,int y)
	{
		return mazeArray[y][x].getAvailable();
	}

	@Override
	protected void paintComponent(Graphics g) {
		//统一绘制迷宫
		super.paintComponent(g);
		paintStartNEnd((Graphics2D)g);
		paintMaze((Graphics2D)g);	
		paintProhibit((Graphics2D)g);
		paintPath((Graphics2D)g);
		paintPerson((Graphics2D)g);
	}
	
	//初始化新迷宫
	public void setMazeSize(int size) {

		cellSize=size;
		clearList();   //删除路线
		if(size!=0)
		{
			max_birds = paintWidth / size;

			mazeArray = new MazeGird[max_birds][];
			for (int i = 0; i < max_birds; i++) {
				mazeArray[i] = new MazeGird[max_birds];
				for (int j = 0; j < max_birds; j++) {
					mazeArray[i][j] = new MazeGird();
				}
			}
			mazeArray[0][0].setAvailable(false);

			for (int i = 0; i < max_birds; i++) {
				mazeArray[max_birds - 1][i].setAvailable(false);
				mazeArray[0][i].setAvailable(false);
				mazeArray[i][0].setAvailable(false);
				mazeArray[i][max_birds - 1].setAvailable(false);
			}

			setMazeArray(mazeArray, max_birds);
			personX=startX;
			personY=startY;
		}
		else{
			max_birds=1;
			mazeArray=new MazeGird[1][];
			mazeArray[0]=new MazeGird[1];
			mazeArray[0][0]=new MazeGird();
			for(int i=0;i<4;i++)
				mazeArray[0][0].wall[i]=true;
			personX=personY=0;
		}
		repaint();
	}
	
	
	
	//标示起点和终点
	private void paintStartNEnd(Graphics2D g) {
		if(cellSize==0) return;
		if((startX<0 && startY<0) || (endX<0 && endY<0)) return;
		g.setColor(Color.GREEN);
		g.fillRect(startX*cellSize, startY*cellSize, cellSize, cellSize);
		g.setColor(Color.RED);
		g.fillRect(endX*cellSize, endY*cellSize, cellSize, cellSize);
	}

	//遍历矩阵有效位置(1 - max-1),绘制迷宫
	private void paintMaze(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(3.0f));
		for (int i = 1; i < max_birds - 1; i++)
			for (int j = 1; j < max_birds - 1; j++) {
				if (mazeArray[i][j].wall[0] == false)
					g.drawLine(j * cellSize, i * cellSize, j * cellSize +cellSize, i * cellSize);
				if (mazeArray[i][j].wall[1] == false)
					g.drawLine(j * cellSize + cellSize, i *cellSize, j * cellSize + cellSize, i * cellSize + cellSize);
				if (mazeArray[i][j].wall[2] == false)
					g.drawLine(j * cellSize, i * cellSize + cellSize, j * cellSize + cellSize, i * cellSize + cellSize);
				if (mazeArray[i][j].wall[3] == false)
					g.drawLine(j * cellSize, i * cellSize, j * cellSize, i * cellSize + cellSize);
				
			}

	}
	
	public int getScore() {
		int score = 0;
		
		for (int i = 1; i < max_birds - 1; i++)
			for (int j = 1; j < max_birds - 1; j++)
				if (mazeArray[i][j].getVisited() == true)
					score ++;
		return score;
	}
	
	//重新加载玩家位置
	private void paintPerson(Graphics2D g)
	{
		if(cellSize==0) return;
		if(personX<0 && personY<0) return;
		g.setColor(Color.BLUE);
		g.fillOval(personX*cellSize,personY*cellSize, cellSize, cellSize);
		/*Image img=personImg.getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
		personImg.setImage(img);
		g.drawImage(personImg.getImage(),personX*cellSize,personY*cellSize,null);
		personImg=new ImageIcon(person_img_name);*/
		
	}
	
	//加载禁止图标־
	private void paintProhibit(Graphics2D g)
	{
		if(cellSize==0) return;
		Image img=prohibitImg.getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
		prohibitImg.setImage(img);
		for (Point2D p : prohibitList) {
			g.drawImage(prohibitImg.getImage(),(int)p.getX()*cellSize,(int)p.getY()*cellSize,null);
		}
		prohibitImg=new ImageIcon(prohibit_img_name);	
	}
	
	//绘制路径
	private void paintPath(Graphics2D g)
	{
		if(cellSize==0) return;
		int n = pathPointList.size();
		Point2D a,b;
		g.setPaint(Color.GREEN);
		g.setStroke(new BasicStroke(5.0f));
		for(int i=0; i<n-1; i++)
		{
			a = pathPointList.get(i);
			b = pathPointList.get(i+1);
			g.drawLine((int)a.getX()*cellSize+cellSize/2, (int)a.getY()*cellSize+cellSize/2,
					(int)b.getX()*cellSize+cellSize/2, (int)b.getY()*cellSize+cellSize/2);
		}
	}
	
	//删除路线记录
	public void clearList()
	{
		prohibitList.clear();
		pathPointList.clear();
	}
	
	
	//设置迷宫数组
	private void setMazeArray(MazeGird[][] maze, int size) {
		
		set = new UnionFindTree((size - 2) * (size - 2));
		//初始化树
		setSE(maze, size, 0);
		setSE(maze, size, 1);
		
		//内部筑墙
		int rx, ry, rfy, rfx, ru; 
		while (set.getCount() > 1) {
			do {
				rx = (int) (Math.random() * (size - 2) + 1);
				ry = (int) (Math.random() * (size - 2) + 1);
				ru = (int) (Math.random() * 4);

				if (ru == 0 && ry > 1) {
					rfx = 0;
					rfy = -1;
				} else if (ru == 1 && rx < size - 2) {
					rfx = 1;
					rfy = 0;
				} else if (ru == 2 && ry < size - 2) {
					rfx = 0;
					rfy = 1;
				} else if (ru == 3 && rx > 1) {
					rfx = -1;
					rfy = 0;
				} else
					rfx = rfy = 0;
			} while (set.find((ry - 1) * (size - 2) + rx - 1) == set.find((ry - 1 + rfy) * (size - 2) + rx + rfx - 1));
			maze[ry][rx].wall[ru] = true;
			maze[ry + rfy][rx + rfx].wall[(ru + 2) % 4] = true;

			set.union((ry - 1) * (size - 2) + rx - 1, (ry - 1 + rfy) * (size - 2) + rx + rfx - 1);
		}
	}
	
	//构建外墙 并且设置入口出口
	private void setSE(MazeGird [][] maze,int size,int SE)
	{
		int pointX,pointY,line,ran;
		pointX=pointY=0;
		line = (int)(Math.random()*4.0);
		ran = (int)(Math.random()*(size-2)+1);
		switch (line) {
			case 0:
				pointX=ran;
				pointY=1;
				break;
			case 1:
				pointX=size-2;
				pointY=ran;
				break;
			case 2:
				pointX=ran;
				pointY=size-2;
				break;
			case 3:
				pointX=1;
				pointY=ran;
				break;
		};
		maze[pointY][pointX].wall[line]=true;
		if(SE==0)  
		{
			startX=pointX;startY=pointY;startWALL=MazeDirection.changeToDirection(line);
		}else
		{
			endX=pointX;endY=pointY;endWALL=MazeDirection.changeToDirection(line);
		}
	}
}

/* Name: MazeGrid类
 * Function: 迷宫矩阵，对于每一个像素的点都有4个方向是否联通来表示 默认是false不联通
 * Create by: 04/22/2016
 */
class MazeGird {
	public boolean[] wall; //4方向是否为墙
	private boolean available;
	private boolean visited;
	//初始化 默认有墙
	public MazeGird() { 
		wall = new boolean[4];
		for (boolean b : wall) {
			b = false;
		}
		available = true;
		visited = false;
	}

	public void setAvailable(boolean b) {
		available = b;
	}

	public boolean getAvailable() {
		return available;
	}
	
	public void setVisited(boolean b) {
		visited = b;
	}

	public boolean getVisited() {
		return visited;
	}

}

/* Creator: Tim Xu
 * Name: UnionFindTree类
 * Function: 树类，用于发现最小生成树和形成迷宫路径
 * Create by: 04/22/2016
 */
class UnionFindTree {
	private int[] node; //节点值
	private int[] size; //
	private int count;
	
	public UnionFindTree(int nodes) {
		node = new int[nodes];
		size = new int[nodes];
		for (int i = 0; i < nodes; i++) {
			node[i] = i;
			size[i] = 1;
		}
		count = nodes;
	}

	public int getCount() {
		return count;
	}

	public int find(int num) {
		int temp;
		while (node[num] != num) {
			temp = num;
			num = node[num];
			node[temp] = node[node[temp]];
		}
		return num;
	}

	public void union(int a, int b) {
		int rootA = find(a);
		int rootB = find(b);
		if (rootA == rootB)
			return;
		if (size[rootA] < size[rootB]) {
			size[rootB] += size[rootA];
			node[rootA] = rootB;
		} else {
			size[rootA] += size[rootB];
			node[rootB] = rootA;
		}
		count--;
	}
}
