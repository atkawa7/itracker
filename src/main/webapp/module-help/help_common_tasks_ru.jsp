<%@ page contentType="text/html;charset=windows-1251" %>

<%@ taglib uri="/tags/itracker" prefix="it" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<a name="top"></a><span class="pageHeader">������������ ��������</span><br/>
<ul>
  <li><a href="#create">�������� �������</a></li>
  <li><a href="#edit">�������������� �������</a></li>
  <li><a href="#list">�������� �������</a></li>
  <li><a href="#search">����� �������</a></li>
  <li><a href="#report">��������� �������</a></li>
  <li><a href="#prefs">��������� ��������</a></li>
</ul>

<center><hr width="75%" noshade height="1"/></center>
<a name="create"></a><span class="editColumnTitle">�������� �������</span>
<a href="#top" class="headerLinks">[������]</a><br/>
<p class="help">
����� ������� ����� �������, ������� ��������� �� �������� ������ ��������, ������� ��
����������� ������ � �������� ����. ����� �������� �� ������ ������� ����� �������
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/create.gif"/>)
����� � ��������, ��� �������� �� ������ ��� �������.<br/>
<br/>
��� ���������� ����� ���������� ������� � ������� ����� ���, ��� �� ������� ��������� ���
���� ����� �������.<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="edit"></a><span class="editColumnTitle">�������������� �������</span>
<a href="#top" class="headerLinks">[������]</a><br/>
<p class="help">
���� ��������� �������� ��������������� �������.<br/>
<br/>
���� ������� ������������ �� �������� ��� ITracker, �� ������ �������� ������ �������������
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) �����
� ���� ��������.<br/>
<br/>
����� �� �������������� ������ �������, ������ �������������
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) �����
������������ � ������� ��������.<br/>
<br/>
����� ��������������� ������������ �������, ������� ��������� �� �������� ������ ��������,
������� �� ����������� ������ � �������� ����. �� ������ �������� �������� ������ ��������
������� � ������� (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/view.gif"/>)
����� � ������������ ��������. ���� ������ �������, �������������� ������� �������������
(<html:img styleClass="helpImage" src="../themes/defaulttheme/images/edit.gif"/>) 
����� � ������ ��������.<br/>
<br/>
��� ���������� ����� ���������� ������������� � ������� ����� ���, ��� �� �������
������������� � ��� ������������ �������.<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="list"></a><span class="editColumnTitle">�������� �������</span>
<a href="#top" class="headerLinks">[������]</a><br/>
<p class="help">
����� ���������� ������ ������� ��� �������, ��������� �� �������� ������ ��������,
������� �� ����������� ������ � �������� ����. ����� ������� ������ �������� ������� �
������� (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/view.gif"/>)
����� � ��������, ������� �������� ������ ����������. �� ������ �������� �� ������ �������
������ �������, ����������� ���, ���� �� ��� �� �������� ����������� �� ����������, ���
������������� �������, ��� ������� ��������������� ����������.<br/>
</p>


<center><hr width="75%" noshade height="1"/></center>
<a name="search"></a><span class="editColumnTitle">����� �������</span>
<a href="#top" class="headerLinks">[������]</a><br/>
<p class="help">
� ��� ���� ����������� ������ ������� � �������� �� �� ����������� � ���� �������. ��
�������� ������ �� ������ �������� ��������� �������� ������������, ��������� ������� Ctrl.
�������� ����� ������� �������� ��������, ������� �� ������ �������� �, ��������� Shift,
�� ��������� ��������. ������ �������� ������ � ����������, �������� �����, ����� �����
��� ����������.
<br/>
����� ����� ��������� ����� ��������� ����� � ���������� �������, ������� �� ������ �����
������� (<html:img styleClass="helpImage" src="../themes/defaulttheme/images/search.gif"/>)
����� � �������� �� �������� ������ ��������.
<br/>
�� ������ ����������� �� ������ ������������� ��� ������������� ������� ��� �������
��������������� ����������, ������� �� ��������������� ������.
<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="report"></a><span class="editColumnTitle">��������� �������</span>
<a href="#top" class="headerLinks">[������]</a><br/>
<p class="help">
��� ��������� ������, ������� �������� ������� ����� � ���������, ������� �������
�������� � �����. ����� �������� ��������� ����� �� ����������� ���� � ������� ������
��������� ������. ����� ����� ������������, � ���������� ���������� � ����� ������� ����
��������. � ����������� �� ����� ������� � ������� � ���������� ��������� ��������, �����
������������� �� ���������� ����� ��� ��������� ������.
<br/>
</p>

<center><hr width="75%" noshade height="1"/></center>
<a name="prefs"></a><span class="editColumnTitle">��������� ��������</span>
<a href="#top" class="headerLinks">[������]</a><br/>
<p class="help">
���� �������� ��������� �������� �� ������ ��� ��������� � �������� ����. ��� ������� ���
�� ��������, ����������� �������� ���� ������ ����������. ��������� ��������� ��������
������� ��� �����.<br/>
<br/>
��� ��������� ����� ��������������� ����� � �������, �� ����� ���������� ����� ��������
���������� ����-���� � ����� ��������������� ������������ � �������. ��� ����� �������
�������� ������������, ��� ��� ���������, ��� ��� ����� ������ ����� ������ ��� �������� �.
<br/>
<br/>
��� �������� ���������� ������������ ������� �� ��������, �� ������ ������ 0 ���
������������� �������� ����� ���������� ���. ����� ������������� ����� ���������
���������� ������������ ���������.
<br/>
</p>
