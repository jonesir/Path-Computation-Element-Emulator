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

package com.graph.topology.importers.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.BasicEdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.elements.vertex.params.BasicVertexParams;
import com.graph.elements.vertex.params.ITResourceVertexParams;
import com.graph.elements.vertex.params.VertexParams;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.intf.ParamsInitializer;
import com.graph.logger.GraphLogger;
import com.graph.topology.importers.ImportTopology;

public class MLSNDLibImportTopology extends ImportTopology {

	private static final String classIdentifier = "MLSLibImportTopology";

	@Override
	public void importTopology(Gcontroller graph, String filename) {
		// add vertices to the graph
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String temp;
			VertexElement vertex1, vertex2;

			// Read till we get to Node definition)
			while ((temp = reader.readLine()).trim().compareTo("NODES (") != 0) {
			}

			// read till we reach the end of node definitions
			while ((temp = reader.readLine()) != null) {
//				System.out.println(temp);
				temp = temp.trim();
				if (temp.trim().compareTo(")") == 0) {
					break;
				}

				Pattern p;
				Matcher m;

				String sourceID = "";
				p = Pattern.compile("[a-zA-Z0-9\\.]+");
				m = p.matcher(temp);
				if (m.find()) {
					sourceID = m.group(0);
				}

				double[] temp1 = new double[2];
				int count = 0;
				while (m.find()) {
					temp1[count] = Double.parseDouble(m.group(0));
					count++;
					if (count == 2)
						break;
				}
				count = 0;
				vertex1 = new VertexElement(sourceID, graph, temp1[0], temp1[1]);
				// BasicVertexParams param = new BasicVertexParams();
				// ITResourceVertexParams param;
				VertexParams param;
				int[] itValues = new int[3];
				if (m.find()) {
					String bOrI = m.group(0);
					if (bOrI.equals("BORDER")) {
//						System.out.println("Border Found!");
						vertex1.setIsBorderNode(true);
						param = new BasicVertexParams();
						param.setVertexElement(vertex1);
						vertex1.setVertexParams(param);
					} else if (bOrI.equals("IT")) {
//						System.out.println("IT Found!");
						vertex1.setIsITNode(true);
						while (m.find()) {
							itValues[count] = Integer.parseInt(m.group(0));
							count++;
							if (count == 3)
								break;
						}
						param = new ITResourceVertexParams(vertex1, sourceID, 0, itValues[0], itValues[1], itValues[2], 0);
						param.setVertexElement(vertex1);
						vertex1.setVertexParams(param);
//						System.out.println("IT " + vertex1.getVertexID() + " cpu: " + itValues[0] + ", ram : " + itValues[1] + ", storage : " + itValues[2]);
					}
				}
				graph.addVertex(vertex1);

			}

			// Read till we get to Edge definition)
			while ((temp = reader.readLine()).trim().compareTo("LINKS (") != 0) {
			}

			// read till we reach the end of the edge definition
			while ((temp = reader.readLine()) != null) {
				temp = temp.trim();
				if (temp.length() == 1) {
					break;
				}

				Pattern p;
				Matcher m;

				p = Pattern.compile("[a-zA-Z0-9\\.]+");
				m = p.matcher(temp);
				String[] temp1 = new String[3];
				int count = 0;
				while (m.find()) {
					temp1[count] = m.group(0);
					count++;
					if (count == 3)
						break;
				}

				vertex1 = graph.getVertex(temp1[1]);
				vertex2 = graph.getVertex(temp1[2]);

				EdgeElement edge = new EdgeElement(temp1[0], vertex1, vertex2, graph);

				// Compute delay using X and Y Coords from Vertices
//				double distance = Math.sqrt(Math.pow(vertex1.getXCoord() - vertex2.getXCoord(), 2) + Math.pow(vertex1.getYCoord() - vertex2.getYCoord(), 2));
//				double delay = distance / 29.9792458; // (in ms)
				// @TODO import parameters for link weight and delay from brite
				Properties leser = new Properties();
				leser.load(new FileInputStream("init.cfg"));
				EdgeParams params = new BasicEdgeParams(edge, ParamsInitializer.initDelay(vertex1, vertex2), 1, Double.parseDouble(leser.getProperty("initBandwidth")));

				// Temporary element to set available link capacity to 0 for all
				// links
				edge.setEdgeParams(params);
				graph.addEdge(edge);
			}
			reader.close();

		} catch (FileNotFoundException e) {
			GraphLogger.logError("The file " + filename + " could not be found", classIdentifier);
			e.printStackTrace();
		} catch (IOException e) {
			GraphLogger.logError("IO Exception while reading file ", classIdentifier);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Gcontroller graph = new GcontrollerImpl();
		String base = ".//atlantaDomain1.txt";
		ImportTopology importer = new MLSNDLibImportTopology();
		importer.importTopology(graph, base);
		//System.out.println("border node size is " + graph.getBorderNodeVertexElements().size());
		for (VertexElement border : graph.getVertexSet()) {
			if (border.isITNode())
				System.out.println(border.getVertexID() + " is an IT node");
			else {
				System.out.println(border.getVertexID() + " is not!");
			}
		}
	}

