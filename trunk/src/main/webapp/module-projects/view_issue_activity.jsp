<%@ include file="/common/taglibs.jsp"%>

<bean:define id="pageTitleKey" value="itracker.web.issueactivity.title"/>
<bean:define id="pageTitleArg" value="${issueId}"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>
		
		<c:if test='${empty activities}'> 

   			<center><span style="color: red;"><it:message key="itracker.web.error.noactivity"/></span></center>
			
		</c:if>
		

            <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
              <tr>
                <td class="editColumnTitle" colspan="7">Activity:</td>
              </tr>
              <tr align="left" class="listHeading">
                <td><it:message key="itracker.web.attr.date"/></td>
                <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/></td>
                <td><it:message key="itracker.web.attr.type"/></td>
                <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/></td>
                <td><it:message key="itracker.web.attr.description"/></td>
                <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" width="5" height="1"/></td>
                <td><it:message key="itracker.web.attr.user"/></td>
              </tr>
				
    			<c:forEach var="activity" items="${activities}" varStatus="i"> 
    			
                  <tr class="${i.count % 2 == 0 ? 'listRowShaded' : 'listRowUnshaded'}" >
                    <td valign="top"><it:formatDate date="${activity.key.createDate}"/></td>
                    <td></td>
                    <td valign="top">${activity.value}</td>
                    <td></td>
                    <td valign="top">${activity.key.description}</td>
                    <td></td>
                    <td valign="top">${activity.key.user.firstName}  ${activity.key.user.lastName}</td>
                  </tr>
          </c:forEach>
            </table>

        <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>

