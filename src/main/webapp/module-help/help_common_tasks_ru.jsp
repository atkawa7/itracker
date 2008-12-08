<%@ page contentType="text/html;windows-1251" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<a name="top"></a><span class="pageHeader">œÓ‚ÒÂ‰ÌÂ‚Ì˚Â ‰ÂÈÒÚ‚Ëˇ</span><br/>
<ul>
  <li><a href="#create">—ÓÁ‰‡ÌËÂ Á‡‰‡ÌËˇ</a></li>
  <li><a href="#edit">–Â‰‡ÍÚËÓ‚‡ÌËÂ Á‡‰‡ÌËˇ</a></li>
  <li><a href="#list">œÓÒÏÓÚ Á‡‰‡ÌËÈ</a></li>
  <li><a href="#search">œÓËÒÍ Á‡‰‡ÌËÈ</a></li>
  <li><a href="#report">√ÂÌÂ‡ˆËˇ ÓÚ˜∏ÚÓ‚</a></li>
  <li><a href="#prefs">»ÁÏÂÌÂÌËÂ Ì‡ÒÚÓÂÍ</a></li>
</ul>

<center><hr width="75%" noshade height="1"/></center>
<a name="create"></a><span class="editColumnTitle">—ÓÁ‰‡ÌËÂ Á‡‰‡ÌËˇ</span>
<a href="#top" class="headerLinks">[Ì‡‚Âı]</a><br/>
<p class="help">
◊ÚÓ·˚ ÒÓÁ‰‡Ú¸ ÌÓ‚ÓÂ Á‡‰‡ÌËÂ, ‚Ì‡˜‡ÎÂ ÔÂÂÈ‰ËÚÂ Ì‡ ÒÚ‡ÌËˆÛ —ÔËÒÓÍ ÔÓÂÍÚÓ‚, ˘∏ÎÍÌÛ‚ Ì‡
Ó‰ÌÓËÏÂÌÌÓÈ ÒÒ˚ÎÍÂ ‚ „ÓÎÓ‚ÌÓÏ ÏÂÌ˛. ƒ‡ÎÂÂ ˘∏ÎÍÌËÚÂ Ì‡ ËÍÓÌÍÂ —ÓÁ‰‡Ú¸ ÌÓ‚ÓÂ Á‡‰‡ÌËÂ
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/create.gif"/>)
ˇ‰ÓÏ Ò ÔÓÂÍÚÓÏ, ‰Îˇ ÍÓÚÓÓ„Ó ‚˚ ıÓÚËÚÂ Â„Ó ÒÓÁ‰‡Ú¸.<br/>
<br/>
¬‡Ï ÌÂÓ·ıÓ‰ËÏÓ ËÏÂÚ¸ ÔÓÎÌÓÏÓ˜ËÂ —ÓÁ‰‡Ú¸ ‚ ÔÓÂÍÚÂ ÔÂÂ‰ ÚÂÏ, Í‡Í ‚˚ ÒÏÓÊÂÚÂ ÒÓÁ‰‡‚‡Ú¸ ‰Îˇ
ÌÂ„Ó ÌÓ‚ÓÂ Á‡‰‡ÌËÂ.<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="edit"></a><span class="editColumnTitle">–Â‰‡ÍÚËÓ‚‡ÌËÂ Á‡‰‡ÌËˇ</span>
<a href="#top" class="headerLinks">[Ì‡‚Âı]</a><br/>
<p class="help">
≈ÒÚ¸ ÌÂÒÍÓÎ¸ÍÓ ÒÔÓÒÓ·Ó‚ ÓÚÂ‰‡ÍÚËÓ‚‡Ú¸ Á‡‰‡ÌËÂ.<br/>
<br/>
≈ÒÎË Á‡‰‡ÌËÂ ÓÚÓ·‡Ê‡ÂÚÒˇ Ì‡ ÒÚ‡ÌËˆÂ ÃÓÈ ITracker, ‚˚ ÏÓÊÂÚÂ ˘∏ÎÍÌÛÚ¸ ËÍÓÌÍÛ –Â‰‡ÍÚËÓ‚‡Ú¸
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) ˇ‰ÓÏ
Ò ˝ÚËÏ Á‡‰‡ÌËÂÏ.<br/>
<br/>
 Ó„‰‡ ‚˚ ÔÓÒÏ‡ÚË‚‡ÂÚÂ ‰ÂÚ‡ÎË Á‡‰‡ÌËˇ, ËÍÓÌÍ‡ –Â‰‡ÍÚËÓ‚‡Ú¸
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) ·Û‰ÂÚ
ÓÚÓ·‡Ê‡Ú¸Òˇ ‚ Ó·Î‡ÒÚË ‰ÂÈÒÚ‚ËÈ.<br/>
<br/>
◊ÚÓ·˚ ÓÚÂ‰‡ÍÚËÓ‚‡Ú¸ ÒÛ˘ÂÒÚ‚Û˛˘ÂÂ Á‡‰‡ÌËÂ, ÒÌ‡˜‡Î‡ ÔÂÂÈ‰ËÚÂ Ì‡ ÒÚ‡ÌËˆÛ —ÔËÒÓÍ ÔÓÂÍÚÓ‚,
˘∏ÎÍÌÛ‚ Ì‡ Ó‰ÌÓËÏÂÌÌÓÈ ÒÒ˚ÎÍÂ ‚ „ÓÎÓ‚ÌÓÏ ÏÂÌ˛. Õ‡ ‰‡ÌÌÓÈ ÒÚ‡ÌËˆÂ ˘∏ÎÍÌËÚÂ ËÍÓÌÍÛ œÓÒÏÓÚ
Á‡‰‡ÌËÈ ‚ ÔÓÂÍÚÂ (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/view.gif"/>)
ˇ‰ÓÏ Ò ËÌÚÂÂÒÛ˛˘ËÏ ÔÓÂÍÚÓÏ. »ÏÂˇ ÒÔËÒÓÍ Á‡‰‡ÌËÈ, ‚ÓÒÔÓÎ¸ÁÛÈÚÂÒ¸ ËÍÓÌÍÓÈ –Â‰‡ÍÚËÓ‚‡Ú¸
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) 
‡‰ÓÏ Ò ÌÛÊÌ˚Ï Á‡‰‡ÌËÂÏ.<br/>
<br/>
¬‡Ï ÌÂÓ·ıÓ‰ËÏÓ ËÏÂÚ¸ ÔÓÎÌÓÏÓ˜ËÂ –Â‰‡ÍÚËÓ‚‡Ú¸ ‚ ÔÓÂÍÚÂ ÔÂÂ‰ ÚÂÏ, Í‡Í ‚˚ ÒÏÓÊÂÚÂ
Â‰‡ÍÚËÓ‚‡Ú¸ ‚ Ì∏Ï ÒÛ˘ÂÒÚ‚Û˛˘ËÂ Á‡‰‡ÌËˇ.<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="list"></a><span class="editColumnTitle">œÓÒÏÓÚ Á‡‰‡ÌËÈ</span>
<a href="#top" class="headerLinks">[Ì‡‚Âı]</a><br/>
<p class="help">
◊ÚÓ·˚ ÔÓÒÏÓÚÂÚ¸ ÒÔËÒÓÍ Á‡‰‡ÌËÈ ‰Îˇ ÔÓÂÍÚ‡, ÔÂÂÈ‰ËÚÂ Ì‡ ÒÚ‡ÌËˆÛ —ÔËÒÓÍ ÔÓÂÍÚÓ‚,
˘∏ÎÍÌÛ‚ Ì‡ Ó‰ÌÓËÏÂÌÌÓÈ ÒÒ˚ÎÍÂ ‚ „ÓÎÓ‚ÌÓÏ ÏÂÌ˛. ƒ‡ÎÂÂ Ì‡ÊÏËÚÂ ËÍÓÌÍÛ œÓÒÏÓÚ Á‡‰‡ÌËÈ ‚
ÔÓÂÍÚÂ (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/view.gif"/>)
ˇ‰ÓÏ Ò ÔÓÂÍÚÓÏ, Á‡‰‡ÌËˇ ÍÓÚÓÓ„Ó ıÓÚËÚÂ ÔÓÒÏÓÚÂÚ¸. Õ‡ ‰‡ÌÌÓÈ ÒÚ‡ÌËˆÂ ‚˚ ÏÓÊÂÚÂ Û‚Ë‰ÂÚ¸
‰ÂÚ‡ÎË Á‡‰‡ÌËˇ, ÓÚÒÎÂÊË‚‡Ú¸ Â„Ó, ÂÒÎË ‚˚ ÛÊÂ ÌÂ ÔÓÎÛ˜‡ÎË Û‚Â‰ÓÏÎÂÌËÈ Ó· ËÁÏÂÌÂÌËˇı, ËÎË
Â‰‡ÍÚËÓ‚‡Ú¸ Á‡‰‡ÌËÂ, ÔË Ì‡ÎË˜ËË ÒÓÓÚ‚ÂÚÒÚ‚Û˛˘Ëı ÔË‚ËÎÂ„ËÈ.<br/>
</p>


<center><hr width="75%" noshade height="1"/></center>
<a name="search"></a><span class="editColumnTitle">>œÓËÒÍ Á‡‰‡ÌËÈ</span>
<a href="#top" class="headerLinks">[Ì‡‚Âı]</a><br/>
<p class="help">
” ‚‡Ò ÂÒÚ¸ ‚ÓÁÏÓÊÌÓÒÚ¸ ËÒÍ‡Ú¸ Á‡‰‡ÌËˇ ‚ ÔÓÂÍÚ‡ı ÔÓ Ëı ÒÂ¸∏ÁÌÓÒÚË Ë ÍÓ‰Â ÒÚ‡ÚÛÒ‡. —Ó
ÒÚ‡ÌËˆ˚ ÔÓËÒÍ‡ ‚˚ ÏÓÊÂÚÂ ‚˚·Ë‡Ú¸ ÌÂÒÍÓÎ¸ÍÓ ÁÌ‡˜ÂÌËÈ Ó‰ÌÓ‚ÂÏÂÌÌÓ, Û‰ÂÊË‚‡ˇ ÍÎ‡‚Ë¯Û Ctrl.
¬ÓÁÏÓÊÌÓ Ú‡ÍÊÂ ‚˚·‡Ú¸ ‰Ë‡Ô‡ÁÓÌ ÁÌ‡˜ÂÌËÈ, ˘∏ÎÍÌÛ‚ Ì‡ ÔÂ‚ÓÏ ˝ÎÂÏÂÌÚÂ Ë, Û‰ÂÊË‚‡ˇ Shift,
Ì‡ ÔÓÒÎÂ‰ÌÂÏ ˝ÎÂÏÂÌÚÂ. ”Í‡Á‡‚ ÍËÚÂËË ÔÓËÒÍ‡ Ë ÒÓÚËÓ‚ÍË, ˘∏ÎÍÌËÚÂ œÓËÒÍ, ˜ÚÓ·˚ Ì‡ÈÚË
‚ÒÂ ÒÓ‚Ô‡‰ÂÌËˇ.
<br/>
ÃÓÊÌÓ Ú‡ÍÊÂ ‚˚ÔÓÎÌËÚ¸ ·ÓÎÂÂ ‰ÂÚ‡Î¸Ì˚È ÔÓËÒÍ ‚ ÍÓÌÍÂÚÌÓÏ ÔÓÂÍÚÂ, ˘∏ÎÍÌÛ‚ Ì‡ ËÍÓÌÍÂ œÓËÒÍ
Á‡‰‡ÌËˇ (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/search.gif"/>)
ˇ‰ÓÏ Ò ÔÓÂÍÚÓÏ Ì‡ ÒÚ‡ÌËˆÂ —ÔËÒÓÍ ÔÓÂÍÚÓ‚.
<br/>
»Á ÒÂÍˆËË ÂÁÛÎ¸Ú‡ÚÓ‚ ‚˚ ÏÓÊÂÚÂ Â‰‡ÍÚËÓ‚‡Ú¸ ËÎË ÔÓÒÏ‡ÚË‚‡Ú¸ Á‡‰‡ÌËˇ ÔË Ì‡ÎË˜ËË
ÒÓÓÚ‚ÂÚÒÚ‚Û˛˘Ëı ÔÓÎÌÓÏÓ˜ËÈ, ˘∏ÎÍÌÛ‚ Ì‡ ÒÓÓÚ‚ÂÚÒÚ‚Û˛˘ÂÈ ËÍÓÌÍÂ.
<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="report"></a><span class="editColumnTitle">√ÂÌÂ‡ˆËˇ ÓÚ˜∏ÚÓ‚</span>
<a href="#top" class="headerLinks">[Ì‡‚Âı]</a><br/>
<p class="help">
ƒÎˇ „ÂÌÂ‡ˆËË ÓÚ˜∏Ú‡, ÒÌ‡˜‡Î‡ ÓÚÏÂÚ¸ÚÂ ÔÓÁËˆËË ˇ‰ÓÏ Ò ÔÓÂÍÚ‡ÏË, ÍÓÚÓ˚Â ÊÂÎ‡ÂÚÂ
‚ÍÎ˛˜ËÚ¸ ‚ ÓÚ˜∏Ú. «‡ÚÂÏ ‚˚·ÂËÚÂ ÚÂ·ÛÂÏ˚È ÓÚ˜∏Ú ËÁ ‚˚Ô‡‰‡˛˘Â„Ó ÏÂÌ˛ Ë Ì‡ÊÏËÚÂ ÍÌÓÔÍÛ
„ÂÌÂ‡ˆËË ÓÚ˜∏Ú‡. ŒÚ˜∏Ú ·Û‰ÂÚ Ò„ÂÌÂËÓ‚‡Ì, ‡ ÂÁÛÎ¸Ú‡Ú˚ ÓÚÓ·‡ÊÂÌ˚ ‚ ‚‡¯ÂÏ ÚÂÍÛ˘ÂÏ ÓÍÌÂ
·‡ÛÁÂ‡. ¬ Á‡‚ËÒËÏÓÒÚË ÓÚ ˜ËÒÎ‡ Á‡‰‡ÌËÈ ‚ ÔÓÂÍÚÂ Ë ÍÓÎË˜ÂÒÚ‚‡ ‚˚·‡ÌÌ˚ı ÔÓÂÍÚÓ‚, ÏÓÊÂÚ
ÔÓÚÂ·Ó‚‡Ú¸Òˇ ‰Ó ÌÂÒÍÓÎ¸ÍËı ÏËÌÛÚ ‰Îˇ Ó·‡·ÓÚÍË ÓÚ˜∏Ú‡.
<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="prefs"></a><span class="editColumnTitle">»ÁÏÂÌÂÌËÂ Ì‡ÒÚÓÂÍ</span>
<a href="#top" class="headerLinks">[Ì‡‚Âı]</a><br/>
<p class="help">
◊ÚÓ· ËÁÏÂÌËÚ¸ Ì‡ÒÚÓÈÍË ˘∏ÎÍÌËÚÂ Ì‡ ÒÒ˚ÎÍÂ ÃÓË Ì‡ÒÚÓÈÍË ‚ „ÓÎÓ‚ÌÓÏ ÏÂÌ˛. ›ÚÓ ÔË‚Â‰∏Ú ‚‡Ò
Ì‡ ÒÚ‡ÌËˆÛ, ÔÓÁ‚ÓÎˇ˛˘Û˛ ËÁÏÂÌËÚ¸ ‚‡¯Û ÎË˜ÌÛ˛ ËÌÙÓÏ‡ˆË˛. ÕÂÍÓÚÓ˚Â Ì‡ÒÚÓÈÍË ËÁÏÂÌˇ˛Ú
‚ÌÂ¯ÌËÈ ‚Ë‰ Ò‡ÈÚ‡.<br/>
<br/>
œË ‚ÍÎ˛˜ÂÌËË ÓÔˆËË ‡‚ÚÓÏ‡ÚË˜ÂÒÍÓ„Ó ‚ıÓ‰‡ ‚ ÒËÒÚÂÏÛ, Ì‡ ‚‡¯ÂÏ ÍÓÏÔ¸˛ÚÂÂ ·Û‰ÂÚ ÒÓı‡Ì∏Ì
ÔÓÒÚÓˇÌÌ˚È ÍÛÍË-Ù‡ÈÎ Ò ‚‡¯ËÏ Ë‰ÂÌÚËÙËÍ‡ÚÓÓÏ ÔÓÎ¸ÁÓ‚‡ÚÂÎˇ Ë Ô‡ÓÎÂÏ. ›ÚÓ ÏÓÊÂÚ ‚˚Á‚‡Ú¸
ÔÓ·ÎÂÏ˚ ·ÂÁÓÔ‡ÒÌÓÒÚË, Ú‡Í ˜ÚÓ Û·Â‰ËÚÂÒ¸, ˜ÚÓ ‚‡Ï ÌÛÊÌ‡ ‰‡ÌÌ‡ˇ ÓÔˆËˇ ÔÂÊ‰Â ˜ÂÏ ‚ÍÎ˛˜‡Ú¸ Â∏.
<br/>
<br/>
œË ÛÍ‡Á‡ÌËË ÍÓÎË˜ÂÒÚ‚‡ ÓÚÓ·‡Ê‡ÂÏ˚ı Á‡‰‡ÌËÈ Ì‡ ÒÚ‡ÌËˆÂ, ‚˚ ÏÓÊÂÚÂ ‚‚ÂÒÚË 0 ËÎË
ÓÚËˆ‡ÚÂÎ¸ÌÓÂ ÁÌ‡˜ÂÌËÂ ˜ÚÓ·˚ ÓÚÓ·‡ÁËÚ¸ ‚ÒÂ. À˛·ÓÂ ÔÓÎÓÊËÚÂÎ¸ÌÓÂ ˜ËÒÎÓ ÔÓÌÛÏÂÛÂÚ
ÂÁÛÎ¸Ú‡Ú˚ Ì‡ ÒÚ‡ÌËˆ‡ı Ò ‚˚·‡ÌÌ˚Ï ÍÓÎË˜ÂÒÚ‚ÓÏ ˝ÎÂÏÂÌÚÓ‚.
<%-- TODO: review last sentence translation. --%>
<br/>
</p>
