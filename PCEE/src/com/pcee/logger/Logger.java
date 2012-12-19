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

package com.pcee.logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.graph.graphcontroller.Gcontroller;
import com.pcee.common.Time;
import com.pcee.logger.logObjectImpl.ConsoleLogObject;

public class Logger {

	public static LogObject logObject = new ConsoleLogObject(); // Default
	// Logging
	// Functionality
	public static boolean logging = true;
	public static boolean debugging;
	public static int counter = 1;

	public static int bw = 4;
	public static int total = 0;
	public static int blocked = 0;
	public static int requestCount = 0;
	public static int requestDelay = 500;
	public static long startTime;
	public static int duration = 30;

	public static ArrayList<Long> timeStampsSentMilli = new ArrayList<Long>();
	public static ArrayList<Long> timeStampsSentNano = new ArrayList<Long>();
	public static ArrayList<Long> timeStampsReceivedMilli = new ArrayList<Long>();
	public static ArrayList<Long> timeStampsReceivedNano = new ArrayList<Long>();

	public static ArrayList<Long> requestEnterTheQueue = new ArrayList<Long>();
	public static ArrayList<Long> requestLeaveTheQueue = new ArrayList<Long>();

	public static ArrayList<Long> enterTheComputation = new ArrayList<Long>();
	public static ArrayList<Long> leaveTheComputation = new ArrayList<Long>();

	public static void setLogObject(LogObject object) {
		Logger.logObject = object;
	}

	public static void logWarning(String msg) {
		// System.out.println("| WARNING | " + msg);
	}

	public static void logConnectionEstablishment(String msg) {
		// System.out.println(" | Connection Establishment | " + msg);
	}

	public static void logError(String msg) {
		// System.out.println(msg);
	}

	public static void logGUINotifications(String msg) {
		if (logging) {
			String timeStamp = Time.timeStamp();
			System.out.println(timeStamp + " " + msg);
		}
	}

	public static synchronized void logSystemEvents(String msg) {
		if (logging) {
			String timeStamp = Time.timeStamp();
			System.out.println(timeStamp + " " + msg);
		}
	}

	public static void debugger(String msg) {
		if (debugging) {
			String timeStamp = Time.timeStamp();
			System.out.println(timeStamp + " " + msg);
		}
	}

