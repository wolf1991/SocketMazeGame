package com.tim.maze;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class MazeFrame extends JFrame {

	public static int PORT = 4440;
    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;
    
    private int num;
	private final int fWidth=1100;
	private final int fHeight=700;
	private FlowLayout frameflow;
	private ImageIcon icon;
	private BackPanel framePanel;//背景
	private MazeShowPanel mazeShow;//迷宫界面板块
	private ControlPanel control;//按钮板块
	
	public static void main(String[] args) throws Exception {
		/*SocketConn socket = null;
		while (true) 
		{
			Object[] options1 = { "Connect", "Host", "Quit" };

			JPanel panel = new JPanel();
			panel.add(new JLabel("Enter IP Address of a machine that is\n" +
		            "running the date service on port XXXX:"));
			JTextField textField = new JTextField(15);
			panel.add(textField);
			
			int result = JOptionPane.showOptionDialog(null, panel, "Enter a Number",
			        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
			        null, options1, null);
			if (result == JOptionPane.YES_OPTION){
				textField.getText();
			    JOptionPane.showMessageDialog(null, textField.getText());
			} else if (result == JOptionPane.NO_OPTION){
				System.exit(-1);		
		}*/
		String ipAdd = JOptionPane.showInputDialog(
	            null,
	            "Enter IP Address of the Server:",
	            "Welcome to the MazeGame",
	            JOptionPane.QUESTION_MESSAGE);
		//程序的入口
		
		MazeFrame mazeFrame;
		mazeFrame = new MazeFrame(ipAdd);

		mazeFrame.play();
	
	}
	
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
	
	public void play() throws Exception {
		String response;
		int size = 0;
		int time = 0;
		String mazeStr = "";
        try {
            response = in.readLine();
            if (response.startsWith("WELCOME")) {
                num = response.charAt(8) - 48;
                this.setTitle("Maze Player" + num);
            }
            while (true) {
                response = in.readLine();
                System.out.println(response);
                if (response.startsWith("MESSAGE")) {
                    control.setMessage(response.substring(8));
                } else if (response.startsWith("TIME")) {
                    time = response.charAt(4) - 48;
                    control.setTime(time);
                } else if (response.startsWith("DIFF")) {
                	size = Integer.parseInt(response.substring(4));
                } else if (response.startsWith("MAZE")) {
                	mazeStr = response.substring(4);
                } else if (response.startsWith("START")) {
                	JOptionPane.showMessageDialog(null, "Game setup\nThe maze size is: "+ 500/size + "X" + 500/size + ".\nThe game time is: "+time+" min.");  
    				control.gameStart(mazeShow, mazeStr, size);
                } else if (response.startsWith("RESULT")) {

    				JOptionPane.showMessageDialog(null, response.substring(6), "Result",JOptionPane.PLAIN_MESSAGE);
                	//JOptionPane.showMessageDialog(null, response.substring(6)); 
                }
            }
        } catch (Exception e) {
        	
        }
        finally {
            socket.close();
        }
	}


	/* Creator: Tim Xu
	 * Name: MazeFrame类
	 * Function: 创建游戏界面 超类至JFrame窗体
	 * Create by: 04/22/2016
	 */
	public MazeFrame(String ipAdd) throws Exception {
		super();
		
		socket = new Socket(ipAdd, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    	
		this.setTitle("MAZE");
		frameflow=new FlowLayout(); //设置流布局器 用于设置组件间宽度
		icon=new ImageIcon("res\\图标.png");
		framePanel= new BackPanel(); //背景板
		framePanel.setLayout(frameflow); //往背景上绘制器件布局
		this.add(framePanel);
		
		
		//动态调整界面出现位置 焦点为二分之一屏幕，即中央显示
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenX = screenSize.width/2-fWidth/2;
		int screenY = screenSize.height/2-fHeight/2;
		setBounds(screenX, screenY,fWidth,fHeight);
		
		//窗口属性设置
		this.setResizable(false);//禁止动态调整
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置窗口关闭方法
		this.setIconImage(icon.getImage());//设置图标
		
		//设置组件间宽距和长距
		frameflow.setHgap(50);
		frameflow.setVgap(35);
		
		//绘制各种面板，如迷宫 按钮等
		mazeShow=new MazeShowPanel(this);//画迷宫
		control=new ControlPanel(this);//画控制按钮
		framePanel.add(mazeShow);
		framePanel.add(control);
	
		control.setMazeSizeListener(mazeShow);//绑定运行监听
		control.setRunListener(mazeShow);//绑定最短路径运行监听
		mazeShow.getControlPanel(control);
		this.setVisible(true);
	}
	
	public void getReady()
	{
		System.out.println("READY");
		try {
			out.println("DIFF" + num + control.getDiff());
			out.println("TIME" + num + control.getTime());
			out.println("READY" + num);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finished(int steps, int time)
	{
		System.out.println("FINISHED");
		try {
			out.println("STEP" + num + steps);
			if (time == 0) {
				out.println("ENDT" + num + -1);
				JOptionPane.showMessageDialog(null, "Time Out.", "GAME OVER",JOptionPane.ERROR_MESSAGE); 
			} else {
				out.println("ENDT" + num + time);
				JOptionPane.showMessageDialog(null, "You finished this maze with "+ steps +" steps and " + time + " Second.\nPlease waiting for your opponent.", "Congratulation",JOptionPane.PLAIN_MESSAGE); 
			}
			
			out.println("ENDS" + num);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}


/* Creator: Tim Xu
 * Name: BackPanel类
 * Function: 背景板
 * Create by: 04/22/2016
 */
class BackPanel extends JPanel
{
	private ImageIcon frameBack;
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		frameBack=new ImageIcon("res\\背景图.png");
		g.drawImage(frameBack.getImage(),0, 0, null);
		
	}
}

