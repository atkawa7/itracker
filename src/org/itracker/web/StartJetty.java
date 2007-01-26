package org.itracker.web;

import java.io.IOException;

import org.mortbay.jetty.Server;
import org.mortbay.xml.XmlConfiguration;
import org.xml.sax.SAXException;

/**
 * Start embedded jetty
 * 
 * @author ricardo
 */
public class StartJetty {

    public StartJetty() {
        try {
            Server server = new Server();           
            XmlConfiguration configuration = new XmlConfiguration(getClass().getResource("/jetty.xml"));
            configuration.configure(server);
            server.start();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
