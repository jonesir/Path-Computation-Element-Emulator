package com.pcee.client.reserverelease;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.pcee.architecture.computationmodule.ted.client.TopologyUpdateDomainClient;
import com.pcee.architecture.computationmodule.ted.client.TopologyUpdateParentClient;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;

public class MultiDomainReserveRelease {

	// HashMap to indicate the domain ID associated with a node address
	public static HashMap<String, String> nodeDomainMapping;

	// HashMap to indicate the location of the Domain PCE servers based on the
	// domain ID
	public static HashMap<String, PCEPAddress> topologyUpdateServerinfo;

	/**
	 * Function to parse the multi-domain topology file and extract information
	 * about the nodes in the topology and the corresponding domains
	 */
	private static void parseNodeDomainMapping(String multiDomainInfoPath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(multiDomainInfoPath));
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
	private static void parseTopologyServerInfo(String multiDomainInfoPath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(multiDomainInfoPath));
			String temp;
			int flag = 0;
			while ((temp = reader.readLine()) != null) {
				if (temp.trim().compareTo("TOPOLOGYUPDATE (") == 0) {
					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				localLogger("Incorrect Multi-Domian Information File");
				System.exit(0);
			}
			topologyUpdateServerinfo = new HashMap<String, PCEPAddress>();
			while ((temp = reader.readLine()) != null) {
				temp = temp.trim();
				// End Loop when parsing of block is complete
				if (temp.compareTo(")") == 0)
					break;
				// Not an empty line
				if (temp.length() != 0) {
					String[] temp1 = temp.split(":");
					if (temp1.length == 3) {
						topologyUpdateServerinfo.put(temp1[0].trim(), new PCEPAddress(temp1[1].trim(), Integer.parseInt(temp1[2].trim())));
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

	public static void parseMultiDomainInfo(String fileName) {
		parseTopologyServerInfo(fileName);
		parseNodeDomainMapping(fileName);
	}

	public static void reserve(double capacity, ArrayList<String> vertexSequence) {
		// Iteratively identify the sequence of nodes in the same domain

		int currentPos = 0;

		do {
			// if (currentPos == vertexSequence.size())
			// break;
			String currDomainID = nodeDomainMapping.get(vertexSequence.get(currentPos));
			// Get the position till which all nodes are in the same domain
			int i;
			for (i = currentPos + 1; i < vertexSequence.size(); i++) {
				if (nodeDomainMapping.get(vertexSequence.get(i)).compareTo(currDomainID) != 0) {
					break;
				}
			}
			if (i - 1 > currentPos) {
				ArrayList<String> temp = new ArrayList<String>();
				for (int j = currentPos; j < i; j++) {
					temp.add(vertexSequence.get(j));
				}
				// Get the IP address and port for the domain PCE
				PCEPAddress ipAddress = topologyUpdateServerinfo.get(currDomainID);
				TopologyUpdateDomainClient.reserveCapacity(ipAddress.getIPv4Address(false), ipAddress.getPort(), capacity, temp);
			}
			// If additional nodes exist the next link (i-1) to (i) is a
			// multi-domain link
			if (i < vertexSequence.size()) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(vertexSequence.get(i - 1));
				temp.add(vertexSequence.get(i));
				PCEPAddress ipAddress = topologyUpdateServerinfo.get("PARENT");
				TopologyUpdateParentClient.reserveCapacity(ipAddress.getIPv4Address(false), ipAddress.getPort(), capacity, temp);
			} else
				break;
			currentPos = i;
		} while (true);
	}

	public static void release(double capacity, ArrayList<String> vertexSequence) {
		// Iteratively identify the sequence of nodes in the same domain

		int currentPos = 0;

		do {
			// if (currentPos == vertexSequence.size())
			// break;
			String currDomainID = nodeDomainMapping.get(vertexSequence.get(currentPos));
			// Get the position till which all nodes are in the same domain
			int i;
			for (i = currentPos + 1; i < vertexSequence.size(); i++) {
				if (nodeDomainMapping.get(vertexSequence.get(i)).compareTo(currDomainID) != 0) {
					break;
				}
			}
			if (i - 1 > currentPos) {
				ArrayList<String> temp = new ArrayList<String>();
				for (int j = currentPos; j < i; j++) {
					temp.add(vertexSequence.get(j));
				}
				// Get the IP address and port for the domain PCE
				PCEPAddress ipAddress = topologyUpdateServerinfo.get(currDomainID);
				TopologyUpdateDomainClient.releaseCapacity(ipAddress.getIPv4Address(false), ipAddress.getPort(), capacity, temp);
			}
			// If additional nodes exist the next link (i-1) to (i) is a
			// multi-domain link
			if (i < vertexSequence.size()) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(vertexSequence.get(i - 1));
				temp.add(vertexSequence.get(i));
				PCEPAddress ipAddress = topologyUpdateServerinfo.get("PARENT");
				TopologyUpdateParentClient.releaseCapacity(ipAddress.getIPv4Address(false), ipAddress.getPort(), capacity, temp);
			} else
				break;
			currentPos = i;
		} while (true);
	}

	public static void itReserve(int cpu, int ram, int storage, String itID, boolean domain) {
		System.out.println("Making IT Request!!");
		PCEPAddress ipAddress;
		if (domain) {
			ipAddress = topologyUpdateServerinfo.get(nodeDomainMapping.get(itID));
		} else {
			ipAddress = topologyUpdateServerinfo.get("PARENT");
		}
		TopologyUpdateDomainClient.reserveITResource(ipAddress.getIPv4Address(false), ipAddress.getPort(), cpu, ram, storage, itID);
	}

	public static void itRelease(int cpu, int ram, int storage, String itID, boolean domain) {
		PCEPAddress ipAddress;
		if (domain) {
			ipAddress = topologyUpdateServerinfo.get(nodeDomainMapping.get(itID));
		} else {
			ipAddress = topologyUpdateServerinfo.get("PARENT");
		}
		TopologyUpdateDomainClient.releaseITResource(ipAddress.getIPv4Address(false), ipAddress.getPort(), cpu, ram, storage, itID);
	}

	/**
	 * Function for logging events
	 * 
	 * @param event
	 */
	private static void localLogger(String event) {
		Logger.logSystemEvents("[ReserveRelease]     " + event);
	}

	/**
	 * Function for logging debug information
	 * 
	 * @param event
	 */
	private static void localDebugger(String event) {
		// Logger.debugger("[ReserveRelease]     " + event);
	}

}
