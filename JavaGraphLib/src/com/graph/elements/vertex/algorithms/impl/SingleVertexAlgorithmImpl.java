package com.graph.elements.vertex.algorithms.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import com.graph.elements.vertex.VertexElement;
import com.graph.elements.vertex.algorithms.VertexAlgorithm;
import com.graph.elements.vertex.algorithms.contraints.VertexConstraint;
import com.graph.elements.vertex.algorithms.contraints.impl.SingleVertexConstraint;
import com.graph.elements.vertex.params.ITResourceVertexParams;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.topology.importers.ImportTopology;
import com.graph.topology.importers.impl.MLSNDLibImportTopology;

/**
 * Algorithm for searching single vertex element using a certain constraint
 * 
 * @author Yuesheng Zhong
 * 
 */
public class SingleVertexAlgorithmImpl implements VertexAlgorithm {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.graph.elements.vertex.algorithms.VertexAlgorithm#searchVertex(com
	 * .graph.graphcontroller.Gcontroller,
	 * com.graph.elements.vertex.algorithms.contraints.VertexConstraint)
	 */
	@Override
	public VertexElement searchVertex(Gcontroller controller, VertexConstraint constraint) {
		ITResourceVertexParams params = null;
		Set<VertexElement> elements = controller.getVertexSet();
		SingleVertexAlgorithmImpl.debug("R-CPU: " + constraint.getCPU() + " , R-RAM: " + constraint.getRAM() + " , R-Storage: " + constraint.getSTORAGE());
		for (VertexElement testElement : elements) {
			if (testElement.isITNode()) {
				params = (ITResourceVertexParams) testElement.getVertexParams();
				SingleVertexAlgorithmImpl.debug("availabe CPU : " + params.getAvailableCPU() + ", required CPU : " + constraint.getCPU());
				SingleVertexAlgorithmImpl.debug("availabe RAM : " + params.getAvailableRAM() + ", required RAM : " + constraint.getRAM());
				SingleVertexAlgorithmImpl.debug("availabe STO : " + params.getAvailableSTORAGE() + ", required STO : " + constraint.getSTORAGE());
				if ((params.getAvailableCPU() >= constraint.getCPU()) && (params.getAvailableRAM() >= constraint.getRAM()) && (params.getAvailableSTORAGE() >= constraint.getSTORAGE())) {
					return testElement;
				}
			}else{
				SingleVertexAlgorithmImpl.debug(testElement.getVertexID() + " is not IT node");
			}
		}
		return null;
	}
	
	public static void debug(String debugString) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./debugFile.txt"), true));
			bw.write(debugString + "\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		SingleVertexConstraint cons = new SingleVertexConstraint(2,30,30);
		SingleVertexAlgorithmImpl algo = new SingleVertexAlgorithmImpl();
		
		ImportTopology importer = new MLSNDLibImportTopology();
		Gcontroller graph = new GcontrollerImpl();
		importer.importTopology(graph, "./germany50.txt");
		
		VertexElement element = algo.searchVertex(graph, cons);
		System.out.println("element  = " + element.getVertexID());
		
	}

}