	public static void logBlockingRate(int total, int blocked, double blockRate) {
		String s = "";
		// s +=
		// "===================================================================\n";
		// s += "\t\t\t\t BLOCKING RATE\n";
		// s += "\t Logging count:\t" + (Logger.counter++) + "\n";
		// s += "\t Total requests:\t" + total + " , \t Total blocks: " +
		// blocked + "\n";
		// s += "\t Blocking Rate:\t" + blockRate*100 + "%\n";
		s += "Total_Request\t" + total + "\n";
		s += "Total_Blocks\t" + blocked + "\n";
		s += "Blocking_Rate\t" + blockRate * 100 + "%\n";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true));
			writer.write(s);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void logTimeStamps() {

		int stampsCount = timeStampsSentMilli.size();

		long averageMilli;
		long averageNano;

		long tempMilli = 0;
		long tempNano = 0;
		for (int i = 0; i < stampsCount; i++) {
			tempMilli += timeStampsReceivedMilli.get(i) - timeStampsSentMilli.get(i);
			tempNano += timeStampsReceivedNano.get(i) - timeStampsSentNano.get(i);
		}

		averageMilli = tempMilli / stampsCount;
		averageNano = tempNano / stampsCount;

		String s = "";
		// s +=
		// "*******************************************************************\n";
		// s += "\t\t\t Signaling Time of Getting !!!PATH!!!\n";
		// s += "\t Average signaling delay in Milliseconds :\t" + averageMilli
		// + "\n";
		// s += "\t Average singaling delay in Nanosecondes:\t" + averageNano +
		// "\n";
		// s +=
		// "*******************************************************************\n";
		s += "Average_Signaling_Delay_of_Getting_PATH_in_Milliseconds\t" + averageMilli + "\n";
		s += "Average_Signaling_Delay_of_Getting_PATH_in_Nanoseconds\t" + averageNano + "\n\n";
		s += "================================================================\n\n";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true));
			writer.write(s);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("after logfile.txt");
			e.printStackTrace();
		}
	}

	public static void logTimeStampsVertex(ArrayList<Long> stampsSentMilli, ArrayList<Long> stampsReceivedMilli, ArrayList<Long> stampsSentNano, ArrayList<Long> stampsReceivedNano) {

		int stampsCount = stampsSentMilli.size();

		long averageMilli;
		long averageNano;

		long tempMilli = 0;
		long tempNano = 0;
		for (int i = 0; i < stampsCount; i++) {
			tempMilli += stampsReceivedMilli.get(i) - stampsSentMilli.get(i);
			tempNano += stampsReceivedNano.get(i) - stampsSentNano.get(i);
		}

		averageMilli = tempMilli / stampsCount;
		averageNano = tempNano / stampsCount;

		String s = "";
		// s += "\t\t\t Signaling Time of Getting |||VERTEX|||\n";
		// s += "\t Average signaling delay in Milliseconds :\t" + averageMilli
		// + "\n";
		// s += "\t Average singaling delay in Nanosecondes:\t" + averageNano +
		// "\n";
		// s +=
		// "*******************************************************************\n";
		s += "Average_Signaling_Delay_in_Milliseconds\t" + averageMilli + "\n";
		s += "Average_Signaling_Delay_in_Nanoseconds\t" + averageNano + "\n\n";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true));
			writer.write(s);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("after logfile.txt");
			e.printStackTrace();
		}
	}

	public static void logQueuingAndComputationTime() {

		Iterator<Long> iter1 = requestEnterTheQueue.iterator();
		Iterator<Long> iter2 = requestLeaveTheQueue.iterator();
		Long totalQueuingTime = (long) 0;
		Long averageQueuingTime = (long) 0;
		while (iter1.hasNext()) {
			totalQueuingTime += (iter2.next() - iter1.next());
		}
		averageQueuingTime = totalQueuingTime / requestEnterTheQueue.size();

		iter1 = enterTheComputation.iterator();
		iter2 = leaveTheComputation.iterator();
		Long totalComputationTime = (long) 0;
		Long averageComputationTime = (long) 0;
		while (iter1.hasNext()) {
			totalComputationTime += (iter2.next() - iter1.next());
		}
		averageComputationTime = totalComputationTime / enterTheComputation.size();

		String s = "";
		// s += "\t Single Path Count :\t" + ClientTest.singlePath +
		// "\t\t Total Count : \t\t" + ClientTest.total + " \n";
		// s += "\t Single Path Ratio :\t" +
		// (((double)ClientTest.singlePath)/ClientTest.total)*100 +
		// "% \t\t 1+1 Ratio : \t\t" +
		// (((double)(ClientTest.total-ClientTest.singlePath))/ClientTest.total)*100+"%\n";
		// s += "\t Nr Connection:     \t" + GlobalCfg.endTime +
		// "\t\t Queuing Time : \t" + averageQueuingTime +
		// "\n \t Computation Time : \t" + averageComputationTime + "\n";
		// s +=
		// "*******************************************************************\n\n\n";
		s += "Average_Request_Queuing_Time_On_Server_Side_Before_Been_Handled_In_Nanoseconds\t" + averageQueuingTime + "\n";
		s += "Average_Path_Computation_Time_On_Server_Side_In_Nanoseconds\t" + averageComputationTime + "\n\n";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true));
			writer.write(s);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("after logfile.txt");
			e.printStackTrace();
		}
	}

	public static void logResult(String result1, String result2) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true));
			writer.write(result1);
			writer.write(result2);
			writer.flush();
			writer.close();
			System.out.println("DONE !");
		} catch (IOException e) {
			System.out.println("after logfile.txt");
			e.printStackTrace();
		}
	}

	public static void reset() {
		total = 0;
		blocked = 0;
		startTime = System.currentTimeMillis();
		requestCount = 0;
		timeStampsReceivedMilli.clear();
		timeStampsSentMilli.clear();
		timeStampsReceivedNano.clear();
		timeStampsSentNano.clear();

	}

	public static void graphSnapshot(Gcontroller graph, String className) {

		if (debugging) {
			String updateString = "";
			updateString += "############### Time      : " + Time.timeStamp() + " ###############\n";
			updateString += "############### className : " + className + " ###\n\n";
			Set<String> edgeIDSet = graph.getEdgeIDSet();
			for (String edgeID : edgeIDSet)
				updateString += (edgeID + "\t : Bandwidth=" + graph.getEdge(edgeID).getEdgeParams().getAvailableCapacity()) + " , Delay=" + graph.getEdge(edgeID).getEdgeParams().getDelay() + "\n";
			updateString += "\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * \n";
			updateString += "##########################################################\n\n\n";
			//		try {
			//			BufferedWriter writer = new BufferedWriter(new FileWriter("graphSnapshots.txt",true));
			//			writer.write(updateString);
			//			writer.flush();
			//			writer.close();
			//		} catch (IOException e) {
			//			e.printStackTrace();
			//		}
			System.out.println(updateString);
		}
	}

}