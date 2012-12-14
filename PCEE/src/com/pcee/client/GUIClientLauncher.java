package com.pcee.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.clientmodule.ClientModuleImpl;
import com.pcee.client.reserverelease.MultiDomainReserveRelease;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPEndPointsObject;
import com.pcee.protocol.message.objectframe.impl.PCEPGenericExplicitRouteObjectImpl;
import com.pcee.protocol.message.objectframe.impl.PCEPITResourceObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

/**
 * @author Yuesheng Zhong
 * 
 */
public class GUIClientLauncher extends JFrame implements ActionListener {

	private JPanel contentPane;

	private int contentPaneWidth = 800;
	private int contentPaneHeight = 750;
	private JButton btnParentPCE;

	private JButton btnStartDomain_1;
	private JButton btnStartDomain_2;

	private JButton btnConnectWithDomain_1;
	private JButton btnConnectWithDomain_2;

	private JTextField tfSourceDomain_1;
	private JTextField tfDestinationDomain_1;
	private JTextField tfSourceDomain_2;
	private JTextField tfDestinationDomain_2;

	private JComboBox bandwidth_1;
	private JComboBox delay_1;
	private JComboBox bandwidth_2;
	private JComboBox delay_2;

	private JLabel lblSourceDomain_1;
	private JLabel lblSourceDomain_2;
	private JLabel lblDestinationDomain_1;
	private JLabel lblDestinationDomain_2;

	private JCheckBox chckbxItRequestDomain_1;
	private JCheckBox chckbxItRequestDomain_2;

	private JComboBox cpu1;
	private JComboBox ram1;
	private JComboBox storage1;
	private JComboBox cpu2;
	private JComboBox ram2;
	private JComboBox storage2;

	private JButton btnRequestDomain_1;
	private JButton btnRequestDomain_2;

	private JLabel taResultDomain_1;
	private JLabel taResultDomain_2;

	private JButton btnReserve_1;
	private JButton btnReserve_2;

	private JLabel rrResultDomain_1;
	private JLabel rrResultDomain_2;

	// --------------- Attributes --------------------
	private boolean parentStarted = false;
	private boolean domain1ServerStarted = false;
	private boolean domain2ServerStarted = false;
	private boolean connect1Established = false;
	private boolean connect2Established = false;
	private boolean isITRequest1 = false;
	private boolean isITRequest2 = false;
	private boolean roleReserve1 = false;
	private boolean roleReserve2 = false;
	private boolean hasBandwidth1 = false;
	private boolean hasBandwidth2 = false;

	private String domainServerAddress1 = "127.0.0.1";
	private int domainServerPort1;
	private String domainServerAddress2 = "127.0.0.1";
	private int domainServerPort2;

	private double bandwidth1 = 0;
	private double bandwidth2 = 0;
	private String itNode1 = "";
	private int CPU1 = 0, RAM1 = 0, STORAGE1 = 0;
	private String itNode2 = "";
	private int CPU2 = 0, RAM2 = 0, STORAGE2 = 0;

	private ArrayList<String> vertexSequence1 = null;
	private ArrayList<String> vertexSequence2 = null;

	private ModuleManagement lm = new ModuleManagement(false);;
	private JButton button;
	
