package com.tim.maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.ImageIcon;

import com.tim.maze.MazePaintPanel.MazeDirection;

//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JTextField;


/**
 * A server for a network multi-player tic tac toe game.  Modified and
 * extended from the class presented in Deitel and Deitel "Java How to
 * Program" book.  I made a bunch of enhancements and rewrote large sections
 * of the code.  The main change is instead of passing *data* between the
 * client and server, I made a TTTP (tic tac toe protocol) which is totally
 * plain text, so you can test the game with Telnet (always a good idea.)
 * The strings that are sent in TTTP are:
 *
 *  Client -> Server           Server -> Client
 *  ----------------           ----------------
 *  MOVE <n>  (0 <= n <= 8)    WELCOME <char>  (char in {X, O})
 *  QUIT                       VALID_MOVE
 *                             OTHER_PLAYER_MOVED <n>
 *                             VICTORY
 *                             DEFEAT
 *                             TIE
 *                             MESSAGE <text>
 *
 * A second change is that it allows an unlimited number of pairs of
 * players to play.
 */
public class MazeServer {

    public static void main(String[] args) throws Exception {
    	ServerSocket listener = new ServerSocket(4440);
        System.out.println("Maze Server is Running");
        try {
            while (true) {
                GameMaster game = new GameMaster();
                GameMaster.Player player1 = game.new Player(listener.accept(), 1);
                System.out.println("Player 1 is coming");
                GameMaster.Player player2 = game.new Player(listener.accept(), 2);
                System.out.println("Player 2 is coming");
                player1.setOpponent(player2);
                player2.setOpponent(player1);
                player1.start();
                player2.start();
            }
        } finally {
            listener.close();
        }
    }
}

/**
 * A two-player game.
 */
class GameMaster {
	
    int readyCheck = 0;
    //Player currentPlayer;

    /**
     * Returns whether the current state of the board is such that one
     * of the players is a winner.
     */
    public boolean Judge() {
        return true;
    }
    
    public int readyCheck(int flag) {
    	readyCheck ++;
    	return readyCheck;
    }

    /**
     * Returns whether there are no more empty squares.
     
    public boolean boardFilledUp() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Called by the player threads when a player tries to make a
     * move.  This method checks to see if the move is legal: that
     * is, the player requesting the move must be the current player
     * and the square in which she is trying to move must not already
     * be occupied.  If the move is legal the game state is updated
     * (the square is set and the next player becomes current) and
     * the other player is notified of the move so it can update its
     * client.
     
    public synchronized boolean legalMove(int location, Player player) {
        if (player == currentPlayer && board[location] == null) {
            board[location] = currentPlayer;
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerMoved(location);
            return true;
        }
        return false;
    }*/

    /**
     * The class for the helper threads in this multithreaded server
     * application.  A Player is identified by a character mark
     * which is either 'X' or 'O'.  For communication with the
     * client the player has a socket with its input and output
     * streams.  Since only text is being communicated we use a
     * reader and a writer.
     */
    class Player extends Thread {
        int num;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;
    	private MazeCreator paintPanel;
    	
    	int time;
        int diff;
        boolean ready;
        
        int step;
        int endtime;
        boolean finished;

        /**
         * Constructs a handler thread for a given socket and mark
         * initializes the stream fields, displays the first two
         * welcoming messages.
         */
        public Player(Socket socket, int num) {
            this.socket = socket;
            this.num = num;
            ready = false;
            finished = false;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("WELCOME " + num);
                output.println("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
                System.out.println("Player died: " + e + num);
            }
        }

        /**
         * Accepts notification of who the opponent is.
         */
        public void setOpponent(Player opponent) {
            this.opponent = opponent;
//            System.out.println("test1");
//            output.println("MESSAFE Your opponent is coming.");
        }

//        /**
//         * Handles the otherPlayerMoved message.
//         */
//        public void otherPlayerMoved(int location) {
//            output.println("OPPONENT_MOVED " + location);
//            output.println(
//                hasWinner() ? "DEFEAT" : boardFilledUp() ? "TIE" : "");
//        }

