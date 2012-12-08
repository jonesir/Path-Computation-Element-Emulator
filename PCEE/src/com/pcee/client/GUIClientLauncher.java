package com.pcee.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUIClientLauncher extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIClientLauncher frame = new GUIClientLauncher();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUIClientLauncher() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 847, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnParentPCE = new JButton("Start Parent PCE");
		
		
		btnParentPCE.setBounds(10, 11, 511, 34);
		contentPane.add(btnParentPCE);
		
		JButton btnStartDomain = new JButton("Start Domain 1 Server");
		btnStartDomain.setBounds(10, 68, 242, 34);
		contentPane.add(btnStartDomain);
		
		JButton btnStartDomain_1 = new JButton("Start Domain 2 Server");
		btnStartDomain_1.setBounds(279, 68, 242, 34);
		contentPane.add(btnStartDomain_1);
		
		JButton btnDomainMake = new JButton("Domain 1 Make Request");
		btnDomainMake.setBounds(10, 274, 242, 34);
		contentPane.add(btnDomainMake);
		
		JButton btnDomainMake_1 = new JButton("Domain 2 Make Request");
		btnDomainMake_1.setBounds(279, 274, 242, 34);
		contentPane.add(btnDomainMake_1);
	}
	
	public void startParentPCE(){
		
	}
	
	public void startDomainServer1(){
		
	}
	
	public void startDomainServer2(){
		
	}
	
	
}
