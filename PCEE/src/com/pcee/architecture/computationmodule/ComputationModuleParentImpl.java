/**
 *  This file is part of Path Computation Element Emulator (PCEE).
 *
 *  PCEE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PCEE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PCEE.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pcee.architecture.computationmodule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.graph.elements.vertex.algorithms.VertexAlgorithm;
import com.graph.elements.vertex.algorithms.constraints.VertexConstraint;
import com.graph.elements.vertex.algorithms.constraints.impl.SingleVertexConstraint;
import com.graph.elements.vertex.algorithms.impl.SingleVertexAlgorithmImpl;
import com.graph.graphcontroller.Gcontroller;
import com.graph.path.algorithms.constraints.impl.SimplePathComputationConstraint;
import com.graph.path.algorithms.impl.BandwidthConstrainedPathComputationAlgorithm;
import com.graph.path.algorithms.impl.SimplePathComputationAlgorithm;
import com.pcee.architecture.ModuleEnum;
import com.pcee.architecture.ModuleManagement;
import com.pcee.architecture.computationmodule.ted.TopologyInformationDomain;
import com.pcee.architecture.computationmodule.ted.TopologyInformationParent;
import com.pcee.architecture.computationmodule.threadpool.MultiDomainRequest;
import com.pcee.architecture.computationmodule.threadpool.Request;
import com.pcee.architecture.computationmodule.threadpool.SingleDomainRequest;
import com.pcee.architecture.computationmodule.threadpool.ThreadPool;
import com.pcee.logger.Logger;
import com.pcee.protocol.message.PCEPMessage;
import com.pcee.protocol.message.PCEPMessageFactory;
import com.pcee.protocol.message.objectframe.PCEPObjectFrameFactory;
import com.pcee.protocol.message.objectframe.impl.PCEPExplicitRouteObject;
import com.pcee.protocol.message.objectframe.impl.PCEPITResourceObject;
import com.pcee.protocol.message.objectframe.impl.PCEPNoPathObject;
import com.pcee.protocol.message.objectframe.impl.PCEPRequestParametersObject;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.EROSubobjects;
import com.pcee.protocol.message.objectframe.impl.erosubobjects.PCEPAddress;
import com.pcee.protocol.request.PCEPRequestFrame;
import com.pcee.protocol.request.PCEPRequestFrameFactory;
import com.pcee.protocol.response.PCEPResponseFrame;
import com.pcee.protocol.response.PCEPResponseFrameFactory;

/**
 * 
 * @author Marek Drogon
 * @author Mohit Chamania
 */
public class ComputationModuleParentImpl extends ComputationModule {

	// Management Object used to forward communications between the different
	// modules
	private ModuleManagement lm;

	// Thread Pool Implementation to compute incoming requests
	private ThreadPool threadPool;

	// Used by the ThreadPool class to initialize the given amount of Threads
	private int computationThreads;

	// Thread-safe queue to store requests to be used by the thread pool
	private LinkedBlockingQueue<Request> requestQueue;

	// Object to retrieve current topology information
	private TopologyInformationParent topologyInstance = TopologyInformationParent
			.getInstance();

	// Graph library used for implementing path computation
	private Gcontroller graph;

	//HashMap for keeping track of requests made to remote peers and the associated worker tasks
	private HashMap <String, LinkedBlockingQueue<PCEPMessage>> remotePeerResponseAssociationHashMap;

	/** Constructor with default configurations for the threads etc
	 * 
	 * @param layerManagement
	 */
	public ComputationModuleParentImpl(ModuleManagement layerManagement) {
		lm = layerManagement;
		computationThreads = 5;
		start();
	}

	/** Constructor
	 * 
	 * @param layerManagement
	 * @param computationThreads
	 */
	public ComputationModuleParentImpl(ModuleManagement layerManagement, int computationThreads) {
		lm = layerManagement;
		this.computationThreads = computationThreads;
		start();
	}

	public void stop() {
		threadPool.stop();
		requestQueue.clear();
	}

	public void start() {
		//Innitialize the map that will record the responses coming from remote peers
		remotePeerResponseAssociationHashMap = new HashMap<String, LinkedBlockingQueue<PCEPMessage>>();
		// Get the current Graph Instance
		graph = topologyInstance.getGraph();
		// Initialize a new request Queue
		requestQueue = new LinkedBlockingQueue<Request>();
		// Initialize the thread pool used for computing requests
		threadPool = new ThreadPool(lm, computationThreads, requestQueue);
	}

	public void closeConnection(PCEPAddress address) {
		// TODO Auto-generated method stub
		// Explicit removal of requests from closed connections not implemented
	}

	public void registerConnection(PCEPAddress address, boolean connected,
			boolean connectionInitialized, boolean forceClient) {
		// TODO Auto-generated method stub
		// Explicit registration of connections not implemented

	}

	public void receiveMessage(PCEPMessage message, ModuleEnum sourceLayer) {
		localDebugger("Entering: receiveMessage(PCEPMessage message)");
		localDebugger("| message: " + message.contentInformation());
		localDebugger("| sourceLayer: " + sourceLayer);
		switch (sourceLayer) {
		case SESSION_MODULE:
			//If message is a path computation request process message 
			if (message.getMessageHeader().getTypeDecimalValue()==3)
				computeRequest(message);
			else if (message.getMessageHeader().getTypeDecimalValue()==4) 
				//Path computation response received from another PCE server /// needs to be sent to a worker in the computataion module
				processResponseFromRemotePeer(message);
			break;
		default:
			localLogger("Error in receiveMessage(PCEPMessage message, LayerEnum targetLayer)");
			localLogger("Wrong sourceLayer");
			break;
		}

	}

