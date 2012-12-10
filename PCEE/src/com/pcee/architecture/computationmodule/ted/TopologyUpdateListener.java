package com.pcee.architecture.computationmodule.ted;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.pcee.architecture.ModuleManagement;
import com.pcee.logger.Logger;
import com.pcee.architecture.computationmodule.threadpool.HierarchicalSocketProcessingDomain;
import com.pcee.architecture.computationmodule.threadpool.HierarchicalSocketProcessingParent;

public class TopologyUpdateListener extends Thread {

	private static ModuleManagement lm;
	private int rrPort = 0;
	private static String role = "";
	private static BufferedWriter writer = null;

	public TopologyUpdateListener(ModuleManagement lm, String role, int rrPort) {
		TopologyUpdateListener.lm = lm;
		this.rrPort = rrPort;
		this.role = role;
	}

	public void run() {
		ServerSocket server;
		try {
			server = new ServerSocket(rrPort);

			localLogger("server socket port : " + server.getLocalPort());

			localLogger("Listening fo topology updates");
			if(this.role.equalsIgnoreCase("domain")){
				Socket ps = new Socket("127.0.0.1", 4289);
				this.writer = new BufferedWriter(new OutputStreamWriter(ps.getOutputStream()));
			}

			while (true) {
				Socket s = server.accept();
				localLogger("incoming message!");
				if (this.role.equalsIgnoreCase("domain")) {
					HierarchicalSocketProcessingDomain socketWorker = new HierarchicalSocketProcessingDomain(s);
					socketWorker.start();
				} else {
					HierarchicalSocketProcessingParent socketWorker = new HierarchicalSocketProcessingParent(s);
					socketWorker.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	public static BufferedWriter getParentWriter() {
		if(TopologyUpdateListener.role.equalsIgnoreCase("domain")){
			if (TopologyUpdateListener.writer == null){
				Socket ps;
				try {
					ps = new Socket("127.0.0.1", 4289);
					TopologyUpdateListener.writer = new BufferedWriter(new OutputStreamWriter(ps.getOutputStream()));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return TopologyUpdateListener.writer;
		}
		return null;
	}

	private static void localLogger(String event) {
		// Logger.logSystemEvents("[TopologyUpdateListener]     " + event);
		System.out.println("[TopologyUpdateListener]      " + event);
	}

	/**
	 * Function to log debugging events
	 * 
	 * @param event
	 */
	private static void localDebugger(String event) {
		// Logger.debugger("[TopologyUpdateListener]     " + event);
	}

}
