package com.pcee.client.connectionsource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

import com.graph.elements.edge.EdgeElement;
import com.graph.graphcontroller.Gcontroller;
import com.pcee.client.event.eventhandler.EventHandler;

public abstract class Source {
	int ID = 0;
	int bwLow;
	int bwHigh;

	double bw;
	int pathCount;
	int cpu, ram, storage;
	float delay;
	int totConnections = 0;
	int blockedConnections = 0;

	String nextID;

	protected Gcontroller controller;

	protected String logFile;

	public abstract void initSource();

	public abstract void nextRequest(boolean currentResvStatus);
	
	public abstract void connectionBlocked();

	public abstract void connectionReserved();

	public abstract int getTotConnections();

	public abstract int getBlockedConnections();

	public abstract double getBlockingProbability();

	public Gcontroller getGraphController() {
		return controller;
	}

	public String getLogFilePath() {
		return logFile;
	}

	public void logGraphState() {
		try {
			if (EventHandler.getTime() > 3000) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
				writer.write("Time = " + EventHandler.getTime() + "\n");
				Iterator<EdgeElement> iter = controller.getEdgeSet().iterator();
				double[] average = new double[3];
				while (iter.hasNext()) {
					EdgeElement temp = iter.next();
					int[] vals = this.getLinkStats(temp);
					for (int i = 0; i < 3; i++)
						average[i] += vals[i];
				}
				String temp = "";
				for (int i = 0; i < 3; i++) {
					average[i] = average[i] / controller.getEdgeSet().size();
					temp = temp + Double.toString(average[i]) + "\t";
				}
				writer.write(temp.substring(0, temp.length() - 1) + "\n");
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Array with link usage [0] is reserved , [1] is GB and [2] is free
	private int[] getLinkStats(EdgeElement temp) {
		int[] out = new int[3];
		for (int i = 0; i < temp.getEdgeParams().getMaxCarriers(); i++) {
			if (temp.getEdgeParams().getCarrierUsage(i) == 1) {
				out[0]++;
			} else {
				int flag = 0;
				for (int j = Math.max(i - 2, 0); j <= Math.min(i + 2, temp.getEdgeParams().getMaxCarriers() - 1); j++) {
					if (temp.getEdgeParams().getCarrierUsage(j) == 1) {
						flag = 1;
						break;
					}
				}
				if (flag == 1) {
					out[1]++;
				} else
					out[2]++;
			}
		}

		return out;

	}
}
