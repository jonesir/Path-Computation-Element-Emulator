package com.pcee.architecture.computationmodule.threadpool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.BasicEdgeParams;
import com.graph.elements.vertex.params.ITResourceVertexParams;
import com.graph.graphcontroller.Gcontroller;
import com.pcee.architecture.computationmodule.ted.TopologyInformationDomain;
import com.pcee.architecture.computationmodule.ted.TopologyUpdateListener;
import com.pcee.logger.Logger;

/**
 * Reserve and Release request processing class
 * 
 * @author Yuesheng Zhong
 * 
 */
public class HierarchicalSocketProcessingDomain extends Thread {

	Socket socket;
	String message;
	BufferedWriter parentWriter;

	/**
	 * @param socket
	 */
	public HierarchicalSocketProcessingDomain(Socket socket) {
		this.socket = socket;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		// Do processing for Socket
		localDebugger("inside run()");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			while (true) {
				message = in.readLine();
				if (message != null) {
					System.out.println("Processing: " + message);
					String[] arr = parseInformation(message);
					extractInformation(arr);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param message
	 * @return
	 */
	private String[] parseInformation(String message) {
		String[] arr = message.split(":");

		return arr;
	}

	/**
	 * @param arr
	 */
	private void extractInformation(String[] arr) {
		String situation = arr[0];
		System.out.println("Situation = " + situation);
		Gcontroller graph = null;
		synchronized (TopologyInformationDomain.getInstance().getGraph()) {
			if (situation.equals("RESERVE")) {
				reserveCapacity(arr[1], arr[2], arr[3]);
			} else if (situation.equals("RESERVE")) {
				releaseCapacity(arr[1], arr[2], arr[3]);
			} else if (situation.equals("GET")) {
				localLogger("Capacity of link from " + arr[1] + " to " + arr[2] + "=" + getCapacity(arr[1], arr[2]));
			} else if (situation.equals("CREATE")) {
				createLink(arr[1], arr[2], arr[3]);
			} else if (situation.equals("VRESERVE")) {
				reserveVertex(arr[1], Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), Integer.parseInt(arr[4]));
			} else if (situation.equals("VRELEASE")) {
				releaseVertex(arr[1], Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), Integer.parseInt(arr[4]));
			} else if (situation.equals("FINISHED")) {
				Logger.logQueuingAndComputationTime();
			} else {
				System.out.println("Wrong Input Parameter in ServerUpdateListener.extractInformation()");
				System.exit(0);
			}
		}
	}

	/**
	 * @param sourceVector
	 * @param destVector
	 * @param bw
	 */
	private void reserveCapacity(String sourceVector, String destVector, String bw) {
		localLogger("Entering reserveCapacity(...)");
		localLogger(sourceVector + " " + destVector + " " + bw);
		System.out.println("sourceVector: " + sourceVector);
		System.out.println("destVector: " + destVector);
		String[] sourceDomains = sourceVector.split("\\.");
		System.out.println(sourceDomains.length + " domains");
		String[] destDomains = destVector.split("\\.");
		// ------------------------UNIFIED VERSION------------------------------

		// source and destination belongs to the same intra domain
		if (TopologyInformationDomain.getInstance().getGraph().vertexExists(sourceVector) && TopologyInformationDomain.getInstance().getGraph().vertexExists(destVector)) {
			if (TopologyInformationDomain.getInstance().getGraph().aConnectingEdge(sourceVector, destVector)) {
				// reserve locally
				TopologyInformationDomain.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().reserveCapacity(Double.valueOf(bw));
				localDebugger(sourceVector + " - " + destVector + " with " + bw + " reserved");
				localDebugger(sourceVector + " - " + destVector + " still " + TopologyInformationDomain.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().getAvailableCapacity() + " bandwidth available");
				// update parent PCE
				updateParentCapacity();
			}
		} else {// if source or destination not all wihtin domain, then
				// reserve capacity on parent PCE
			reserveCapacityOnParent(sourceVector, destVector, bw);
		}
	}

	/**
	 * @param sourceVector
	 * @param destVector
	 * @param bw
	 */
	private void releaseCapacity(String sourceVector, String destVector, String bw) {
		localLogger("releaseCapacity(...)");
		localLogger(sourceVector + " " + destVector + " " + bw);
		// ------------------------UNIFIED VERSION------------------------------
		if (TopologyInformationDomain.getInstance().getGraph().vertexExists(sourceVector) && TopologyInformationDomain.getInstance().getGraph().vertexExists(destVector)) {
			if (TopologyInformationDomain.getInstance().getGraph().aConnectingEdge(sourceVector, destVector)) {
				// reserve locally
				TopologyInformationDomain.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().releaseCapacity(Double.valueOf(bw));
				localDebugger(sourceVector + " - " + destVector + " with " + bw + " reserved");
				localDebugger(sourceVector + " - " + destVector + " still " + TopologyInformationDomain.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().getAvailableCapacity() + " bandwidth available");
				// update parent PCE
				updateParentCapacity();
			}
		} else {// if source or destination not all wihtin domain, then
				// reserve capacity on parent PCE
			releaseCapacityOnParent(sourceVector, destVector, bw);
		}
	}

	/**
	 * @param edgeID
	 * @param bw
	 */
	private void updateParentEdge(String edgeID, String bw) {
		// if (TopologyInformation.getInstance().getGraph().getEdge(edgeID) !=
		// null) {
		// ((BasicEdgeParams)
		// TopologyInformation.getInstance().getGraph().getEdge(edgeID).getEdgeParams()).setAvailableCapacity(Double.valueOf(bw));
		//
		// } else {
		// System.out.println("edge id : " + edgeID + " doesn't exist!!!");
		// }

	}

	/**
	 * @param source
	 * @param destination
	 * @param bw
	 */
	private void reserveCapacityOnParent(String source, String destination, String bw) {
		doSend(TopologyUpdateListener.getParentWriter(), "RESERVE:" + source + ":" + destination + ":" + bw + "\n");
	}

	/**
	 * @param source
	 * @param destination
	 * @param bw
	 */
	private void releaseCapacityOnParent(String source, String destination, String bw) {
		doSend(TopologyUpdateListener.getParentWriter(), "RELEASE:" + source + ":" + destination + ":" + bw + "\n");
	}

	/**
	 * 
	 */
	private void updateParentCapacity() {
		Set<String> idSet = TopologyInformationDomain.getInstance().getVirtualGraph().getEdgeIDSet();
		for (String edgeID : idSet) {
//			doSend(TopologyUpdateListener.getParentWriter(), "UPDATE:" + edgeID + ":" + ((PathEdgeParams) TopologyInformation.getInstance().getVirtualGraph().getEdge(edgeID).getEdgeParams()).getAvailableCapacity() + "\n");
		}
	}

	/**
	 * @param vertexID
	 * @param cpu
	 * @param ram
	 * @param storage
	 */
	private void updateParentNode(String vertexID, String cpu, String ram, String storage) {
		// if (TopologyInformation.getInstance().getGraph().getVertex(vertexID)
		// != null) {
		// ((ITResourceVertexParams)
		// TopologyInformation.getInstance().getGraph().getVertex(TopologyInformation.getInstance().getGraph().getVertex(vertexID).getITNodeVertexID()).getVertexParams()).setAvailableITResource(Integer.parseInt(cpu),
		// Integer.parseInt(ram), Integer.parseInt(storage));
		// } else {
		// System.out.println("IT Node " + vertexID +
		// " on parent does not exist!!!");
		// }
	}

	private void doSend(BufferedWriter writer, String message) {

		try {
			writer.write(message);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param vertexID
	 * @param cpu
	 * @param ram
	 * @param storage
	 */
	private void reserveVertex(String vertexID, int cpu, int ram, int storage) {
		localLogger("reserveVertex(...)");
		localLogger(vertexID + " CPU-" + cpu + " , RAM-" + ram + " , STORAGE-" + storage);

		// -------------------UNIFIED VERSION---------------------------
		ITResourceVertexParams params = null;
		if (TopologyInformationDomain.getInstance().getVirtualGraph().getVertex(vertexID) != null && TopologyInformationDomain.getInstance().getVirtualGraph().getVertex(vertexID).isITNode()) {
			// reserve IT within domain
			params = ((ITResourceVertexParams) TopologyInformationDomain.getInstance().getGraph().getVertex(vertexID).getVertexParams());
			params.reserveITResource(cpu, ram, storage);
			localLogger("Updating IT Resource of Vertex");
			localLogger("Available IT Resource in Vertex " + vertexID + " is: \n\t CPU-" + params.getAvailableCPU() + " \n\t RAM-" + params.getAvailableRAM() + " \n\t STORAGE-" + params.getAvailableSTORAGE());
			localLogger("Total IT Resource in Vertex " + vertexID + " is: \n\t CPU-" + params.getCpu() + " \n\t RAM-" + params.getRam() + " \n\t STORAGE-" + params.getStorage());

			// update IT on parent
			updateParentVertex(vertexID, params.getAvailableCPU(), params.getAvailableRAM(), params.getAvailableSTORAGE());
		} else {// IT node is not within domain
			reserveVertexOnParent(vertexID, cpu, ram, storage);
		}
	}

	/**
	 * @param vertexID
	 * @param cpu
	 * @param ram
	 * @param storage
	 */
	private void releaseVertex(String vertexID, int cpu, int ram, int storage) {
		localLogger("reserveVertex(...)");
		localLogger(vertexID + " CPU-" + cpu + " , RAM-" + ram + " , STORAGE-" + storage);

		// -------------------UNIFIED VERSION---------------------------
		ITResourceVertexParams params = null;
		if (TopologyInformationDomain.getInstance().getVirtualGraph().getVertex(vertexID) != null) {
			// reserve IT within domain
			params = ((ITResourceVertexParams) TopologyInformationDomain.getInstance().getGraph().getVertex(vertexID).getVertexParams());
			params.releaseITResource(cpu, ram, storage);
			localLogger("Updating IT Resource of Vertex");
			localLogger("Available IT Resource in Vertex " + vertexID + " is: \n\t CPU-" + params.getAvailableCPU() + " \n\t RAM-" + params.getAvailableRAM() + " \n\t STORAGE-" + params.getAvailableSTORAGE());
			localLogger("Total IT Resource in Vertex " + vertexID + " is: \n\t CPU-" + params.getCpu() + " \n\t RAM-" + params.getRam() + " \n\t STORAGE-" + params.getStorage());

			// update IT on parent
			updateParentVertex(vertexID, params.getAvailableCPU(), params.getAvailableRAM(), params.getAvailableSTORAGE());
		} else {// IT node is not within domain
			releaseVertexOnParent(vertexID, cpu, ram, storage);
		}
	}

	/**
	 * @param vertexID
	 * @param cpu
	 * @param ram
	 * @param storage
	 */
	private void reserveVertexOnParent(String vertexID, int cpu, int ram, int storage) {
		doSend(TopologyUpdateListener.getParentWriter(), "VRESERVE:" + vertexID + ":" + cpu + ":" + ram + ":" + storage + "\n");
	}

	/**
	 * @param vertexID
	 * @param cpu
	 * @param ram
	 * @param storage
	 */
	private void releaseVertexOnParent(String vertexID, int cpu, int ram, int storage) {
		doSend(TopologyUpdateListener.getParentWriter(), "VRELEASE:" + vertexID + ":" + cpu + ":" + ram + ":" + storage + "\n");
	}

	/**
	 * @param vertexID
	 * @param cpu
	 * @param ram
	 * @param storage
	 */
	private void updateParentVertex(String vertexID, int cpu, int ram, int storage) {
		doSend(TopologyUpdateListener.getParentWriter(), "UPDATE:" + vertexID + ":" + cpu + ":" + ram + ":" + storage + "\n");
	}

	/**
	 * @param sourceVector
	 * @param destVector
	 * @return
	 */
	private double getCapacity(String sourceVector, String destVector) {
		localLogger("getCapacity(...)");
		localLogger(sourceVector + " " + destVector);
		if (TopologyInformationDomain.getInstance().getGraph().aConnectingEdge(sourceVector, destVector)) {
			localDebugger("Updating Capacity of link");
			return TopologyInformationDomain.getInstance().getGraph().getConnectingEdge(sourceVector, destVector).getEdgeParams().getAvailableCapacity();
		} else {
			localLogger("Link not found");
			return -1;
		}
	}

	/**
	 * @param sourceVector
	 * @param destVector
	 * @param bw
	 */
	private void createLink(String sourceVector, String destVector, String bw) {
		localLogger("createLink(...)");
		localLogger(sourceVector + " " + destVector + " " + bw);
		Gcontroller graph = TopologyInformationDomain.getInstance().getGraph();
		if (graph.aConnectingEdge(sourceVector, destVector) == true) {
			// double maxCap = graph.getConnectingEdge(sourceVector,
			// destVector).getEdgeParams().getMaxCapacity();
			// double availableCap = graph.getConnectingEdge(sourceVector,
			// destVector).getEdgeParams().getAvailableCapacity();
			// graph.getConnectingEdge(sourceVector,
			// destVector).getEdgeParams().setMaxCapacity(maxCap +
			// Double.parseDouble(bw));
			// graph.getConnectingEdge(sourceVector,
			// destVector).getEdgeParams().setAvailableCapacity(availableCap +
			// Double.parseDouble(bw));
		} else {
			EdgeElement edge = new EdgeElement(sourceVector + "-" + destVector, graph.getVertex(sourceVector), graph.getVertex(destVector), graph);
			EdgeParams params = new BasicEdgeParams(edge, 1, 1, Double.valueOf(bw));
			edge.setEdgeParams(params);
			TopologyInformationDomain.getInstance().getGraph().addEdge(edge);
		}

		int p = ((ITResourceVertexParams) TopologyInformationDomain.getInstance().getGraph().getVertex(sourceVector).getVertexParams()).getFreePorts();
		((ITResourceVertexParams) TopologyInformationDomain.getInstance().getGraph().getVertex(sourceVector).getVertexParams()).setFreePorts(p - 1);

		p = ((ITResourceVertexParams) TopologyInformationDomain.getInstance().getGraph().getVertex(destVector).getVertexParams()).getFreePorts();
		((ITResourceVertexParams) TopologyInformationDomain.getInstance().getGraph().getVertex(destVector).getVertexParams()).setFreePorts(p - 1);
	}

	/**
	 * @param message
	 * @return
	 */
	public static String[] printMessage(String message) {
		String[] arr = message.split(":");

		return arr;
	}

	// /**
	// *
	// */
	// public static void checkPolicy() {
	// Gcontroller graph = TopologyInformation.getInstance().getGraph();
	// Iterator<EdgeElement> iter = graph.getEdgeSet().iterator();
	// while (iter.hasNext()) {
	// EdgeElement temp = iter.next();
	// if (temp.getEdgeParams().getMaxCapacity() > 10) {
	// if (temp.getEdgeParams().getAvailableCapacity() >= 10) {
	// new TEDecommissionClientLauncher(temp.getSourceVertex().getVertexID(),
	// temp.getDestinationVertex().getVertexID());
	//
	// }
	// }
	// }
	// }

	/**
	 * Put all kinds of snapshots into snapshot buffer
	 * 
	 * Format of snapshotString EDGE:[EdgeID]:[bw]
	 * VERTEX:[VERTEXID]:[CPU]:[RAM]:[STORAGE]
	 * 
	 * @param snapshotString
	 */
	// private static void logSnapshots(String snapshotString) {
	// String[] arr = snapshotString.split(":");
	//
	// ArrayList<String> snapshot = new ArrayList<String>();// create a new
	// // snapshot to
	// // be inserted
	// // to snapshot
	// // buffer
	//
	// int ID;
	// if (GlobalCfg.IDMAP.keySet().contains(arr[1])) { // get the ID of type
	// // with typeid
	// ID = GlobalCfg.IDMAP.get(arr[1]) + 1;
	// } else {
	// GlobalCfg.IDMAP.put(arr[1], 0);
	// ID = 0;
	// }
	//
	// snapshot.add(arr[0]); // TYPE, here is VERTEX or EDGE
	// snapshot.add(arr[1]); // TYPEID, here is VERTEXID or EDGEID
	// snapshot.add(Integer.toString(ID)); // ID, here is ID
	//
	// if (arr[0].equals("VERTEX")) { // VERTEX has the format of 'VERTEX
	// // VERTEXID ID CPU RAM STORAGE'
	// snapshot.add(arr[2]); // CPU
	// snapshot.add(arr[3]); // RAM
	// snapshot.add(arr[4]); // STORAGE
	// } else { // EDGE has the format of 'EDGE EDGEID ID BW
	// snapshot.add(arr[2]); // BW
	// }
	//
	// GlobalCfg.snapshotBuffer.add(snapshot);
	// }

	/**
	 * @param event
	 */
	private static void localLogger(String event) {
		// Logger.logSystemEvents("[TopologyUpdateListener]     " + event);
		System.out.println("[TopologyUpdateListener]     " + event);
	}

	/**
	 * Function to log debugging events
	 * 
	 * @param event
	 */
	private static void localDebugger(String event) {
		Logger.debugger("[SocketProcessing]     " + event);
	}

	public static void main(String[] args) {
		ArrayList<String> s = new ArrayList<String>();
		s.add("111111");
		s.add("222222");
		ArrayList<String> s1 = s;
		System.out.println("s1 = " + s1.toString());
		s = new ArrayList<String>();
		s.add("333");
		s.add("444");
		System.out.println("s12 = " + s1.toString());
	}
}
