  <%@ taglib uri="/itracker.tld" prefix="it" %>
   <table border="0" width="100%" cellpadding="1" class="footer">
      <tr>
      	<td align="left" width="" style="text-align: left;" nowrap colspan="2">
      	   <hr noshade width="100%" height="1">
      	   </td>
      </tr>
      <tr>
        <td align="left" width="" style="text-align: left;" nowrap>
 
           <it:message key="itracker.web.attr.copyright"/> 2002, 2003, 2004 by Jason Carroll, donated it to public domain,
        	<br/>
        	2005 by <a href="http://www.itracker.org" target="_blank">itracker.org</a> Version
        	3.0, licensed under LGPL. </td>
        <td align="right" width="50%" style="text-align: right;" valign="top">
          <% java.util.Date currentDate = new java.util.Date(); %>
          <it:message key="itracker.web.attr.gendate"/>: <it:formatDate date="<%= currentDate %>"/>
        </td>
      </tr>
    </table>
    

