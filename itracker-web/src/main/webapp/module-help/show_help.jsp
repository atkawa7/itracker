<%@ include file="/common/taglibs.jsp"%>
<%@ page pageEncoding="UTF-8" %>

<%-- Now include the appropriate help file ${helpPage} --%>
<div class="container-fluid maincontent">
   <div class="row">
      <div class="col-xs-12 col-md-offset-2 col-md-8">
<tiles:insert name="${helpPage}" />
      </div>
   </div>
</div>