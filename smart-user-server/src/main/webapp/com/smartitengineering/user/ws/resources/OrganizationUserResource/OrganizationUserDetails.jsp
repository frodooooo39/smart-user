<%-- 
    Document   : OrganizationUserDetails
    Created on : Jul 24, 2010, 1:30:52 PM
    Author     : russel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.smartitengineering.user.domain.User" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="java.util.Collection"%>

<c:if test="${param['lang']!=null}">
  <fmt:setLocale scope="session" value="${param['lang']}"/>
</c:if>

<div id="leftmenu_userdeatils_1" class="leftmenu">
  <div id="leftmenu_header_userdeatils_1" class="leftmenu_header"><label>Individual-User</label></div>
  <div id="leftmenu_body_userdeatils_1" class="leftmenu_body">
    <ul>
      <li><a href="javascript: showonlyone('newboxes1')"><fmt:message key="org.tablehead7"/></a></li>
      <li><a href="javascript: showonlyone('newboxes2')"><fmt:message key="org.tablehead4"/></a></li>
      <li><a href="javascript: showonlyone('newboxes3')"><fmt:message key="org.tablehead6"/></a></li>
    </ul>
  </div>
</div>

<div name="newboxes" id="newboxes1" class="show">
  <div id="individual_user_details_header"  class="header_details_info"><label><c:out value="${it.user.username}"></c:out></label></div>
  <div id="individual_user_details_content" class="content_details_info">
    <div class="individual_details_label"><label><fmt:message key="org.usrtablehead2"/></label></div>
    <div class="individual_details_data"><label>${it.user.username}</label></div>
    <div class="clear"></div>
    <div class="individual_details_label"><label><fmt:message key="org.usrinput4"/></label></div>
    <div class="individual_details_data"><label>${it.user.password}</label></div>
    <div class="clear"></div>

    <form method="POST" action ="http://localhost:9090/orgs/sn/${it.user.organization.uniqueShortName}/users/un/${it.user.username}/delete" accept="application/json" id="organizationform">
      <input type="hidden" name="id" value="${it.id}" class="textField" id="id">
      <input type="hidden" name="version" value="${it.version}" class="textField" id="version">
      <input type="hidden" name="userId" value="${it.user.id}" class="textField" id="id">
      <input type="hidden" name="userVersion" value="${it.user.version}" class="textField" id="version">
      <input type="hidden" name="personId" value="${it.person.id}" class="textField" id="id">
      <input type="hidden" name="personVersion" value="${it.person.version}" class="textField" id="version">
      <input type="hidden" name="selfId" value="${it.person.self.id}" class="textField" id="id">
      <input type="hidden" name="selfVersion" value="${it.person.self.version}" class="textField" id="version">
      <input type="hidden" name="spouseId" value="${it.person.spouse.id}" class="textField" id="id">
      <input type="hidden" name="spouseVersion" value="${it.person.spouse.version}" class="textField" id="version">
      <input type="hidden" name="fatherId" value="${it.person.father.id}" class="textField" id="id">
      <input type="hidden" name="fatherVersion" value="${it.person.father.version}" class="textField" id="version">
      <input type="hidden" name="motherId" value="${it.person.mother.id}" class="textField" id="id">
      <input type="hidden" name="motherVersion" value="${it.person.mother.version}" class="textField" id="version">
      <input type="hidden" name="userName" value="${it.user.username}" class="textField" id="name">
      <input type="hidden" name="password" value="${it.user.password}" class="textField" id="password">
      <div class="clear"></div>
      <div class="btnfield"><input type="submit" value="DELETE" name="submitbtn" class="submitbtn"></div>
      <div class="clear"></div>
    </form>
  </div>
</div>