	private String getKeyForRemotePeerAssociation(PCEPAddress address, String requestID) {
		return address.getIPv4Address(true) + "-" + requestID;
	}

	public synchronized boolean  isValidRequestToRemotePeer(PCEPAddress address, String requestID){
		//If the particular combination of remote PCE peer and request ID already exist do not make a new association
		String key = getKeyForRemotePeerAssociation(address, requestID);
		if (remotePeerResponseAssociationHashMap.containsKey(key)) 
			return false;
		return true;
	}

	public synchronized void registerRequestToRemotePeer(PCEPAddress address, String requestID, LinkedBlockingQueue<PCEPMessage> queue){
		if (isValidRequestToRemotePeer(address, requestID)) {
			String key = getKeyForRemotePeerAssociation(address, requestID);
			remotePeerResponseAssociationHashMap.put(key, queue);
		}
		else
			localLogger("registerRequestToRemotePeer: Not a valid request");
	}

	//Function to implement a mechanism where a response from another server (hierarchical or PCE peer) is sent to the correct worker task
	protected synchronized void processResponseFromRemotePeer(PCEPMessage message) {
		PCEPAddress address = message.getAddress();
		//Message is of type PCEP Response
		PCEPResponseFrame responseFrame = PCEPResponseFrameFactory
				.getPathComputationResponseFrame(message);
		String requestID = Integer.toString(responseFrame.getRequestID());
		String key = getKeyForRemotePeerAssociation(address, requestID);
		if (remotePeerResponseAssociationHashMap.containsKey(key)) {
			localLogger("Path Computation Response Received by the computation Module, adding to queue from worker task");
			remotePeerResponseAssociationHashMap.get(key).add(message);
			remotePeerResponseAssociationHashMap.remove(key);
		} else {
			localLogger("Response for Peer-requestID combnation that is not registered with the computation module");
		}


	}

	public void sendMessage(PCEPMessage message, ModuleEnum targetLayer) {
		switch (targetLayer) {
		case NETWORK_MODULE:
			// undefined
			break;
		case SESSION_MODULE:
			lm.getSessionModule().receiveMessage(message,
					ModuleEnum.COMPUTATION_MODULE);
			break;
		case COMPUTATION_MODULE:
			// undefined
			break;
		case CLIENT_MODULE:
			// Not possible
			break;
		default:
			localLogger("Error in sendMessage(PCEPMessage message, LayerEnum targetLayer)");
			localLogger("Wrong target Layer");
			break;
		}

	}

	/**
	 * Function to create the Request Object used by the thread pool to perform
	 * path computation
	 * 
	 * @param message
	 */
	private void computeRequest(PCEPMessage message) {
		localDebugger("Entering: computeRequest(PCEPMessage message)");
		try {
			
			Request req = new MultiDomainRequest();
			
			// Extract Request Frame from the incoming message
			PCEPRequestFrame requestFrame = PCEPRequestFrameFactory
					.getPathComputationRequestFrame(message);
			PCEPAddress address = message.getAddress();

			// Extract request parameters to be used for computing the path
			String requestID = Long.toString(requestFrame.getRequestID());
			String source = requestFrame.getSourceAddress().getIPv4Address(
					false);
			String destination = requestFrame.getDestinationAddress()
					.getIPv4Address(false);
			double bandwidth = -1;
			if (requestFrame.containsBandwidthObject()) {
				bandwidth = requestFrame.extractBandwidthObject().getBandwidthFloatValue();
			}
			
			PCEPITResourceObject it = null;
			if (requestFrame.containsITResourceObject()) {
				it = requestFrame.extractITResourceObject();
			}
			
			// Check if IT resource parameters included, if yes, it is an IT request
			// provide the request with IT constraints and algorithm
			if (it !=null ){
				req.setITRequest(true);
				// Creating VertexConstraint for IT searching request
				VertexConstraint vConstraints = new SingleVertexConstraint();
				vConstraints.setCPU(it.getCpuDecimalValue());
				vConstraints.setRAM(it.getRamDecimalValue());
				vConstraints.setSTORAGE(it.getStorageDecimalValue());
				req.setVConstraints(vConstraints);
				
				// Creating VertexAlgorithm for IT searching request
				VertexAlgorithm vAlgo = new SingleVertexAlgorithmImpl();
				req.setVAlgorithm(vAlgo);
			}
			
			req.setRequestID(requestID);
			req.setAddress(address);

			//Set the source and destination IP addresses
			req.setSourceRouterIP(source.trim());
			req.setDestRouterIP(destination.trim());
			if (bandwidth < 0) {
				req.setBandwidth(0);
				req.setAlgo(new SimplePathComputationAlgorithm());
			} else {
				req.setBandwidth(bandwidth);
				req.setAlgo(new BandwidthConstrainedPathComputationAlgorithm());
			}
			localLogger("Adding Request for multi-domain path computation with ID " + requestID + " to the Queue");
			requestQueue.add(req);



		} catch (NullPointerException e) {
			localDebugger("Problem in generating request. Please check request parameters");
		} 
	}


	/**
	 * Function to log events in the Computation layer
	 * 
	 * @param event
	 */
	private void localLogger(String event) {
		Logger.logSystemEvents("[MessageHandler] " + event);
	}

	/**
	 * Function to log debugging information in the computation layer
	 * 
	 * @param event
	 */
	private void localDebugger(String event) {
		Logger.debugger("[MessageHandler] " + event);
	}

}
