package org.itracker.web.util;

import javax.servlet.ServletContext;

import org.itracker.services.implementations.ITrackerServices;
import org.springframework.web.context.WebApplicationContext;

/**
 * This class gets access to ITracker services layer from a servlet context
 * Since actions already have it done in the top level ITrackerAction (ItrackerBaseAction), this is mostly 
 * to be used in JSPs. 
 * 
 * Marky: And that means that this is somehow deprecated because we
 * want to move all logic code except Taglibs from the JSPs to the Actions. 
 * 
 * TODO In fact this shouldn't be needed / used, ITracker's JSP have way too much java code 
 * 
 * @author ricardo
 *
 */
public class ServletContextUtils {
  
    public static ITrackerServices getItrackerServices(ServletContext servletContext) { 
        WebApplicationContext wac = org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        ITrackerServices itrackerServices = (ITrackerServices) wac.getBean("itrackerServices");
        return itrackerServices;
    }

}

