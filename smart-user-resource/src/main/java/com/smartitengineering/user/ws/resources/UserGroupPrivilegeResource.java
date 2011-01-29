/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.ws.resources;

import com.smartitengineering.user.service.Services;
import com.smartitengineering.user.domain.Organization;
import com.smartitengineering.user.domain.Privilege;
import com.smartitengineering.user.domain.UserGroup;
import com.smartitengineering.util.rest.atom.server.AbstractResource;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

/**
 *
 * @author modhu7
 */
@Path("/orgs/sn/{organizationUniqueShortName}/usergroups/name/{groupName}/privs/name/{privilegeName}")
public class UserGroupPrivilegeResource extends AbstractResource {

  private String organizationUniqueShortName;
  private String groupName;
  private String privilegeName;
  private static String REL_PRIV = "privilege";
  private Organization organization;
  private UserGroup userGroup;
  private Privilege privilege;

  public UserGroupPrivilegeResource(@PathParam("organizationUniqueShortName") String organizationUniqueShortName, @PathParam(
      "groupName") String groupName, @PathParam("privilegeName") String privilegeName) {
    this.organizationUniqueShortName = organizationUniqueShortName;
    this.groupName = groupName;
    this.privilegeName = privilegeName;
    organization = getOrganization();
    userGroup = getUserGroup();
    privilege = getPrivilege();
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    ResponseBuilder responseBuilder;
    if (organization == null || userGroup == null || privilege == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    try {
      responseBuilder = Response.status(Status.OK);
      Feed privilegeFeed = getUserGroupPrivilegeFeed();
      responseBuilder = Response.ok(privilegeFeed);
    }
    catch (Exception ex) {
      responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
    }
    return responseBuilder.build();
  }

  @DELETE
  public Response delete() {
    ResponseBuilder responseBuilder;
    if (organization == null || userGroup == null || privilege == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    try {
      responseBuilder = Response.status(Status.OK);
      userGroup.getPrivileges().remove(getPrivilege());
      Services.getInstance().getUserGroupService().update(userGroup);
    }
    catch (Exception ex) {      
      responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
    }
    return responseBuilder.build();
  }

  private Feed getUserGroupPrivilegeFeed() {

    Feed privilegeFeed = getAbderaFactory().newFeed();

    privilegeFeed.setId(privilegeName);
    privilegeFeed.setTitle(privilegeName);
    privilegeFeed.addLink(getSelfLink());

    Link altLink = getAbderaFactory().newLink();
    altLink.setHref(getRelativeURIBuilder().path(UserGroupPrivilegeResource.class).build(organizationUniqueShortName,
                                                                                         groupName, privilegeName).
        toString());
    altLink.setRel(Link.REL_ALTERNATE);
    altLink.setMimeType(MediaType.APPLICATION_JSON);
    privilegeFeed.addLink(altLink);

    Link privLink = getAbderaFactory().newLink();
    privLink.setHref(getRelativeURIBuilder().path(OrganizationPrivilegeResource.class).build(organizationUniqueShortName, privilegeName).toString());

    privLink.setRel(REL_PRIV);
    privLink.setMimeType(MediaType.APPLICATION_JSON);
    privilegeFeed.addLink(privLink);

    return privilegeFeed;
  }

  private Privilege getPrivilege() {
    return Services.getInstance().getPrivilegeService().getPrivilegeByOrganizationAndPrivilegeName(
        organizationUniqueShortName, privilegeName);
  }

  private Organization getOrganization() {
    return Services.getInstance().getOrganizationService().getOrganizationByUniqueShortName(organizationUniqueShortName);
  }

  private UserGroup getUserGroup() {
    return Services.getInstance().getUserGroupService().getByOrganizationAndUserGroupName(organizationUniqueShortName,
                                                                                          groupName);
  }

  @Override
  protected String getAuthor() {
    return "Smart User";
  }
}
