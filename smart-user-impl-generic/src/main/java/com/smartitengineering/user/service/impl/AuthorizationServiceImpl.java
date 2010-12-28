/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.service.impl;

import com.smartitengineering.user.domain.Privilege;
import com.smartitengineering.user.domain.SecuredObject;
import com.smartitengineering.user.domain.User;
import com.smartitengineering.user.domain.UserGroup;
import com.smartitengineering.user.parser.SmartUserStrings;
import com.smartitengineering.user.service.AuthorizationService;
import com.smartitengineering.user.service.SecuredObjectService;
import com.smartitengineering.user.service.UserGroupService;
import com.smartitengineering.user.service.UserService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.vote.AccessDecisionVoter;

/**
 *
 * @author modhu7
 */
public class AuthorizationServiceImpl implements AuthorizationService {

  private UserService userService;
  private SecuredObjectService securedObjectService;
  private UserGroupService userGroupService;

  public SecuredObjectService getSecuredObjectService() {
    return securedObjectService;
  }

  public void setSecuredObjectService(SecuredObjectService securedObjectService) {
    this.securedObjectService = securedObjectService;
  }

  public UserService getUserService() {
    return userService;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public UserGroupService getUserGroupService() {
    return userGroupService;
  }

  public void setUserGroupService(UserGroupService userGroupService) {
    this.userGroupService = userGroupService;
  }

  @Override
  public Integer authorize(String username, String organizationName, String oid, Integer permission) {

    User user = userService.getUserByOrganizationAndUserName(organizationName, username);
    if (user == null) {
      return AccessDecisionVoter.ACCESS_DENIED;
    }
    if (user != null && oid == null) {
      return AccessDecisionVoter.ACCESS_ABSTAIN;
    }
    if (oid.contains(SmartUserStrings.PRIVILEGES_URL) || oid.contains(SmartUserStrings.ROLES_URL)) {
      return AuthorizeForPrivilegeAndRoleOperations(oid, username, organizationName, permission);
    }
    for (Privilege privilege : user.getPrivileges()) {
      if (oid.startsWith(privilege.getSecuredObject().getObjectID()) && (permission.intValue() & privilege.
                                                                         getPermissionMask().intValue()) == permission.
          intValue()) {
        return AccessDecisionVoter.ACCESS_GRANTED;
      }
    }
    SecuredObject securedObject = securedObjectService.getByOrganizationAndObjectID(organizationName, oid);
    if (user != null && securedObject == null) {
      return AccessDecisionVoter.ACCESS_ABSTAIN;
    }
    else if (authorize(user.getPrivileges(), securedObject, permission) == AccessDecisionVoter.ACCESS_GRANTED) {
      return AccessDecisionVoter.ACCESS_GRANTED;
    }
    else {
      List<UserGroup> userGroups = new ArrayList<UserGroup>(userGroupService.getUserGroupsByUser(user));
      for (UserGroup userGroup : userGroups) {
        if (authorize(userGroup.getPrivileges(), securedObject, permission) == AccessDecisionVoter.ACCESS_GRANTED) {
          return AccessDecisionVoter.ACCESS_GRANTED;
        }
      }
      return AccessDecisionVoter.ACCESS_ABSTAIN;
    }
  }

  private Integer authorize(Collection<Privilege> privileges, SecuredObject securedObject, Integer permission) {
    for (Privilege privilege : privileges) {
      if (privilege.getSecuredObject().getObjectID().equals(securedObject.getObjectID()) && (permission.intValue() & privilege.
                                                                                             getPermissionMask().
                                                                                             intValue()) == permission.
          intValue()) {
        return AccessDecisionVoter.ACCESS_GRANTED;
      }
    }
    if (StringUtils.isNotBlank(securedObject.getParentObjectID())) {
      SecuredObject parentSecuredObject = securedObjectService.getByOrganizationAndObjectID(securedObject.
          getOrganization().
          getUniqueShortName(), securedObject.getParentObjectID());
      if (parentSecuredObject == null) {
        return AccessDecisionVoter.ACCESS_ABSTAIN;
      }
      return authorize(privileges, securedObjectService.getByOrganizationAndObjectID(securedObject.getOrganization().
          getUniqueShortName(), securedObject.getParentObjectID()), permission);
    }
    else {
      return AccessDecisionVoter.ACCESS_DENIED;
    }
  }

  @Override
  public Boolean login(String username, String password) {
    User user = userService.getUserByUsername(username);
    if (user != null && user.getPassword().equals(password)) {
      return true;
    }
    else {
      return false;
    }
  }

  public Integer AuthorizeForPrivilegeAndRoleOperations(String oid, String username, String organizationName,
                                                        int permission) {
    if (permission == BasePermission.READ.getMask() && oid.contains(SmartUserStrings.USERS_URL +
        SmartUserStrings.USER_UNIQUE_URL_FRAGMENT + "/" + username)) {
      return authorize(username, organizationName, SmartUserStrings.ORGANIZATIONS_URL +
          SmartUserStrings.ORGANIZATION_UNIQUE_URL_FRAGMENT + "/" + organizationName + SmartUserStrings.USERS_URL +
          SmartUserStrings.USER_UNIQUE_URL_FRAGMENT + "/" + username, permission);
    }
    else {
      return authorize(username, organizationName, SmartUserStrings.ORGANIZATIONS_URL +
          SmartUserStrings.ORGANIZATION_UNIQUE_URL_FRAGMENT + "/" + organizationName, permission);
    }
  }
}
