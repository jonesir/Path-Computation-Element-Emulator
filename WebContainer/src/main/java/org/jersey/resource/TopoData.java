package org.jersey.resource;

import java.util.ArrayList;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.graph.topology.importers.ImportTopology;
import com.graph.topology.importers.impl.SNDLibImportTopology;

@Path("/topo/nodes")
public class TopoData {
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
	public String getHtmlTopology() {
		
		Gcontroller graph = new GcontrollerImpl();
		ImportTopology importTopology = new SNDLibImportTopology();
		importTopology.importTopology(graph, ".//atlanta.txt");
		
		//Generate map of nodes 
		ArrayList<Node> list = new ArrayList<Node>();
		Iterator<VertexElement> iter = graph.getVertexSet().iterator();
		while(iter.hasNext()) {
			VertexElement vertex = iter.next();
			list.add(new Node(vertex.getVertexID(), vertex.getXCoord()/10,vertex.getYCoord()/10));
		}
		
		Gson gson = new Gson();
		
		
		return gson.toJson(list);
		
		
	}
}