	private String projectPath = "F:\\Users\\joneisr-ssd\\Documents\\GitHub\\jonesir\\Path-Computation-Element-Emulator\\PCEE\\";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIClientLauncher frame = new GUIClientLauncher();
					frame.setResizable(false);
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
		setTitle("Hierarchical PCE Emulator with IT support");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, this.contentPaneWidth, this.contentPaneHeight);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		// ----------------------- Parent PCE Button --------------------------
		btnParentPCE = new JButton("Start Parent PCE");
		btnParentPCE.setBounds(10, 11, getSize().width - 40, 60);
		contentPane.add(btnParentPCE);

		// ----------------------- Domain Starter Button ----------------------
		btnStartDomain_1 = new JButton("Start Domain 1 Server");
		btnStartDomain_1.setBounds(10, btnParentPCE.getY() + btnParentPCE.getSize().height + 20, (btnParentPCE.getSize().width / 2) - 10, 34);
		contentPane.add(btnStartDomain_1);

		btnStartDomain_2 = new JButton("Start Domain 2 Server");
		btnStartDomain_2.setBounds(btnParentPCE.getSize().width + btnParentPCE.getX() - btnStartDomain_1.getSize().width, btnStartDomain_1.getY(), btnStartDomain_1.getSize().width, btnStartDomain_1.getSize().height);
		contentPane.add(btnStartDomain_2);

		// ----------------------- Init Connection Button ---------------------
		btnConnectWithDomain_1 = new JButton("Connect With Domain 1 Server");
		btnConnectWithDomain_1.setBounds(10, btnStartDomain_1.getY() + btnStartDomain_1.getSize().height + 20, btnStartDomain_1.getSize().width, 34);
		contentPane.add(btnConnectWithDomain_1);

		btnConnectWithDomain_2 = new JButton("Connect With Domain 2 Server");
		btnConnectWithDomain_2.setBounds(btnStartDomain_2.getX(), btnConnectWithDomain_1.getY(), btnStartDomain_2.getSize().width, btnConnectWithDomain_1.getSize().height);
		contentPane.add(btnConnectWithDomain_2);

		// ------------------------ Source and Destination Text Field ---------
		lblSourceDomain_1 = new JLabel("Source: ");
		lblSourceDomain_1.setBounds(btnConnectWithDomain_1.getX(), btnConnectWithDomain_1.getY() + btnConnectWithDomain_1.getSize().height + 24, 60, 14);
		contentPane.add(lblSourceDomain_1);

		lblDestinationDomain_1 = new JLabel("Destination: ");
		lblDestinationDomain_1.setBounds(195, 203, 80, 14);
		contentPane.add(lblDestinationDomain_1);

		lblSourceDomain_2 = new JLabel("Source: ");
		lblSourceDomain_2.setBounds(400, 203, 71, 14);
		contentPane.add(lblSourceDomain_2);

		lblDestinationDomain_2 = new JLabel("Destination: ");
		lblDestinationDomain_2.setBounds(585, 203, 95, 14);
		contentPane.add(lblDestinationDomain_2);

		tfSourceDomain_1 = new JTextField();
		tfSourceDomain_1.setText("192.169.2.1");
		tfSourceDomain_1.setBounds(66, 200, 86, 20);
		contentPane.add(tfSourceDomain_1);
		tfSourceDomain_1.setColumns(10);

		tfDestinationDomain_1 = new JTextField();
		tfDestinationDomain_1.setText("192.169.2.14");
		tfDestinationDomain_1.setColumns(10);
		tfDestinationDomain_1.setBounds(294, 200, 86, 20);
		contentPane.add(tfDestinationDomain_1);

		tfSourceDomain_2 = new JTextField();
		tfSourceDomain_2.setText("192.168.2.1");
		tfSourceDomain_2.setColumns(10);
		tfSourceDomain_2.setBounds(481, 200, 80, 20);
		contentPane.add(tfSourceDomain_2);

		tfDestinationDomain_2 = new JTextField();
		tfDestinationDomain_2.setText("192.168.2.13");
		tfDestinationDomain_2.setColumns(10);
		tfDestinationDomain_2.setBounds(690, 200, 80, 20);
		contentPane.add(tfDestinationDomain_2);

		// ------------------------- Constraints Combobox --------------------
		JLabel lblBandwidthDomain_1 = new JLabel("Bandwidth: ");
		lblBandwidthDomain_1.setBounds(10, 258, 74, 14);
		contentPane.add(lblBandwidthDomain_1);

		bandwidth_1 = new JComboBox();
		bandwidth_1.setModel(new DefaultComboBoxModel(new String[] { "0", "2", "4", "6", "8" }));
		bandwidth_1.setBounds(94, 255, 77, 20);
		contentPane.add(bandwidth_1);

		JLabel lblDelayDomain_1 = new JLabel("Delay: ");
		lblDelayDomain_1.setBounds(195, 258, 46, 14);
		contentPane.add(lblDelayDomain_1);

		delay_1 = new JComboBox();
		delay_1.setModel(new DefaultComboBoxModel(new String[] { "32", "64", "128" }));
		delay_1.setBounds(275, 255, 77, 20);
		contentPane.add(delay_1);

		JLabel lblBandwidthDomain_2 = new JLabel("Bandwidth: ");
		lblBandwidthDomain_2.setBounds(400, 258, 74, 14);
		contentPane.add(lblBandwidthDomain_2);

		bandwidth_2 = new JComboBox();
		bandwidth_2.setModel(new DefaultComboBoxModel(new String[] { "0", "2", "4", "6", "8" }));
		bandwidth_2.setBounds(484, 255, 77, 20);
		contentPane.add(bandwidth_2);

		JLabel lblDelayDomain_2 = new JLabel("Delay: ");
		lblDelayDomain_2.setBounds(585, 258, 46, 14);
		contentPane.add(lblDelayDomain_2);

		delay_2 = new JComboBox();
		delay_2.setModel(new DefaultComboBoxModel(new String[] { "32", "64", "128" }));
		delay_2.setBounds(665, 255, 77, 20);
		contentPane.add(delay_2);

		// ------------------------ IT Request Checkbox and
		// Combobox-----------------------
		chckbxItRequestDomain_1 = new JCheckBox("IT Request");
		chckbxItRequestDomain_1.setBounds(10, 289, 88, 23);
		contentPane.add(chckbxItRequestDomain_1);

		JLabel lblNewLabel = new JLabel("CPU:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(104, 293, 35, 14);
		contentPane.add(lblNewLabel);

		cpu1 = new JComboBox();
		cpu1.setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6" }));
		cpu1.setBounds(138, 290, 43, 20);
		contentPane.add(cpu1);

		JLabel lblRam = new JLabel("RAM:");
		lblRam.setHorizontalAlignment(SwingConstants.CENTER);
		lblRam.setBounds(191, 293, 35, 14);
		contentPane.add(lblRam);

		ram1 = new JComboBox();
		ram1.setModel(new DefaultComboBoxModel(new String[] { "128", "256", "512" }));
		ram1.setBounds(225, 290, 50, 20);
		contentPane.add(ram1);

		JLabel lblStrg = new JLabel("STRG:");
		lblStrg.setHorizontalAlignment(SwingConstants.CENTER);
		lblStrg.setBounds(285, 293, 35, 14);
		contentPane.add(lblStrg);

		storage1 = new JComboBox();
		storage1.setModel(new DefaultComboBoxModel(new String[] { "10", "20", "30", "40", "50", "60", "70", "80", "90" }));
		storage1.setBounds(330, 290, 50, 20);
		contentPane.add(storage1);

		JLabel label = new JLabel("CPU:");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(494, 293, 35, 14);
		contentPane.add(label);

		cpu2 = new JComboBox();
		cpu2.setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6" }));
		cpu2.setBounds(527, 290, 44, 20);
		contentPane.add(cpu2);

		JLabel label_1 = new JLabel("RAM:");
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setBounds(581, 293, 35, 14);
		contentPane.add(label_1);

		ram2 = new JComboBox();
		ram2.setModel(new DefaultComboBoxModel(new String[] { "128", "256", "512" }));
		ram2.setBounds(612, 290, 50, 20);
		contentPane.add(ram2);

		JLabel label_2 = new JLabel("STRG:");
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setBounds(675, 293, 35, 14);
		contentPane.add(label_2);

		storage2 = new JComboBox();
		storage2.setModel(new DefaultComboBoxModel(new String[] { "10", "20", "30", "40", "50", "60", "70", "80", "90" }));
		storage2.setBounds(720, 290, 50, 20);
		contentPane.add(storage2);

		chckbxItRequestDomain_2 = new JCheckBox("IT Request");
		chckbxItRequestDomain_2.setBounds(400, 289, 97, 23);
		contentPane.add(chckbxItRequestDomain_2);

		// ----------------------- Make Request Button ------------------------
		btnRequestDomain_1 = new JButton("Domain 1 Make Request");
		btnRequestDomain_1.setBounds(btnStartDomain_1.getX(), btnConnectWithDomain_1.getY() + btnConnectWithDomain_1.getSize().height + 150, btnStartDomain_1.getSize().width, 34);
		contentPane.add(btnRequestDomain_1);

		btnRequestDomain_2 = new JButton("Domain 2 Make Request");
		btnRequestDomain_2.setBounds(btnStartDomain_2.getX(), btnRequestDomain_1.getY(), btnRequestDomain_1.getSize().width, btnRequestDomain_1.getSize().height);
		contentPane.add(btnRequestDomain_2);

		JLabel lblResultDomain = new JLabel("Result Domain 1");
		lblResultDomain.setHorizontalAlignment(SwingConstants.CENTER);
		lblResultDomain.setBounds(109, 395, 152, 14);
		contentPane.add(lblResultDomain);

		JLabel lblResultDomain_1 = new JLabel("Result Domain 2");
		lblResultDomain_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblResultDomain_1.setBounds(510, 395, 152, 14);
		contentPane.add(lblResultDomain_1);

		taResultDomain_1 = new JLabel();
		taResultDomain_1.setBounds(10, 420, 370, 129);
		contentPane.add(taResultDomain_1);

		taResultDomain_2 = new JLabel();
		taResultDomain_2.setBounds(400, 420, 370, 129);
		contentPane.add(taResultDomain_2);

		btnReserve_1 = new JButton("Reserve");
		btnReserve_1.setHorizontalAlignment(SwingConstants.CENTER);
		btnReserve_1.setBounds(this.btnRequestDomain_1.getX(), taResultDomain_1.getY() + taResultDomain_1.getSize().height + 15, this.btnRequestDomain_1.getSize().width, this.btnRequestDomain_1.getSize().height);
		contentPane.add(btnReserve_1);

		btnReserve_2 = new JButton("Reserve");
		btnReserve_2.setHorizontalAlignment(SwingConstants.CENTER);
		btnReserve_2.setBounds(this.btnRequestDomain_2.getX(), btnReserve_1.getY(), btnReserve_1.getSize().width, btnReserve_1.getSize().height);
		contentPane.add(btnReserve_2);

		rrResultDomain_1 = new JLabel();
		rrResultDomain_1.setBounds(taResultDomain_1.getX(), btnReserve_1.getY() + btnReserve_1.getSize().height + 10, taResultDomain_1.getSize().width, 60);
		contentPane.add(rrResultDomain_1);

		rrResultDomain_2 = new JLabel();
		rrResultDomain_2.setBounds(taResultDomain_2.getX(), btnReserve_2.getY() + btnReserve_2.getSize().height + 10, taResultDomain_2.getSize().width, 60);
		contentPane.add(rrResultDomain_2);

		button = new JButton("");
		button.setEnabled(false);
		button.setBackground(Color.BLACK);
		button.setBounds(387, 70, 6, 660);
		contentPane.add(button);

		addActionListeners();

		changeAppearance();

		initParams();

		parseMultiDomainInfo();
	}

	private void parseMultiDomainInfo() {
		MultiDomainReserveRelease.parseMultiDomainInfo("multiDomainInfoClient.txt");
	}

	private void addActionListeners() {
		this.btnParentPCE.setActionCommand("startparent");
		this.btnParentPCE.addActionListener(this);
		this.btnStartDomain_1.setActionCommand("startdomain1");
		this.btnStartDomain_1.addActionListener(this);
		this.btnStartDomain_2.setActionCommand("startdomain2");
		this.btnStartDomain_2.addActionListener(this);
		this.btnConnectWithDomain_1.setActionCommand("connect1");
		this.btnConnectWithDomain_1.addActionListener(this);
		this.btnConnectWithDomain_2.setActionCommand("connect2");
		this.btnConnectWithDomain_2.addActionListener(this);
		this.chckbxItRequestDomain_1.setActionCommand("it1");
		this.chckbxItRequestDomain_1.addActionListener(this);
		this.chckbxItRequestDomain_2.setActionCommand("it2");
		this.chckbxItRequestDomain_2.addActionListener(this);
		this.btnRequestDomain_1.setActionCommand("request1");
		this.btnRequestDomain_1.addActionListener(this);
		this.btnRequestDomain_2.setActionCommand("request2");
		this.btnRequestDomain_2.addActionListener(this);
		this.btnReserve_1.setActionCommand("reserve1");
		this.btnReserve_1.addActionListener(this);
		this.btnReserve_2.setActionCommand("reserve2");
		this.btnReserve_2.addActionListener(this);
	}

	private void changeAppearance() {
		disableIT1();
		disableIT2();
		disableReserve1();
		disableReserve2();
	}

	private void enableReserve1() {
		this.btnReserve_1.setEnabled(true);
		this.btnReserve_1.setText("Reserve");
		this.roleReserve1 = true;
	}

	private void disableReserve1() {
		this.btnReserve_1.setEnabled(false);
	}

	private void enableReserve2() {
		this.btnReserve_2.setEnabled(true);
		this.btnReserve_2.setText("Reserve");
		this.roleReserve2 = true;
	}

	private void disableReserve2() {
		this.btnReserve_2.setEnabled(false);
	}

	private void initParams() {
		Properties reader1 = new Properties();
		Properties reader2 = new Properties();
		try {
			reader1.load(new FileInputStream("initDomain1.cfg"));
			reader2.load(new FileInputStream("initDomain2.cfg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.domainServerPort1 = Integer.parseInt(reader1.getProperty("port"));
		this.domainServerPort2 = Integer.parseInt(reader2.getProperty("port"));
	}

	private void enableIT1() {
		this.cpu1.setEnabled(true);
		this.ram1.setEnabled(true);
		this.storage1.setEnabled(true);

		this.tfDestinationDomain_1.setEnabled(false);
	}

	private void disableIT1() {
		this.cpu1.setEnabled(false);
		this.ram1.setEnabled(false);
		this.storage1.setEnabled(false);

		this.tfDestinationDomain_1.setEnabled(true);
	}

	private void enableIT2() {
		this.cpu2.setEnabled(true);
		this.ram2.setEnabled(true);
		this.storage2.setEnabled(true);

		this.tfDestinationDomain_2.setEnabled(false);
	}

	private void disableIT2() {
		this.cpu2.setEnabled(false);
		this.ram2.setEnabled(false);
		this.storage2.setEnabled(false);

		this.tfDestinationDomain_2.setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals("startparent") && !this.parentStarted)
			startParentPCE();
		else if (arg0.getActionCommand().equals("startdomain1") && !this.domain1ServerStarted)
			startDomainServer1();
		else if (arg0.getActionCommand().equals("startdomain2") && !this.domain2ServerStarted)
			startDomainServer2();
		else if (arg0.getActionCommand().equals("connect1"))
			establishConnection1();
		else if (arg0.getActionCommand().equals("connect2"))
			establishConnection2();
		else if (arg0.getActionCommand().equals("it1"))
			processIT1();
		else if (arg0.getActionCommand().equals("it2"))
			processIT2();
		else if (arg0.getActionCommand().equals("request1"))
			issueRequest1();
		else if (arg0.getActionCommand().equals("request2"))
			issueRequest2();
		else if (arg0.getActionCommand().equals("reserve1"))
			reserveRelease1();
		else if (arg0.getActionCommand().equals("reserve2"))
			reserveRelease2();
	}

	private void reserveRelease1() {
		if (this.roleReserve1) {
			makeReserve1();
		} else {
			makeRelease1();
		}

		updateReserveReleaseResult1();
	}

	private void makeReserve1() {
		if (this.isITRequest1) {
			String sourceDomain = MultiDomainReserveRelease.nodeDomainMapping.get(this.vertexSequence1.get(0));
			String itDomain = MultiDomainReserveRelease.nodeDomainMapping.get(this.itNode1);
			boolean sameDomain = sourceDomain.equals(itDomain);
			MultiDomainReserveRelease.itReserve(this.CPU1, this.RAM1, this.STORAGE1, this.itNode1, sameDomain);
		}
		MultiDomainReserveRelease.reserve(this.bandwidth1, this.vertexSequence1);
		this.btnReserve_1.setText("Reserved, click to release");
		this.roleReserve1 = false;
		this.btnRequestDomain_1.setEnabled(false);
	}

	private void makeRelease1() {
		if (this.isITRequest1) {
			String sourceDomain = MultiDomainReserveRelease.nodeDomainMapping.get(this.vertexSequence1.get(0));
			String itDomain = MultiDomainReserveRelease.nodeDomainMapping.get(this.itNode1);
			boolean sameDomain = sourceDomain.equals(itDomain);
			MultiDomainReserveRelease.itRelease(this.CPU1, this.RAM1, this.STORAGE1, this.itNode1, sameDomain);
		}
		MultiDomainReserveRelease.release(this.bandwidth1, this.vertexSequence1);
		this.btnReserve_1.setText("Released");
		disableReserve1();
		this.roleReserve1 = true;
		this.btnRequestDomain_1.setEnabled(true);
	}

	private void reserveRelease2() {
		if (this.roleReserve2) {
			makeReserve2();
		} else if (!this.roleReserve2) {
			makeRelease2();
		}

		updateReserveReleaseResult2();
	}

	private void makeReserve2() {
		if (this.isITRequest2) {
			String sourceDomain = MultiDomainReserveRelease.nodeDomainMapping.get(this.vertexSequence2.get(0));
			String itDomain = MultiDomainReserveRelease.nodeDomainMapping.get(this.itNode2);
			boolean sameDomain = sourceDomain.equals(itDomain);
			MultiDomainReserveRelease.itReserve(this.CPU2, this.RAM2, this.STORAGE2, this.itNode2, sameDomain);
		}
		MultiDomainReserveRelease.reserve(this.bandwidth2, this.vertexSequence2);
		this.btnReserve_2.setText("Reserved, click to release");
		this.roleReserve2 = false;
		this.btnRequestDomain_2.setEnabled(false);
	}

	private void makeRelease2() {
		if (this.isITRequest2) {
			String sourceDomain = MultiDomainReserveRelease.nodeDomainMapping.get(this.vertexSequence2.get(0));
			String itDomain = MultiDomainReserveRelease.nodeDomainMapping.get(this.itNode2);
			boolean sameDomain = sourceDomain.equals(itDomain);
			MultiDomainReserveRelease.itRelease(this.CPU2, this.RAM2, this.STORAGE2, this.itNode2, sameDomain);
		}
		MultiDomainReserveRelease.release(bandwidth2, vertexSequence2);
		this.btnReserve_2.setText("Released");
		disableReserve2();
		this.roleReserve2 = true;
		this.btnRequestDomain_2.setEnabled(true);
	}

	private void startParentPCE() {
		Properties reader = new Properties();
		try {
			reader.load(new FileInputStream("initParent.cfg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Runtime.getRuntime().exec("java -jar " + this.projectPath + "PCE.jar " + this.projectPath + "initParent.cfg > " + this.projectPath + "parentLog.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.parentStarted = true;
		this.btnParentPCE.setBackground(Color.GREEN);
		this.btnParentPCE.setText("Parent PCE is UP on port " + reader.getProperty("port"));
	}

	private void startDomainServer1() {
		if (this.parentStarted) {
			try {
				Runtime.getRuntime().exec("java -jar " + this.projectPath + "PCE.jar " + this.projectPath + "initDomain1.cfg > " + this.projectPath + "domain1Log.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.domain1ServerStarted = true;
			this.btnStartDomain_1.setBackground(Color.GREEN);
			this.btnStartDomain_1.setText("Domain 1 Server is UP on port " + this.domainServerPort1);
		}
	}

	private void startDomainServer2() {
		if (this.parentStarted) {
			try {
				Runtime.getRuntime().exec("java -jar " + this.projectPath + "PCE.jar " + this.projectPath + "initDomain2.cfg > " + this.projectPath + "domain2Log.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.domain2ServerStarted = true;
			this.btnStartDomain_2.setBackground(Color.GREEN);
			this.btnStartDomain_2.setText("Domain 2 Server is UP on port " + this.domainServerPort2);
		}
	}

	private void establishConnection1() {
		if (this.domain1ServerStarted && !this.connect1Established) {
			PCEPAddress serverAddress = new PCEPAddress(this.domainServerAddress1, this.domainServerPort1);
			this.lm.getClientModule().registerConnection(serverAddress, false, true, true);

			this.connect1Established = true;
			this.btnConnectWithDomain_1.setBackground(Color.GREEN);
			this.btnConnectWithDomain_1.setText("Connection with Server Established");
		}
	}

	private void establishConnection2() {
		if (this.domain2ServerStarted && !this.connect2Established) {
			PCEPAddress serverAddress = new PCEPAddress(this.domainServerAddress2, this.domainServerPort2);
			this.lm.getClientModule().registerConnection(serverAddress, false, true, true);

			this.connect2Established = true;
			this.btnConnectWithDomain_2.setBackground(Color.GREEN);
			this.btnConnectWithDomain_2.setText("Connection with Server Established");
		}
	}

	private void processIT1() {
		this.isITRequest1 = this.chckbxItRequestDomain_1.isSelected();
		if (this.isITRequest1)
			enableIT1();
		else
			disableIT1();
	}

	private void processIT2() {
		this.isITRequest2 = this.chckbxItRequestDomain_2.isSelected();
		if (this.isITRequest2)
			enableIT2();
		else
			disableIT2();
	}

	private void issueRequest1() {
		if (this.connect1Established) {
			PCEPAddress sourceAddress = new PCEPAddress(this.tfSourceDomain_1.getText().trim(), false);
			PCEPAddress destinationAddress = null;
			PCEPITResourceObject it = null;
			if (this.isITRequest1) {
				this.CPU1 = Integer.parseInt((String) this.cpu1.getSelectedItem());
				this.RAM1 = Integer.parseInt((String) this.ram1.getSelectedItem());
				this.STORAGE1 = Integer.parseInt((String) this.storage1.getSelectedItem());
				it = PCEPObjectFrameFactory.generatePCEPITResourceObject("1", "0", 0, this.CPU1, this.RAM1, this.STORAGE1);
				destinationAddress = sourceAddress;
			} else {
				destinationAddress = new PCEPAddress(this.tfDestinationDomain_1.getText().trim(), false);
			}

			PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "1", "1", "0", "432");
			PCEPEndPointsObject endPoints = PCEPObjectFrameFactory.generatePCEPEndPointsObject("1", "0", sourceAddress, destinationAddress);

			PCEPRequestFrame requestMessage = PCEPRequestFrameFactory.generatePathComputationRequestFrame(RP, endPoints);
			if (it != null)
				requestMessage.insertITResourceObject(it);

			if (this.bandwidth_1.getSelectedIndex() != 0) {
				this.hasBandwidth1 = true;
				this.bandwidth1 = Double.parseDouble((String) this.bandwidth_1.getSelectedItem());
				PCEPBandwidthObject bandwidth = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) this.bandwidth1);
				requestMessage.insertBandwidthObject(bandwidth);
			} else {
				this.hasBandwidth1 = false;
			}

			PCEPAddress destAddress = new PCEPAddress(this.domainServerAddress1, this.domainServerPort1);

			PCEPMessage message = PCEPMessageFactory.generateMessage(requestMessage);

			message.setAddress(destAddress);

			this.lm.getClientModule().sendMessage(message, ModuleEnum.SESSION_MODULE);

			PCEPMessage responseMessage = null;
			try {
				responseMessage = ((ClientModuleImpl) this.lm.getClientModule()).receiveQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (responseMessage != null) {
				updateResult1(PCEPResponseFrameFactory.getPathComputationResponseFrame(responseMessage));
			}

			this.rrResultDomain_1.setText("");
		}
	}

	private void issueRequest2() {
		if (this.connect2Established) {
			PCEPAddress sourceAddress = new PCEPAddress(this.tfSourceDomain_2.getText().trim(), false);
			PCEPAddress destinationAddress = null;
			PCEPITResourceObject it = null;
			if (this.isITRequest2) {
				this.CPU2 = Integer.parseInt((String) this.cpu2.getSelectedItem());
				this.RAM2 = Integer.parseInt((String) this.ram2.getSelectedItem());
				this.STORAGE2 = Integer.parseInt((String) this.storage2.getSelectedItem());
				it = PCEPObjectFrameFactory.generatePCEPITResourceObject("1", "0", 0, this.CPU2, this.RAM2, this.STORAGE2);
				destinationAddress = sourceAddress;
			} else {
				destinationAddress = new PCEPAddress(this.tfDestinationDomain_2.getText().trim(), false);
			}

			PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "1", "1", "0", "432");
			PCEPEndPointsObject endPoints = PCEPObjectFrameFactory.generatePCEPEndPointsObject("1", "0", sourceAddress, destinationAddress);

			PCEPRequestFrame requestMessage = PCEPRequestFrameFactory.generatePathComputationRequestFrame(RP, endPoints);
			if (it != null)
				requestMessage.insertITResourceObject(it);

			if (this.bandwidth_2.getSelectedIndex() != 0) {
				this.hasBandwidth2 = true;
				this.bandwidth2 = Double.parseDouble((String) this.bandwidth_2.getSelectedItem());
				PCEPBandwidthObject bandwidth = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) this.bandwidth2);
				requestMessage.insertBandwidthObject(bandwidth);
			} else {
				this.hasBandwidth2 = false;
			}

			PCEPAddress destAddress = new PCEPAddress(this.domainServerAddress2, this.domainServerPort2);

			PCEPMessage message = PCEPMessageFactory.generateMessage(requestMessage);

			message.setAddress(destAddress);

			this.lm.getClientModule().sendMessage(message, ModuleEnum.SESSION_MODULE);

			PCEPMessage responseMessage = null;
			try {
				responseMessage = ((ClientModuleImpl) this.lm.getClientModule()).receiveQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (responseMessage != null) {
				updateResult2(PCEPResponseFrameFactory.getPathComputationResponseFrame(responseMessage));
			}

			this.rrResultDomain_2.setText("");
		}
	}

	private void updateResult1(PCEPResponseFrame response) {

		ArrayList<EROSubobjects> objs = null;

		String updateString = "<html><center>";
		if (response.containsNoPathObject()) {
			if (this.isITRequest1)
				updateString += "<font color='red'>No IT Node has been found or no path between source and IT node</font></br>";
			else
				updateString += "No path between <font color='red' size=5>" + this.tfSourceDomain_1.getText() + "</font> and <font color='red' size=5>" + this.tfDestinationDomain_1.getText() + "</font><br>";
		} else {
			PCEPGenericExplicitRouteObjectImpl route = (PCEPGenericExplicitRouteObjectImpl) response.extractExplicitRouteObjectList().get(0);
			objs = route.getTraversedVertexList();
			if (this.isITRequest1) {
				this.itNode1 = ((PCEPAddress) objs.get(objs.size() - 1)).getIPv4Address(false);
				updateString += "<font color='green' size=6>IT node exists : </font> <font color='blue' size=6>" + this.itNode1 + "</font><br>";
			} else {
				updateString += "<font color='green' size=6>Path exists!</font><br>";
			}
			updateString += "Path is : ";
			for (int i = 0; i < objs.size(); i++) {
				updateString += "<font color='purple' size=4>" + ((PCEPAddress) objs.get(i)).getIPv4Address(false) + "</font>";
				if (i != objs.size() - 1) {
					updateString += " - ";
				}
			}

		}

		updateString += "</center></html>";

		this.taResultDomain_1.setText(updateString);
		if (!response.containsNoPathObject() && (this.hasBandwidth1 || this.isITRequest1)) {
			assignVertexSequence1(objs);
			enableReserve1();
		}
	}

	private void updateResult2(PCEPResponseFrame response) {
		ArrayList<EROSubobjects> objs = null;

		String updateString = "<html><center>";
		if (response.containsNoPathObject()) {
			if (this.isITRequest2)
				updateString += "<font color='red'>No IT Node has been found or no path between source and IT node</font></br>";
			else
				updateString += "No path between <font color='red' size=5>" + this.tfSourceDomain_2.getText() + "</font> and <font color='red' size=5>" + this.tfDestinationDomain_2.getText() + "</font><br>";
		} else {
			PCEPGenericExplicitRouteObjectImpl route = (PCEPGenericExplicitRouteObjectImpl) response.extractExplicitRouteObjectList().get(0);
			objs = route.getTraversedVertexList();
			if (this.isITRequest2) {
				this.itNode2 = ((PCEPAddress) objs.get(objs.size() - 1)).getIPv4Address(false);
				updateString += "<font color='green' size=6>IT node exists : </font> <font color='blue' size=6>" + this.itNode2 + "</font><br>";
			} else {
				updateString += "<font color='green' size=6>Path exists!</font><br>";
			}
			updateString += "Path is : ";
			for (int i = 0; i < objs.size(); i++) {
				updateString += "<font color='purple' size=4>" + ((PCEPAddress) objs.get(i)).getIPv4Address(false) + "</font>";
				if (i != objs.size() - 1) {
					updateString += " - ";
				}
			}

		}

		updateString += "</center></html>";

		this.taResultDomain_2.setText(updateString);
		if (!response.containsNoPathObject() && (this.hasBandwidth2 || this.isITRequest2)) {
			assignVertexSequence2(objs);
			enableReserve2();
		}
	}

	private void assignVertexSequence1(ArrayList<EROSubobjects> objs) {
		this.vertexSequence1 = new ArrayList<String>();
		for (int i = 0; i < objs.size(); i++)
			this.vertexSequence1.add(((PCEPAddress) objs.get(i)).getIPv4Address(false));
	}

	private void assignVertexSequence2(ArrayList<EROSubobjects> objs) {
		this.vertexSequence2 = new ArrayList<String>();
		for (int i = 0; i < objs.size(); i++)
			this.vertexSequence2.add(((PCEPAddress) objs.get(i)).getIPv4Address(false));
	}

	private void updateReserveReleaseResult1() {
		String updateString = "<html><center>";
		if (!this.roleReserve1) {
			if (this.isITRequest1)
				updateString += "IT NODE: <font color='blue'>" + this.itNode1 + " [CPU:" + this.CPU1 + ",RAM:" + this.RAM1 + ",STORAGE:" + this.STORAGE1 + "]</font>has been reserved!<br>";
			if (this.hasBandwidth1)
				updateString += "<font color='blue'>Bandwidth : " + this.bandwidth1 + "</font> has been reserved along the path<br>";
		} else {
			if (this.isITRequest1)
				updateString += "IT NODE: <font color='purple'>" + this.itNode1 + " [CPU:" + this.CPU1 + ",RAM:" + this.RAM1 + ",STORAGE:" + this.STORAGE1 + "]</font>has been released!<br>";
			if (this.hasBandwidth1)
				updateString += "<font color='purple'>Bandwidth : " + this.bandwidth1 + "</font> has been released along the path<br>";
		}
		updateString += "</center></html>";

		this.rrResultDomain_1.setText(updateString);
	}

	private void updateReserveReleaseResult2() {
		String updateString = "<html><center>";
		if (!this.roleReserve2) {
			if (this.isITRequest2)
				updateString += "IT NODE: <font color='blue'>" + this.itNode2 + " [CPU:" + this.CPU2 + ",RAM:" + this.RAM2 + ",STORAGE:" + this.STORAGE2 + "]</font>has been reserved!<br>";
			if (this.hasBandwidth2)
				updateString += "<font color='blue'>Bandwidth : " + this.bandwidth2 + "</font> has been reserved along the path<br>";
		} else {
			if (this.isITRequest2)
				updateString += "IT NODE: <font color='purple'>" + this.itNode2 + " [CPU:" + this.CPU2 + ",RAM:" + this.RAM2 + ",STORAGE:" + this.STORAGE2 + "]</font>has been released!<br>";
			if (this.hasBandwidth2)
				updateString += "<font color='purple'>Bandwidth : " + this.bandwidth2 + "</font> has been released along the path<br>";
		}
		updateString += "</center></html>";

		this.rrResultDomain_2.setText(updateString);
	}
}
