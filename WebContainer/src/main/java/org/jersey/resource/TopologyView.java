package org.jersey.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/topology")
public class TopologyView {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getHtmlTopology() {
		
		String out = "";
		
		
		out = "{\"nodes\": [ " +
						    "  { \"data\": { \"id\": \"j\", \"name\": \"Mohit\" } , \"position\" : {\"x\":100, \"y\":100} },  " +
						     " { \"data\": { \"id\": \"e\", \"name\": \"Gunjan\" }, \"position\" : {\"x\":100, \"y\":200}  }," +
						    "  { \"data\": { \"id\": \"k\", \"name\": \"Kramer\" }, \"position\" : {\"x\":200, \"y\":100}  }," +
						    "  { \"data\": { \"id\": \"g\", \"name\": \"George\" } , \"position\" : {\"x\":200, \"y\":200} }" +
						   " ]," +
						   " \"edges\": [" +
						   "   { \"data\": { \"source\": \"j\", \"target\": \"e\" } }," +
						   "   { \"data\": { \"source\": \"j\", \"target\": \"k\" } }," +
						   "   { \"data\": { \"source\": \"j\", \"target\": \"g\" } }," +
						   "   { \"data\": { \"source\": \"e\", \"target\": \"j\" } }," +
						   "   { \"data\": { \"source\": \"e\", \"target\": \"k\" } }," +
						   "   { \"data\": { \"source\": \"k\", \"target\": \"j\" } }," +
						   "   { \"data\": { \"source\": \"k\", \"target\": \"e\" } }," +
						   "   { \"data\": { \"source\": \"k\", \"target\": \"g\" } }," +
						   "   { \"data\": { \"source\": \"g\", \"target\": \"j\" } }" +
						   " ]" +
						"  }";
		
		return out;
		
	}


}
