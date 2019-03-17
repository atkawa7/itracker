<%@ include file="/common/taglibs.jsp" %>
<sec:authorize ifAllGranted="ROLE_USER">
   <sec:authentication property="principal.username" var="username"/>
   <sec:authentication property="principal.displayName" var="userDN" />
</sec:authorize>
<footer>
    <div class="container-fluid">

       <c:if test="${username == null}">
          <div class="row locales">
             <div class="text-center col-xs-12">
             <c:if test="${fn:length(locales) gt 1}">
                <c:forEach items="${locales}" varStatus="status" var="locMap">
                   <c:if test="${empty locMap.value}"> <span>
                      <a href="?loc=${locMap.key}"
                              class="${locMap.key}_loc"><it:message key="itracker.locale.name" locale="${locMap.key}"/></a></span>
                   </c:if>
                   <c:forEach items="${locMap.value}" var="loc"> <span>
                      <a href="?loc=${loc}"
                         class="${loc}_loc"><it:message key="itracker.locale.name" locale="${loc}"/></a></span></c:forEach>
                </c:forEach>
             </c:if>
             </div>
          </div>
       </c:if>
        <div class="row">
            <div class="col-sm-6 text-left text-xs-center">
                <it:message key="itracker.web.footer.powered" /> <a href="http://itracker.sourceforge.net" target="_blank">itracker </a> <c:out  value="${currentVersion}" />,
                 <it:message key="itracker.web.footer.licensing" /> <a href="http://www.gnu.org/licenses/lgpl.html">LGPL</a></td>
            </div>
            <div class="col-sm-6 text-right text-xs-center">


                <c:set var="gendate"><it:formatDate date="${currentDate}"/></c:set>
                <it:message key="itracker.web.footer.gendate" arg0="${gendate}"/>
            </div>
        </div>
    </div>
</footer>