	@Override
	public void importTopologyFromString(Gcontroller graph, String[] topology) {
		// add vertices to the graph
		String temp;
		VertexElement vertex1, vertex2;

		int counter = 0;
		int flag = 0;
		while (counter < topology.length) {
			if (topology[counter].trim().compareTo("NODES (") == 0) {
				flag = 1;
				break;
			}
			counter++;
		}
		if (flag == 0) {
			GraphLogger.logError("Invalid Topology Information", classIdentifier);
			System.exit(-1);
		}
		counter++;
		// read till we reach the end of node definitions
		while (counter < topology.length) {
			temp = topology[counter].trim();
			if (temp.trim().compareTo(")") == 0) {
				break;
			}

			Pattern p;
			Matcher m;

			String sourceID = "";
			p = Pattern.compile("[a-zA-Z0-9\\.]+");
			m = p.matcher(temp);
			if (m.find()) {
				sourceID = m.group(0);
			}

			double[] temp1 = new double[2];
			int count = 0;
			while (m.find()) {
				temp1[count] = Double.parseDouble(m.group(0));
				count++;
				if (count == 2)
					break;
			}

			vertex1 = new VertexElement(sourceID, graph, temp1[0], temp1[1]);
			graph.addVertex(vertex1);
			System.out.println("Vertex Added: VertexID=" + vertex1.getVertexID() + ", X=" + vertex1.getXCoord() + ", Y=" + vertex1.getYCoord());
			counter++;
		}

		// Read till we get to Edge definition)
		flag = 0;
		while (counter < topology.length) {
			if (topology[counter].trim().compareTo("LINKS (") == 0) {
				flag = 1;
				break;
			}
			counter++;
		}

		if (flag == 0) {
			GraphLogger.logError("Invalid Topology Information", classIdentifier);
			System.exit(-1);
		}

		counter++;
		// read till we reach the end of the edge definition
		while (counter < topology.length) {
			temp = topology[counter].trim();
			if (temp.length() == 1) {
				break;
			}

			Pattern p;
			Matcher m;

			p = Pattern.compile("[a-zA-Z0-9\\.]+");
			m = p.matcher(temp);
			String[] temp1 = new String[3];
			int count = 0;
			while (m.find()) {
				temp1[count] = m.group(0);
				count++;
				if (count == 3)
					break;
			}

			vertex1 = graph.getVertex(temp1[1]);
			vertex2 = graph.getVertex(temp1[2]);

			EdgeElement edge = new EdgeElement(temp1[0], vertex1, vertex2, graph);

			System.out.println("Edge Added: Edge ID=" + edge.getEdgeID() + ", sourceID=" + vertex1.getVertexID() + ", destinationID = " + vertex2.getVertexID());
			// Compute delay using X and Y Coords from Vertices
			double distance = Math.sqrt(Math.pow(vertex1.getXCoord() - vertex2.getXCoord(), 2) + Math.pow(vertex1.getYCoord() - vertex2.getYCoord(), 2));

			double delay = distance / 29.9792458; // (in ms)
			// @TODO import parameters for link weight and delay from brite
			EdgeParams params = new BasicEdgeParams(edge, delay, 1, 40);

			edge.setEdgeParams(params);
			graph.addEdge(edge);
			counter++;
		}

	}

}