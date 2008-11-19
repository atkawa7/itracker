<%@ taglib uri="/tags/itracker" prefix="it" %>

   <table border="0" width="100%" cellpadding="1" class="footer">
      <tr>
      	<td class="footer_ruler" colspan="2">
      	   <hr>
      	   </td>
      </tr>
      <tr>
        <td align="left" width="" style="text-align: left;" nowrap>
 
           <it:message key="itracker.web.attr.copyright"/> 2002, 2003, 2004 by Jason Carroll, donated it to public domain,
        	<br/>
        	2005 by <a href="http://www.itracker.org" target="_blank">itracker.org</a> Version
        	3.0, licensed under LGPL. </td>
        <td align="right" width="50%" style="text-align: right;" valign="top">
          ${currentDate}
          <it:message key="itracker.web.attr.gendate"/>: <it:formatDate date="${currentDate}"/>
        </td>
      </tr>
    </table>
    

