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

import java.sql.SQLException;
import java.util.List;

import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.prolobjectlink.db.DatabaseDriver;
import org.prolobjectlink.db.DatabaseDriverFactory;
import org.prolobjectlink.db.jpa.spi.JPAPersistenceUnitInfo;
import org.prolobjectlink.db.util.JavaReflect;
import org.prolobjectlink.web.application.ControllerGenerator;
import org.prolobjectlink.web.application.JettyControllerGenerator;
import org.prolobjectlink.web.application.JettyModelGenerator;
import org.prolobjectlink.web.application.ModelGenerator;
import org.prolobjectlink.web.application.ServletUrlMapping;
import org.prolobjectlink.web.servlet.admin.DatabaseServlet;
import org.prolobjectlink.web.servlet.admin.DocumentsServlet;
import org.prolobjectlink.web.servlet.admin.ManagerServlet;
import org.prolobjectlink.web.servlet.admin.WelcomeServlet;

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
		handler.addServletWithMapping(WelcomeServlet.class, "/welcome");
		jettyServer.setHandler(handler);
		handler.addServletWithMapping(DatabaseServlet.class, "/databases");
		jettyServer.setHandler(handler);
		handler.addServletWithMapping(ManagerServlet.class, "/manager");
		jettyServer.setHandler(handler);
		handler.addServletWithMapping(DocumentsServlet.class, "/doc");
		jettyServer.setHandler(handler);

		// applications models
		try {
			ModelGenerator modelGenerator = new JettyModelGenerator();
			List<PersistenceUnitInfo> units = modelGenerator.getPersistenceUnits();
			for (PersistenceUnitInfo unit : units) {
				DatabaseDriver databaseDriver = DatabaseDriverFactory.createDriver(unit);
				if (!databaseDriver.getDatabasePing()) {
					databaseDriver.createDatabase();
					JPAPersistenceUnitInfo jpaUnit = (JPAPersistenceUnitInfo) unit;
					String name = jpaUnit.getPersistenceProviderClassName();
					Class<?> cls = JavaReflect.classForName(name);
					Object object = JavaReflect.newInstance(cls);
					PersistenceProvider provider = (PersistenceProvider) object;
					provider.generateSchema(unit, unit.getProperties());
				}
			}
		} catch (SQLException e) {
			// do nothing
		}

		// applications controllers
		ControllerGenerator controllerGenerator = new JettyControllerGenerator();
		List<ServletUrlMapping> mappings = controllerGenerator.getMappings();
		for (ServletUrlMapping servletUrlMapping : mappings) {
			handler.addServletWithMapping(servletUrlMapping.getServlet().getClass(), servletUrlMapping.getMappingUrl());
			jettyServer.setHandler(handler);
		}

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
