/*-
 * #%L
 * prolobjectlink-jps-jetty
 * %%
 * Copyright (C) 2012 - 2019 Prolobjectlink Project
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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.prolobjectlink.db.DatabaseServer;
import org.prolobjectlink.db.platform.linux.LinuxDatabaseServer;
import org.prolobjectlink.db.platform.macosx.MacosxDatabaseServer;
import org.prolobjectlink.db.platform.win32.Win32DatabaseServer;
import org.prolobjectlink.web.platform.linux.jetty.LinuxJettyWebServer;
import org.prolobjectlink.web.platform.macosx.jetty.MacosxJettyWebServer;
import org.prolobjectlink.web.platform.win32.jetty.Win32JettyWebServer;

public class JettyServerControl extends AbstractWebControl implements WebServerControl {

	public JettyServerControl(WebServer webServer, DatabaseServer databaseServer) {
		super(webServer, databaseServer);
	}

	public static void main(String[] args) {
		// TODO Port from args

		int port = 8081;

		JettyWebServer server = null;
		DatabaseServer database = null;

		if (WebPlatformUtil.runOnWindows()) {
			database = new Win32DatabaseServer();
			server = new Win32JettyWebServer(port);
		} else if (WebPlatformUtil.runOnOsX()) {
			database = new MacosxDatabaseServer();
			server = new MacosxJettyWebServer(port);
		} else if (WebPlatformUtil.runOnLinux()) {
			database = new LinuxDatabaseServer();
			server = new LinuxJettyWebServer(port);
		} else {
			Logger.getLogger(JettyServerControl.class.getName()).log(Level.SEVERE, null, "Not supported platfor");
			System.exit(1);
		}

		new JettyServerControl(server, database).run(args);
	}

}
