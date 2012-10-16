<%@ include file="/common/taglibs.jsp" %>


<p class="help">
    <b><it:message key="itracker.web.helpabout.itrackerversion"/>:</b> <bean:write name="version"/><br/>
    <b><it:message key="itracker.web.helpabout.starttime"/>:</b> <bean:write name="starttime"/><br/>
    <b><it:message key="itracker.web.helpabout.defaultlocale"/>:</b> <it:message key="itracker.locale.name"/><br/>
    <br/>

    <b><it:message key="itracker.web.helpabout.javaversion"/>:</b> <bean:write name="javaVersion" scope="request"/>,
    <bean:write name="javaVendor" scope="request"/><br/>
    <br/>
</p>

<br/><br/>
<table cellspacing="0" cellspacing="1" border="0" width="100%" class="help">
    <tr>
        <td><b><it:message key="itracker.web.helpabout.createdby"/>:</b></td>
        <td align="left">Initial Code Donation: Jason Carroll (<a href="mailto:jcarroll@cowsultants.com">jcarroll@cowsultants.com</a>),
            and the itracker developer community...
        </td>
    </tr>
</table>
