<%@ include file="/common/taglibs.jsp" %>

   <table border="0" width="100%" cellpadding="1" class="footer">
      <tr>
      	<td class="footer_ruler" colspan="2">
      	   <hr />
      	</td>
      </tr>
      <tr>
        <td align="left" width="" style="text-align: left;" nowrap>
 
           <it:message key="itracker.web.footer.powered" /> <a href="http://itracker.sourceforge.net" target="_blank">itracker </a> <c:out  value="${currentVersion}" />,
            <it:message key="itracker.web.footer.licensing" /> <a href="http://www.gnu.org/licenses/lgpl.html">LGPL</a></td>
        <td align="right" width="50%" style="text-align: right;" valign="top">
            <c:set var="gendate"><it:formatDate date="${currentDate}"/></c:set>
            <it:message key="itracker.web.footer.gendate" arg0="${gendate}"/>
        </td>
      </tr>
    </table>
    