    	/**
    	 * 通信方式， 客户端与服务端信息交互格式如下
    	 *
    	 *  Client -> Server				Server -> Client
    	 *  ----------------				----------------          
    	 *  DIFF + mark + (012)				WELCOME + (12)
    	 *  TIME + mark + (X)				MESSAGE + (String)
    	 *  READY + mark					TIME + X
    	 *  STEP + mark + (X)   			DIFF + (50,20,10,0)
    	 *  ENDT + mark + (X)          		START
    	 *  ENDS + mark						RESULT + (String)
    	 *	                           
    	 */
        public void run() {
            try {
                // The thread is only started after everyone connects.
                output.println("MESSAGE All players connected");
                
                // Repeatedly get commands from the client and process them.
                while (true) {
                    String command = input.readLine();
                    System.out.println(command);
                    if (command.startsWith("DIFF")) {
                        diff = command.charAt(5) - 48;//"DIFF 1(2) 0(12)"
                    } else if (command.startsWith("TIME")) {
                        time = command.charAt(5) - 48;//"TIME 1(2) X"
                    } else if (command.startsWith("READY")) {
                        ready = true;
                        if (opponent.ready == true){
                        	Player selected;
                        	//随机选择1名玩家的设置
                        	if (zero1random() == num)
                        		selected = this;
                        	else selected = this.opponent;
                        	//给各个玩家发送设置
                        	
                        	sendTogether("TIME"+selected.time);
                        	
                        	//发出开始指令
                        	paintPanel = new MazeCreator();
                        	switch(selected.diff)
                    		{
                    			case 0:
                    				paintPanel.setMazeSizeForServer(50);
                    				sendTogether("DIFF"+50);
                    				break;
                    			case 1:
                    				paintPanel.setMazeSizeForServer(20);
                    				sendTogether("DIFF"+20);
                    				break;
                    			case 2:
                    				paintPanel.setMazeSizeForServer(10);
                    				sendTogether("DIFF"+10);
                    				break;
                    			default:
                    				paintPanel.setMazeSizeForServer(0);
                    				sendTogether("DIFF"+0);
                    				break;
                    		}
                        	String mazeStr = paintPanel.serialize();
                        	System.out.println(mazeStr);
                        	sendTogether("MAZE"+mazeStr);
                        	
                        	ready = false;
                        	opponent.ready = false;
                        	sendTogether("START");
                        }
                    } else if (command.startsWith("STEP")) {
                    	step = Integer.parseInt(command.substring(5));
                    } else if (command.startsWith("ENDT")) {
                    	endtime = Integer.parseInt(command.substring(5));
                    	if (endtime < 0)
                    		endtime = 65535;
                    } else if (command.startsWith("ENDS")) {
                    	finished = true;
                    	if (opponent.finished == true){
                    		String result = "";
                        	if ((step + endtime) > (opponent.step + opponent.endtime)) {
                        		result = "1.\tPlayer" + opponent.num + "\tStep: "+opponent.step +", Time: "+opponent.time+" Second.\n"
                        				+ "2.\tPlayer" + num + "\tStep: "+step +", Time: "+time+" Second.";
                        	} else {
                        		result = "1.\tPlayer" + num + "\tStep: "+step +", Time: "+time+" Second.\n"
                        				+ "2.\tPlayer" + opponent.num + "\tStep: "+opponent.step +", Time: "+opponent.time+" Second.";
                        	}
                        	finished = false;
                        	opponent.finished = false;
                        	sendTogether("RESULT"+result);
                    	}
                    } else if (command.startsWith("QUIT")) {
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Player died: " + e);
            } finally {
                try {socket.close();} catch (IOException e) {}
            }
        }
        
        public void sendTogether(String command)
        {
        	output.println(command);
        	opponent.output.println(command);
        }
        
        public int zero1random()
        {
        	Random rand = new Random(); 
        	return rand.nextInt(2) + 1;
        }
    }
    
    class MazeCreator {
    	
    	private int max_birds; //有效范围值
    	private MazeGird[][] mazeArray; //矩阵
    	private UnionFindTree set; 
    	private int startX;
    	private int startY;
    	private MazeDirection startWALL;
    	private int endX;
    	private int endY;
    	private MazeDirection endWALL;
    	
    	public MazeCreator() {
    		setMazeSizeForServer(0);
    	}
    	
    	//给服务器端的迷宫生成
    	public void setMazeSizeForServer(int size) {
    		if(size!=0)
    		{
    			max_birds = 500 / size;

    			mazeArray = new MazeGird[max_birds][];
    			for (int i = 0; i < max_birds; i++) {
    				mazeArray[i] = new MazeGird[max_birds];
    				for (int j = 0; j < max_birds; j++) {
    					mazeArray[i][j] = new MazeGird();
    				}
    			}
    			setMazeArray(mazeArray, max_birds);
    		}
    		else{
    			max_birds=1;
    			mazeArray=new MazeGird[1][];
    			mazeArray[0]=new MazeGird[1];
    			mazeArray[0][0]=new MazeGird();
    			for(int i=0;i<4;i++)
    				mazeArray[0][0].wall[i]=true;
    		}
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
    	
    	//序列化迷宫
    	public String serialize()
    	{
    		String strMaze = "";
    		for (int i = 0; i < max_birds; i++) 
    			for (int j = 0; j < max_birds; j++) {
    				strMaze+="W";
    				if (mazeArray[i][j].wall[0] == true)
    					strMaze+="0";
    				if (mazeArray[i][j].wall[1] == true)
    					strMaze+="1";
    				if (mazeArray[i][j].wall[2] == true)
    					strMaze+="2";
    				if (mazeArray[i][j].wall[3] == true)
    					strMaze+="3";
    				strMaze+="W";
    				if (i==startY && j==startX)
    					strMaze+="S";
    				if (i==endY && j==endX)
    					strMaze+="T";
    				strMaze+="/";
    			}
    		return strMaze;
    	}
    	
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
    }
}