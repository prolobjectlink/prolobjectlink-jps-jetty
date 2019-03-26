/*-
 * #%L
 * prolobjectlink-jps-jetty
 * %%
 * Copyright (C) 2019 Prolobjectlink Project
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.prolobjectlink.web.platform;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.prolobjectlink.web.servlet.HomeServlet;

/**
 * 
 * @author Jose Zalacain
 * @since 1.0
 */
public abstract class AbstractJettyServer extends AbstractWebServer implements JettyWebServer {

	private final Server jettyServer = new Server();
	// private final Connector connector = new SelectChannelConnector()
	private final ServerConnector connector = new ServerConnector(jettyServer);

	public AbstractJettyServer(int serverPort) {
		super(serverPort);
		connector.setPort(serverPort);
		jettyServer.setConnectors(new Connector[] { connector });
		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(HomeServlet.class, "/home");
		jettyServer.setHandler(handler);
	}

	public final String getVersion() {
		return Server.getVersion();
	}

	public final String getName() {
		return JETTY;
	}

	public final void start() {
		try {
			jettyServer.start();
			jettyServer.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stop();
		}
	}

	public final void stop() {
		System.out.println("Server is stopping");
		try {
			jettyServer.stop();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Server is stopped");
	}

}
