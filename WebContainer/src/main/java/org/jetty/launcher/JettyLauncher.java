package org.jetty.launcher;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyLauncher {

	private static Server server;

	static int port = 8080;

	public static void main (String[] args) {
		try {
			server = new Server (port);
			
			  HandlerList handlers = new HandlerList();
			  
			WebAppContext webAppContext = new WebAppContext();
			webAppContext.setDescriptor(webAppContext + "/WEB-INF/web.xml");
			webAppContext.setResourceBase(".");
			webAppContext.setContextPath("/");
		    ResourceHandler resource_handler = new ResourceHandler();
		    resource_handler.setDirectoriesListed(true);
		    resource_handler.setWelcomeFiles(new String[]{ "index.html" });
		    resource_handler.setResourceBase("./static");
	        handlers.setHandlers(new Handler[] { webAppContext, resource_handler, new DefaultHandler() });
			server.setHandler(handlers);
//			server.setHandlers(new Handler[] { webAppContext, new DefaultHandler() });
			server.start();
			server.join();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
