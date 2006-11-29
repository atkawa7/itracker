<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/itracker.tld" prefix="it" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tags/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/c" prefix="c" %>

<%-- <it:checkLogin/> --%>

<a name="top"></a><span class="pageHeader">Alltägliche Aufgaben</span><br/>
<ul>
  <li><a href="#create">Einen Eintrag anlegen</a></li>
  <li><a href="#edit">Einen Eintrag bearbeiten</a></li>
  <li><a href="#list">Einträge auflisten</a></li>
  <li><a href="#search">Einträge suchen</a></li>
  <li><a href="#report">Reports erzeugen</a></li>
  <li><a href="#prefs">Voreinstellungen bearbeiten</a></li>
</ul>

<center><hr width="75%" noshade height="1"/></center>
<a name="create"></a><span class="editColumnTitle">Einen Eintrag anlegen</span> <a href="#top" class="headerLinks">[top]</a><br/>
<p class="help">
Um einen neuen Eintrag anzulegen, müssen Sie zuerst die "Projekt Liste" im
Hauptmenü auswählen. Dort können Sie über den Knopf "Neuer Eintrag"
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/create.gif"/>) 
neben dem gewünschten Projekt den Eintrag erstellen.<br/>
<br/>
Sie müssen dabei die Rechte haben, in dem Projekt neue Einträge anlegen zu können.
<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="edit"></a><span class="editColumnTitle">Einen Eintrag bearbeiten</span> <a href="#top" class="headerLinks">[top]</a><br/>
<p class="help">
Es gibt mehrere Wege, einen bestehenden Eintrag zu bearbeiten.<br/>
<br/>
Ist der Eintrag auf der myITracker Seite sichtbar, können Sie auf den "Bearbeiten" Knopf
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) neben dem
Eintrag klicken.<br/>
<br/>
Wenn Sie sich gerade die Details eines Eintrags ansehen, wird der "Bearbeiten" Knopf
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) auf der Seite angzeigt.<br/>
<br/>
Um einen beliebigen Eintrag zu bearbeiten, können Sie zuerst die "Projekt Liste"
im Hauptmenü anwählen. Von da aus können Sie den "Einträge ansehen" Knopf 
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/view.gif"/>)
neben dem gewünschten Projekt betätigen, um eine Liste der Einträge zu erhalten.
In dieser Liste können Sie dann neben dem gewünschten Eintrag auf den 
"Bearbeiten" Knopf (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) klicken.<br/>
<br/>
Sie müssen dabei die Rechte haben, in dem Projekt bestehende Einträge bearbeiten zu dürfen.<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="list"></a><span class="editColumnTitle">Einträge auflisten</span> <a href="#top" class="headerLinks">[top]</a><br/>
<p class="help">
Um die Einträge für ein Projekt aufzulisten, müssen Sie zuerst die "Projekt Liste"
im Hauptmenü anwählen. Von dieser Liste aus können Sie den "Einträge ansehen" Knopf 
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/view.gif"/>)
neben dem gewünschten Projekt betätigen, um eine Liste der Einträge zu erhalten.
Von dieser Liste aus, können Sie die Details eines Eintrags ansehen, auswählen, dass Sie
an dem Eintrag interessiert sind (nur falls Sie noch keine Benachrichtigungs E-Mails für
diesen Eintrag erhalten) oder den Eintrag bearbeiten, wenn Sie über die
notwendigen Rechte verfügen.<br/>
</p>


<center><hr width="75%" noshade height="1"/></center>
<a name="search"></a><span class="editColumnTitle">Einträge suchen</span> <a href="#top" class="headerLinks">[top]</a><br/>
<p class="help">
Hier können Sie Einträge über verschiedene Projekte hinweg suchen. Die Suche kann nach Wichtigkeit
und Status erfolgen. In der Suchseite können Sie in Auswahlboxen durch Drücken der Ctrl-Taste
mehrere Werte durch Anklicken auswählen, während über die Betätigung der Shift-Taste während
des Klickens ein Bereich von Werten ausgewählt wird. Nach dem Sie die Suchparameter eingetragen
haben, können Sie über den "Suchen" Knopf mit der Suche beginnen.
<br/>
Sie können auch eine detailliertere Suche innerhalb eines Projekts durchführen, indem Sie
den "Suchen" Knopf (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/search.gif"/>)
eines bestimmten Projekts in der "Projekt Liste" Seite betätigen.
<br/>
Von der Liste mit den Suchergebnissen aus können Sie Einträge ansehen oder bearbeiten, sofern
Sie über die entsprechenden Rechte verfügen.<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="report"></a><span class="editColumnTitle">Reports Erstellen</span> <a href="#top" class="headerLinks">[top]</a><br/>
<p class="help">
Kreuzen Sie in den Kästchen neben den Projekten diejenigen an, die Sie in dem Report
enthalten sehen wollen. Danach können Sie den gewünschten Report aus der Checkbox
auswählen und die Erzeugung über den "Report erstellen" Knopf starten.
Der Report wird dann erstellt und im aktuellen Browser-Fenster angezeigt. Je nach 
Anzahl der Einträge in den Projekten kann die Erzeugung des Reports mehrere Minuten
dauern.<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="prefs"></a><span class="editColumnTitle">Voreinstellungen bearbeiten</span> <a href="#top" class="headerLinks">[top]</a><br/>
<p class="help">
Über den Punkt "Meine Einstellungen" im Haputmenü können Sie ihre Voreinstellungen ändern.
Auf der Seite mit den Einstellungen können Sie sowohl persönliche Informationen ändern,
als auch Einstellungen, die die Darstellung von ITracker beeinflussen.<br/>
<br/>
Wenn Sie "Login speichern" ausgewählt haben, wird ein permanenter Cookie mit ihrem
Benutzernamen im Klartext und dem verschlüsselten Password auf Ihrem Rechner gespeichert. 
Dies kann ein
Sicherheitsrisiko darstellen, wenn andere Personen Zugriff auf Ihren Rechner haben.
Wählen Sie diesen Punkt also nur aus, wenn Sie sicher sind, dass Sie das wollen.<br/>
<br/>
Wenn Sie bei der Anzahl der Einträge, die auf einer Seite angezeigt werden sollen, 
0 oder eine negative Zahl eintragen, können Sie alle Einträge ansehen auf einer Seite
ansehen. Die Anzeige einer positiven Zahl beschränkt die Anzeige auf diese Anzahl an
Einträgen pro Seite.<br/>
</p>
