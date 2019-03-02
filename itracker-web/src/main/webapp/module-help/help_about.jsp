<%@ include file="/common/taglibs.jsp" %>
<%@ page pageEncoding="UTF-8" %>

      <div class="help well">
         <b><it:message key="itracker.web.helpabout.itrackerversion"/>:</b> <bean:write name="currentVersion"/><br/>
         <b><it:message key="itracker.web.helpabout.starttime"/>:</b> <bean:write name="starttime"/><br/>
         <b><it:message key="itracker.web.helpabout.defaultlocale"/>:</b> <it:message key="itracker.locale.name"/><br/>
         <br/>

         <b><it:message key="itracker.web.helpabout.javaversion"/>:</b> <bean:write name="javaVersion" scope="request"/>,
         <bean:write name="javaVendor" scope="request"/><br/>
         <br/>

         <b><it:message key="itracker.web.helpabout.createdby"/>: </b>
         itracker developer community, <a href="http://itracker.sourceforge.net">itracker.org</a>, based on the
         initial code donation by Jason Carroll.
      </div>

