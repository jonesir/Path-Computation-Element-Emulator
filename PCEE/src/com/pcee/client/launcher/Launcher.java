package com.pcee.client.launcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.clientmodule.ClientModuleImpl;
import com.pcee.client.connectionsource.SinglePathExponentialSource;
import com.pcee.client.connectionsource.Source;
import com.pcee.client.event.eventhandler.EventHandler;
import com.pcee.client.reserverelease.MultiDomainReserveRelease;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPBandwidthObject;
import com.pcee.protocol.message.objectframe.impl.PCEPEndPointsObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

import java.io.BufferedReader;

public class Launcher {

	public static int roundCount, minCPU, minRAM, minSTORAGE, maxCPU, maxRAM, maxSTORAGE;
	public static int startTime, endTime;
	public static double interArrivalTime, holdingTime, minBandwidth, maxBandwidth, initBandwidth, minDelay, maxDelay, initDelay;
	public static int itRequestPercent;
	public static boolean isITRequest;
	public static ModuleManagement lm;
	public static ArrayList<String> multiDomainNodes;
	public static HashMap<String, String> nodeDomainMapping;
	public static HashMap<String, PCEPAddress> domainAddressMapping;
	private static Random random = new Random();

	public Launcher() {
	}

	public void init() {
		// initialize parameters from init.cfg file which can be configured
		// accordingly for each simulation
		Properties reader = new Properties();
		try {
			reader.load(new FileInputStream("init.cfg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		roundCount = Integer.parseInt(reader.getProperty("roundCount"));
		initBandwidth = Double.parseDouble(reader.getProperty("initBandwidth"));
		minBandwidth = Double.parseDouble(reader.getProperty("minBandwidth"));
		maxBandwidth = Double.parseDouble(reader.getProperty("maxBandwidth"));
		minDelay = Double.parseDouble(reader.getProperty("minDelay"));
		maxDelay = Double.parseDouble(reader.getProperty("maxDelay"));
//		initDelay = Double.parseDouble(reader.getProperty("initDelay"));
		minCPU = Integer.parseInt(reader.getProperty("minCPU"));
		maxCPU = Integer.parseInt(reader.getProperty("maxCPU"));
		minRAM = Integer.parseInt(reader.getProperty("minRAM"));
		maxRAM = Integer.parseInt(reader.getProperty("maxRAM"));
		minSTORAGE = Integer.parseInt(reader.getProperty("minSTORAGE"));
		maxSTORAGE = Integer.parseInt(reader.getProperty("maxSTORAGE"));

		itRequestPercent = Integer.parseInt(reader.getProperty("itRequestPercent"));
		startTime = Integer.parseInt(reader.getProperty("startTime"));
		endTime = Integer.parseInt(reader.getProperty("endTime"));
		interArrivalTime = Double.parseDouble(reader.getProperty("interArrivalTime"));
		holdingTime = Double.parseDouble(reader.getProperty("holdingTime"));
		
		Logger.debugging = reader.getProperty("debugging").equalsIgnoreCase("on");
		Logger.logging = reader.getProperty("logging").equalsIgnoreCase("on");

		checkInitParams();
		
		// Initialize Multi-Domain Information
		MultiDomainReserveRelease.parseMultiDomainInfo("multiDomainInfoClient.txt");

		// Initialize Multi-Domain Node : Domain Mapping
		parseMultiDomainNodeDomainMapping(reader.getProperty("multiDomainNodes"));

		// Initialize Multi-Domain Domain : Address
		parseMultiDomainDomainAddressMapping(reader.getProperty("multiDomainNodes"));

		// Initialized ModuleManagement to establish connections to all domains
		initModuleManagement();

	}

	private void parseMultiDomainNodeDomainMapping(String multiDomainNodesFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(multiDomainNodesFile));
			String temp;
			int flag = 0;
			while ((temp = reader.readLine()) != null) {
				if (temp.trim().compareTo("NODES (") == 0) {
					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				localLogger("Incorrect Multi-Domian Information File");
				System.exit(0);
			}
			nodeDomainMapping = new HashMap<String, String>();
			multiDomainNodes = new ArrayList<String>();
			while ((temp = reader.readLine()) != null) {
				// End Loop when parsing of block is complete
				temp = temp.trim();
				if (temp.compareTo(")") == 0)
					break;
				// Not an empty line
				if (temp.length() != 0) {
					String[] temp1 = temp.split(":");
					if (temp1.length == 2) {
						nodeDomainMapping.put(temp1[0].trim(), temp1[1].trim());
						multiDomainNodes.add(temp1[0].trim());
					}
				}
			}
			reader.close();

		} catch (FileNotFoundException e) {
			localLogger("Multi-domain Info File not found : " + e.getMessage());
		} catch (IOException e) {
			localLogger("IOException when reading from Multi-DOmain Info File : " + e.getMessage());
		}
	}

	/**
	 * Function to parse the multi-domain topology file and extract information
	 * about the Domain PCEs
	 */
	private static void parseMultiDomainDomainAddressMapping(String multiDomainInfoPath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(multiDomainInfoPath));
			String temp;
			int flag = 0;
			while ((temp = reader.readLine()) != null) {
				if (temp.trim().compareTo("REQUESTADDRESS (") == 0) {
					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				localLogger("Incorrect Multi-Domian Information File");
				System.exit(0);
			}
			domainAddressMapping = new HashMap<String, PCEPAddress>();
			while ((temp = reader.readLine()) != null) {
				temp = temp.trim();
				// End Loop when parsing of block is complete
				if (temp.compareTo(")") == 0)
					break;
				// Not an empty line
				if (temp.length() != 0) {
					String[] temp1 = temp.split(":");
					if (temp1.length == 3) {
						domainAddressMapping.put(temp1[0].trim(), new PCEPAddress(temp1[1].trim(), Integer.parseInt(temp1[2].trim())));
					}
				}
			}
			reader.close();

		} catch (FileNotFoundException e) {
			localLogger("Multi-domain Info File not found : " + e.getMessage());
		} catch (IOException e) {
			localLogger("IOException when reading from Multi-DOmain Info File : " + e.getMessage());
		}

	}

	private void initModuleManagement() {
		lm = new ModuleManagement(false);
		for (String domainID : domainAddressMapping.keySet()) {
			PCEPAddress domainAddress = domainAddressMapping.get(domainID);
			lm.getClientModule().registerConnection(domainAddress, false, true, true);
		}
	}

	public static PCEPResponseFrame getPath(String sourceID, String destID, int cpu, int ram, int storage, double bandwidth, double delay) {
		PCEPAddress sourceAddress = new PCEPAddress(sourceID, false);
		PCEPAddress destinationAddress = new PCEPAddress(destID, false);

		PCEPBandwidthObject bandwidthObject = PCEPObjectFrameFactory.generatePCEPBandwidthObject("1", "0", (float) bandwidth);

		PCEPRequestParametersObject RP = PCEPObjectFrameFactory.generatePCEPRequestParametersObject("1", "0", "0", "1", "1", "0", "432");
		PCEPEndPointsObject endPoints = PCEPObjectFrameFactory.generatePCEPEndPointsObject("1", "0", sourceAddress, destinationAddress);
		PCEPRequestFrame requestMessage = PCEPRequestFrameFactory.generatePathComputationRequestFrame(RP, endPoints);
		requestMessage.insertBandwidthObject(bandwidthObject);
		
		PCEPAddress destAddress = domainAddressMapping.get(nodeDomainMapping.get(sourceID));

		PCEPMessage message = PCEPMessageFactory.generateMessage(requestMessage);

		message.setAddress(destAddress);

		Logger.timeStampsSentMilli.add(System.currentTimeMillis());
		Logger.timeStampsSentNano.add(System.nanoTime());
		lm.getClientModule().sendMessage(message, ModuleEnum.SESSION_MODULE);

		PCEPMessage responseMessage = null;
		try {
			responseMessage = ((ClientModuleImpl) lm.getClientModule()).receiveQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Logger.timeStampsReceivedMilli.add(System.currentTimeMillis());
		Logger.timeStampsReceivedNano.add(System.nanoTime());
		
		return PCEPResponseFrameFactory.getPathComputationResponseFrame(responseMessage);

	}

	public static boolean itOrNormal() {
		return isITRequest = random.nextInt(endTime) <= endTime*itRequestPercent/100;
	}

	public static void main(String[] args) {

		/** Init variables */
		Launcher launcher = new Launcher();
		launcher.init();

		for (int i = 0; i < roundCount; i++) {

			/**
			 * Specify Topology Importer and import topology into Gcontroller
			 * with given topology
			 */
			EventHandler.initEventHandler(startTime, endTime);// initialize
																// EventHandler

			/** Instantiate a Source object */
			Source source;
			source = new SinglePathExponentialSource(interArrivalTime, holdingTime, null, minCPU, maxCPU, minRAM, maxRAM, minSTORAGE, maxSTORAGE, minBandwidth, maxBandwidth, minDelay, maxDelay);

			/** Scenario with IT Resource */
			source.nextRequest(true);
			EventHandler.startEventHandler();

			// do the log and reset work with jonesir style at the end of each
			// round
			 Logger.logBlockingRate(source.getTotConnections(),
			 source.getBlockedConnections(), source.getBlockingProbability());
			 Logger.logTimeStamps();

			/** end the request sending process and reset client */
			// TopologyUpdateLauncher.sendUpdatje(GlobalCfg.prrAddress,
			// GlobalCfg.prrPort, "FINISHED");
			 Logger.reset();
		}
		System.out.println("\n\nDONE!");
		System.exit(0);// terminate application after all tests

	}

	/**
	 * Function for logging events
	 * 
	 * @param event
	 */
	private static void localLogger(String event) {
		Logger.logSystemEvents("[Launcher]     " + event);
	}

	/**
	 * Function for logging debug information
	 * 
	 * @param event
	 */
	private static void localDebugger(String event) {
		// Logger.debugger("[Launcher]     " + event);
	}
	
	private static void checkInitParams(){
		System.out.println("roundCount = " + roundCount);
		System.out.println("startTime = " + startTime);
		System.out.println("endTime = " + endTime);
		System.out.println("interArrivalTime = " + interArrivalTime);
		System.out.println("holdingTime = " + holdingTime);
		System.out.println("minBandwidth = " + minBandwidth);
		System.out.println("maxBandwidth = " + maxBandwidth);
		System.out.println("initBandwidth = " + initBandwidth);
		System.out.println("minDelay = " + minDelay);
		System.out.println("maxDelay = " + maxDelay);
		System.out.println("initDelay = " + initDelay);
	}

}