<div name="newboxes" id="newboxes2" class="hide">
  <div id="header_organization_users" class="header_entry_form"><label id="header_user_label"><c:out value="${it.user.username}"></c:out>-Change your password</label></div>
  <fmt:message key="org.usrinput6" var="submitbtn"/>
  <div id="form_organizationentry" class="entry_form">
    <form method="POST" action ="http://localhost:9090/orgs/sn/${it.user.organization.uniqueShortName}/users/un/${it.user.username}/update" accept="application/json" id="organizationform">
      <div class="form_label"><label><fmt:message key="org.usrtablehead1"/></label></div>
      <div class="form_textField"><input type="text" name="firstName" value="${it.person.self.name.firstName}" class="textField" id="name"></div>
      <div class="clear"></div>
      <div class="form_label"><label><fmt:message key="org.usrtablehead2"/></label></div>
      <div class="form_textField"><input type="text" name="middleInitial" value="${it.person.self.name.middleInitial}" class="textField" id="name"></div>
      <div class="clear"></div>
      <div class="form_label"><label><fmt:message key="org.usrtablehead3"/></label></div>
      <div class="form_textField"><input type="text" name="lastName" value="${it.person.self.name.lastName}" class="textField" id="name"></div>
      <div class="clear"></div>
      <div class="form_label"><label><fmt:message key="org.usrtablehead2"/></label></div>
      <div class="form_textField"><input type="text" name="userName" value="${it.user.username}" class="textField" id="name"></div>
      <div class="clear"></div>
      <div class="form_label"><label><fmt:message key="org.usrinput4"/></label></div>
      <div class="form_textField"><input type="text" name="password" value="${it.user.password}" class="textField" id="password"></div>
      <div class="clear"></div>
      <div class="btnfield"><input type="submit" value="UPDATE" name="submitbtn" class="submitbtn"></div>
      <div class="clear"></div>

      <input type="hidden" name="id" value="${it.id}" class="textField" id="id">
      <input type="hidden" name="version" value="${it.version}" class="textField" id="version">
      <input type="hidden" name="userId" value="${it.user.id}" class="textField" id="id">
      <input type="hidden" name="userVersion" value="${it.user.version}" class="textField" id="version">
      <input type="hidden" name="personId" value="${it.person.id}" class="textField" id="id">
      <input type="hidden" name="personVersion" value="${it.person.version}" class="textField" id="version">
      <input type="hidden" name="selfId" value="${it.person.self.id}" class="textField" id="id">
      <input type="hidden" name="selfVersion" value="${it.person.self.version}" class="textField" id="version">
      <input type="hidden" name="spouseId" value="${it.person.spouse.id}" class="textField" id="id">
      <input type="hidden" name="spouseVersion" value="${it.person.spouse.version}" class="textField" id="version">
      <input type="hidden" name="fatherId" value="${it.person.father.id}" class="textField" id="id">
      <input type="hidden" name="fatherVersion" value="${it.person.father.version}" class="textField" id="version">
      <input type="hidden" name="motherId" value="${it.person.mother.id}" class="textField" id="id">
      <input type="hidden" name="motherVersion" value="${it.person.mother.version}" class="textField" id="version">
    </form>
  </div>
</div>

<div name="newboxes" id="newboxes3" class="hide">
  <div id="header_organization_users" class="header_entry_form"><label id="header_user_label"><c:out value="${it.user.username}"></c:out>-Edit Information</label></div>
  <fmt:message key="org.usrinput6" var="submitbtn"/>
  <div id="form_organizationentry" class="entry_form">
    <form method="POST" action ="http://localhost:9090/orgs/sn${it.user.organization.uniqueShortName}/users/un/${it.user.username}/update" accept="application/json" id="userEditForm">
      <div class="form_label"><label><fmt:message key="org.usrtablehead2"/></label></div>
      <div class="individual_details_data"><label><c:out value="${it.user.username}"/></label></div>
      <div class="clear"></div>
      <div class="form_label"><label>Old Password</label></div>
      <div class="form_textField"><input type="password" name="oldPassword" class="textField" id="oldPassword"><img id="wrong" src="/css/images/icontexto-webdev-remove-032x032.png" alt="wrong" /><label class="error">Wrong Password</label></div>
      <div class="clear"></div>
      <div class="form_label"><label>New Password</label></div>
      <div class="form_textField"><input type="password" name="password" class="textField" id="newPassword"></div>
      <div class="clear"></div>
      <div class="form_label"><label>Confirm Password</label></div>
      <div class="form_textField"><input type="password" name="confirmPassword" class="textField" id="confirmPassword"></div>
      <div class="clear"></div>
      <div class="btnfield"><input type="submit" value="UPDATE" name="submitbtn" class="submitbtn" id="update"></div>
      <div class="clear"></div>

      <input type="hidden" name="id" value="${it.id}" class="textField" id="id">
      <input type="hidden" name="version" value="${it.version}" class="textField" id="version">
      <input type="hidden" name="userId" value="${it.user.id}" class="textField" id="id">
      <input type="hidden" name="userVersion" value="${it.user.version}" class="textField" id="version">
      <input type="hidden" name="personId" value="${it.person.id}" class="textField" id="id">
      <input type="hidden" name="personVersion" value="${it.person.version}" class="textField" id="version">
      <input type="hidden" name="selfId" value="${it.person.self.id}" class="textField" id="id">
      <input type="hidden" name="selfVersion" value="${it.person.self.version}" class="textField" id="version">
      <input type="hidden" name="spouseId" value="${it.person.spouse.id}" class="textField" id="id">
      <input type="hidden" name="spouseVersion" value="${it.person.spouse.version}" class="textField" id="version">
      <input type="hidden" name="fatherId" value="${it.person.father.id}" class="textField" id="id">
      <input type="hidden" name="fatherVersion" value="${it.person.father.version}" class="textField" id="version">
      <input type="hidden" name="motherId" value="${it.person.mother.id}" class="textField" id="id">
      <input type="hidden" name="motherVersion" value="${it.person.mother.version}" class="textField" id="version">
      <input type="hidden" name="firstName" value="${it.person.self.name.firstName}" class="textField" id="name">
      <input type="hidden" name="middleInitial" value="${it.person.self.name.middleInitial}" class="textField" id="name">
      <input type="hidden" name="lastName" value="${it.person.self.name.lastName}" class="textField" id="name">
      <input type="hidden" name="nationalID" value="${it.person.self.nationalID}" class="textField" id="name">
      <input type="hidden" name="primaryEmail" value="${it.person.primaryEmail}" class="textField" id="name">
      <input type="hidden" name="cellPhoneNumber" value="${it.person.cellPhoneNumber}" class="textField" id="name">
      <input type="hidden" name="originalPassword" value="${it.user.password}" class="textField" id="originalPassword">
    </form>
  </div>
</div>