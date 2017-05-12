package com.tim.maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout.Constraints;
import javax.swing.border.TitledBorder;

public class ControlPanel extends JPanel {
	
	private final int cpWidth=250;
	private final int cpHeight=600;
	
	private GridBagLayout gridBag;
	private MazeFrame mf;
	private JLabel leftLbl = new JLabel("LEFT:");
	private SpinnerNumberModel timeMin = new SpinnerNumberModel(2, 0, 99, 1);
    private JSpinner countMinuteSpn = new JSpinner(timeMin);
    private JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) countMinuteSpn.getEditor();
    private JLabel countMinuteLbl = new JLabel("min");
    private JTextField countSecondTxt = new JTextField("00");
    private JLabel countSecondLbl = new JLabel("sec");
    private JPanel countPnl = new JPanel();
	private GridBagConstraints constraintsTimer;
	private JComboBox diffSelection;
	private GridBagConstraints constraintsCombo;
	private JButton gameStart;
	private GridBagConstraints constraintsZero;
	private JButton runByCom;
	private GridBagConstraints constraintsRun;
	private JTextArea message = new JTextArea("Welcome!");
	private GridBagConstraints constraintsMess;
	private CounterThread t;
	/*
	private JButton ten10;
	private GridBagConstraints constraints10;
	private JButton twentyfive25;
	private GridBagConstraints constraints25;
	private JButton fifty50;
	private GridBagConstraints constraints50;
	*/
	
	
	
	/* Creator: Tim Xu
	 * Name: ControlPanel类
	 * Function: 背景板
	 * Create by: 04/22/2016
	 */
	public ControlPanel(MazeFrame mf) {
		super();
		
		this.mf = mf;
		this.setBackground(Color.lightGray);
		this.setPreferredSize(new Dimension(cpWidth, cpHeight));
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK,2),"CONTROL"));
		
		gridBag = new GridBagLayout();
		this.setLayout(gridBag);
		/*
		ten10=new JButton("����48x48�Թ�");
		ten10.setPreferredSize(new Dimension(220, 50));
		ten10.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		ten10.setFocusable(false);
		constraints10=new GridBagConstraints();
		constraints10.weightx=100;
		constraints10.weighty=100;
		constraints10.gridx=0;
		constraints10.gridy=3;
		constraints10.gridheight=1;
		constraints10.gridwidth=GridBagConstraints.REMAINDER;
		constraints10.fill= GridBagConstraints.NONE;
		this.add(ten10, constraints10);
		
		twentyfive25=new JButton("����23x23�Թ�");
		twentyfive25.setPreferredSize(new Dimension(220, 50));
		twentyfive25.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		twentyfive25.setFocusable(false);
		constraints25=new GridBagConstraints();
		constraints25.weightx=100;
		constraints25.weighty=100;
		constraints25.gridx=0;
		constraints25.gridy=2;
		constraints25.gridheight=1;
		constraints25.gridwidth=GridBagConstraints.REMAINDER;
		constraints25.fill= GridBagConstraints.NONE;
		this.add(twentyfive25, constraints25);
		
		fifty50=new JButton("����8x8�Թ�");
		fifty50.setPreferredSize(new Dimension(220, 50));
		fifty50.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		fifty50.setFocusable(false);
		constraints50=new GridBagConstraints();
		constraints50.weightx=100;
		constraints50.weighty=100;
		constraints50.gridx=0;
		constraints50.gridy=1;
		constraints50.gridheight=1;
		constraints50.gridwidth=GridBagConstraints.REMAINDER;
		constraints50.fill= GridBagConstraints.NONE;
		this.add(fifty50, constraints50);
		*/
		
		String[] sHard = {"Easy 10*10","Normal 25*25","Hard 50*50"};
		diffSelection = new JComboBox(sHard);
		diffSelection.setPreferredSize(new Dimension(220, 100));
		diffSelection.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		diffSelection.setFocusable(true);
		constraintsCombo=new GridBagConstraints();
		constraintsCombo.weightx=100;
		constraintsCombo.weighty=100;
		constraintsCombo.gridx=0;
		constraintsCombo.gridy=0;
		constraintsCombo.gridheight=1;
		constraintsCombo.gridwidth=GridBagConstraints.REMAINDER;
		constraintsCombo.fill= GridBagConstraints.NONE;
		this.add(diffSelection, constraintsCombo);
		

        countSecondTxt.setHorizontalAlignment(JTextField.RIGHT);
        countSecondTxt.setFocusable(false);
        countPnl.setBackground(Color.lightGray);
        countPnl.setLayout(new GridLayout(1, 5, 5, 5));
        countPnl.setBorder(new TitledBorder("TIME COUNT"));
        countPnl.add(leftLbl);
        countPnl.add(countMinuteSpn);
        countPnl.add(countMinuteLbl);
        countPnl.add(countSecondTxt);
        countPnl.add(countSecondLbl);
        
        constraintsTimer=new GridBagConstraints();
        constraintsTimer.weightx=100;
        constraintsTimer.weighty=100;
        constraintsTimer.gridx=0;
        constraintsTimer.gridy=1;
        constraintsTimer.gridheight=1;
        constraintsTimer.gridwidth=GridBagConstraints.REMAINDER;
        constraintsTimer.fill= GridBagConstraints.NONE;
		this.add(countPnl, constraintsTimer);
		
		
		gameStart=new JButton("Ready");
		gameStart.setPreferredSize(new Dimension(220, 50));
		gameStart.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
		gameStart.setFocusable(true);
		constraintsZero=new GridBagConstraints();
		constraintsZero.weightx=100;
		constraintsZero.weighty=100;
		constraintsZero.gridx=0;
		constraintsZero.gridy=2;
		constraintsZero.gridheight=1;
		constraintsZero.gridwidth=GridBagConstraints.REMAINDER;
		constraintsZero.fill= GridBagConstraints.NONE;
		this.add(gameStart, constraintsZero);
		
		runByCom=new JButton("Show shortest path");
		runByCom.setPreferredSize(new Dimension(220, 50));
		runByCom.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		runByCom.setFocusable(false);
		constraintsRun=new GridBagConstraints();
		constraintsRun.weightx=100;
		constraintsRun.weighty=100;
		constraintsRun.gridx=0;
		constraintsRun.gridy=3;
		constraintsRun.gridheight=1;
		constraintsRun.gridwidth=GridBagConstraints.REMAINDER;
		constraintsRun.fill= GridBagConstraints.NONE;
		this.add(runByCom, constraintsRun);
		
		message.setPreferredSize(new Dimension(220, 50));
		message.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		message.setEditable(false);
		message.setBackground(Color.lightGray);
		constraintsMess=new GridBagConstraints();
		constraintsMess.weightx=100;
		constraintsMess.weighty=100;
		constraintsMess.gridx=0;
		constraintsMess.gridy=4;
		constraintsMess.gridheight=1;
		constraintsMess.gridwidth=GridBagConstraints.REMAINDER;
		constraintsMess.fill= GridBagConstraints.NONE;
		this.add(message, constraintsMess);
	}
	
	public void setMazeSizeListener(final MazeShowPanel p)
	{	
		gameStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mf.getReady();
			}
		});
	}
	
	public void setTime(int min)
	{
		t = new CounterThread(editor.getTextField(), countSecondTxt);
		t.showTime(min, 0);
	}
	
	public int getDiff()
	{
		return diffSelection.getSelectedIndex();
	}
	
	public String getTime()
	{
		return editor.getTextField().getText();
	}
	
	public void gameStart(final MazeShowPanel p, String mazeStr, int size)//public void gameStart(final MazeShowPanel p)
	{
//		switch(diffSelection.getSelectedIndex())
//		{
//			case 0:
//				p.setMazeSize(50);
//				break;
//			case 1:
//				p.setMazeSize(20);
//				break;
//			case 2:
//				p.setMazeSize(10);
//				break;
//			default:
//				p.setMazeSize(0);
//				break;
//		}
		p.setMazeSize(mazeStr, size);
		startTimer();
	}
	public void setRunListener(final MazeShowPanel p)
	{
		runByCom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				p.computerRun();
			}
		});		
	}

	public void startTimer(){
        t.start();
        diffSelection.setEnabled(false);
        countMinuteSpn.setEnabled(false);
        gameStart.setEnabled(false);
	}
	
	public void setMessage(String str) {
		message.setText(str);
	}
	
	private class CounterThread extends Thread implements ActionListener {
		private javax.swing.Timer timer = new javax.swing.Timer(1000, this);
		
        private JTextField minuteTxt = null;
        private JTextField secondTxt = null;
        private int countSec = 0;
        private int minute = -1;
        private int second = -1;
 
        public CounterThread(JTextField m, JTextField s) {
            minuteTxt = m;
            secondTxt = s;
 
            if (minuteTxt.getText().length() == 0)
                minute = 0;
            else
                minute = Integer.parseInt(minuteTxt.getText());
 
            if (secondTxt.getText().length() == 0)
                second = 0;
            else
                second = Integer.parseInt(secondTxt.getText());
        }
 
        public void run() {
            timer.start();
        }
 
        public void stopCount() {
            timer.stop();
        }
 
        public void actionPerformed(ActionEvent e) {
            second = second - 1;
            countSec = countSec + 1;
            if (second >= 0) {
                showTime(minute, second);
            } else {
                if (minute > 0) {
                    minute = minute - 1;
                    second = 59;
                    showTime(minute, second);
                } else {
                	resetButton();
                	mf.finished(0, 0);
                }
            }
        }
        
        public void showTime(int m, int s) {
            minuteTxt.setText(String.valueOf(m));
            secondTxt.setText(String.valueOf(s));       
        }
	}
	
	public int resetButton () {
		t.stopCount();
		t.showTime(2,0);
		diffSelection.setEnabled(true);
        countMinuteSpn.setEnabled(true);
        gameStart.setEnabled(true);
        
        return t.countSec;
	}

	public void showErrorMsgDialog(String msg) {
        javax.swing.JOptionPane.showMessageDialog(null, msg, "ERROR",
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

}
	

