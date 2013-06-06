package org.jersey.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.global.GlobalCfg;

// Plain old Java Object it does not extend as class or implements 
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation. 
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML. 

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/gf")
public class GlobalCfgInit {

	// This method is called if TEXT_PLAIN is request
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "Hello Jersey 123123";
	}

	@GET
	@Path("bandwidth/{bandwidth}/pathCount/{pathCount}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public String initGF(@PathParam("bandwidth") String bandwidth, @PathParam("pathCount") String pathCount) {
		// Parameter initialization
		GlobalCfg.bandwidth = Integer.parseInt(bandwidth);
		GlobalCfg.pathCount = Integer.parseInt(pathCount);
		
		// Give back response
		String response = "Client send bandwidth : " + bandwidth + ", and pathCount : " + pathCount + " to server side";
		return response;
	}

	// This method is called if XML is request
	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXMLHello() {
		return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey 123" + "</hello>";
	}

	// This method is called if HTML is request
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<html> " + "<title>" + "Hello Jersey" + "</title>" + "<body><h1>" + "Hello Jersey 123" + "</body></h1>" + "</html> ";
	}

}