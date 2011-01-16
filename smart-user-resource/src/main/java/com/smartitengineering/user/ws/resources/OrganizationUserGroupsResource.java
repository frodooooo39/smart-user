/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.ws.resources;

import com.smartitengineering.user.service.Services;
import com.smartitengineering.user.domain.Organization;
import com.smartitengineering.user.domain.UserGroup;
import com.smartitengineering.util.rest.atom.server.AbstractResource;
import com.sun.jersey.api.view.Viewable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

/**
 *
 * @author russel
 */
@Path("/orgs/sn/{uniqueShortName}/usergroups")
public class OrganizationUserGroupsResource extends AbstractResource {

  private String organizationUniqueShortName;
  static final Method ORGANIZATIONS_USER_GROUPS_BEFORE_NAME_METHOD;
  static final Method ORGANIZATIONS_USER_GROUPS_AFTER_NAME_METHOD;

  static {
    try {
      ORGANIZATIONS_USER_GROUPS_AFTER_NAME_METHOD = OrganizationUsersResource.class.getMethod("getAfter", String.class);
    }
    catch (Exception ex) {
      throw new InstantiationError();
    }
    try {
      ORGANIZATIONS_USER_GROUPS_BEFORE_NAME_METHOD =
      OrganizationUsersResource.class.getMethod("getBefore", String.class);
    }
    catch (Exception ex) {
      throw new InstantiationError();
    }

  }
  @PathParam("count")
  private Integer count;
  private Organization organization;

  public OrganizationUserGroupsResource(@PathParam("uniqueShortName") String organizationUniqueShortName) {
    this.organizationUniqueShortName = organizationUniqueShortName;
    organization = getOrganization();
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHtml() {
    ResponseBuilder responseBuilder = Response.ok();
    if (organization == null) {
      return responseBuilder.status(Status.NOT_FOUND).build();
    }
    Collection<UserGroup> userGroups = Services.getInstance().getUserGroupService().getByOrganizationName(
        organizationUniqueShortName);

    Viewable view = new Viewable("userList", userGroups, OrganizationUsersResource.class);
    responseBuilder.entity(view);
    return responseBuilder.build();

  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Path("/before/{beforeUserGroupName}")
  public Response getBefore(@PathParam("beforeUserGroupName") String beforeUserGroupName) {
    return get(organizationUniqueShortName, beforeUserGroupName, true);
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Path("/after/{afterUserGroupName}")
  public Response getAfter(@PathParam("afterUserGroupName") String afterUserGroupName) {
    return get(organizationUniqueShortName, afterUserGroupName, false);
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    return get(organizationUniqueShortName, null, true);
  }

  private Response get(String uniqueOrganizationName, String userName, boolean isBefore) {

    if (count == null) {
      count = 10;
    }
    ResponseBuilder responseBuilder = Response.ok();
    if (organization == null) {
      return responseBuilder.status(Status.NOT_FOUND).build();
    }
    Feed atomFeed = getFeed(userName, new Date());

    Link parentLink = getAbderaFactory().newLink();
    parentLink.setHref(UriBuilder.fromResource(RootResource.class).build().toString());
    parentLink.setRel("parent");
    atomFeed.addLink(parentLink);


    Collection<UserGroup> userGroups = Services.getInstance().getUserGroupService().getByOrganizationName(
        uniqueOrganizationName);


    if (userGroups != null && !userGroups.isEmpty()) {

      MultivaluedMap<String, String> queryParam = getUriInfo().getQueryParameters();
      List<UserGroup> userGroupList = new ArrayList<UserGroup>(userGroups);

      // uri builder for next and previous organizations according to count
      final UriBuilder nextUri = getRelativeURIBuilder().path(OrganizationUserGroupsResource.class).path(ORGANIZATIONS_USER_GROUPS_AFTER_NAME_METHOD);
      final UriBuilder previousUri = getRelativeURIBuilder().path(OrganizationUserGroupsResource.class).path(ORGANIZATIONS_USER_GROUPS_BEFORE_NAME_METHOD);

      // link to the next organizations based on count
      Link nextLink = getAbderaFactory().newLink();
      nextLink.setRel(Link.REL_NEXT);
      UserGroup lastUserGroup = userGroupList.get(0);


      for (String key : queryParam.keySet()) {
        final Object[] values = queryParam.get(key).toArray();
        nextUri.queryParam(key, values);
        previousUri.queryParam(key, values);
      }
      nextLink.setHref(nextUri.build(organizationUniqueShortName, lastUserGroup.getName()).toString());


      atomFeed.addLink(nextLink);

      /* link to the previous organizations based on count */
      Link prevLink = getAbderaFactory().newLink();
      prevLink.setRel(Link.REL_PREVIOUS);
      UserGroup firstUser = userGroupList.get(userGroups.size() - 1);

      prevLink.setHref(previousUri.build(organizationUniqueShortName, firstUser.getName()).toString());
      atomFeed.addLink(prevLink);

      for (UserGroup userGroup : userGroups) {

        Entry userEntry = getAbderaFactory().newEntry();

        userEntry.setId(userGroup.getName());
        userEntry.setTitle(userGroup.getName());
        userEntry.setSummary(userGroup.getName());
        userEntry.setUpdated(userGroup.getLastModifiedDate());

        // setting link to the each individual user
        Link userLink = getAbderaFactory().newLink();
        userLink.setHref(getRelativeURIBuilder().path(OrganizationUserGroupResource.class).build(organizationUniqueShortName, userGroup.getName()).toString());
        userLink.setRel(Link.REL_ALTERNATE);
        userLink.setMimeType(MediaType.APPLICATION_ATOM_XML);

        userEntry.addLink(userLink);

        atomFeed.addEntry(userEntry);
      }
    }
    responseBuilder.entity(atomFeed);
    return responseBuilder.build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response post(UserGroup userGroup) {

    ResponseBuilder responseBuilder = Response.ok();
    if (organization == null) {
      return responseBuilder.status(Status.NOT_FOUND).build();
    }
    try {
      userGroup.setOrganization(organization);
      Services.getInstance().getUserGroupService().save(userGroup);
      responseBuilder = Response.status(Status.CREATED);
      responseBuilder.location(getAbsoluteURIBuilder().path(OrganizationUserGroupResource.class).build(organizationUniqueShortName, userGroup.getName().toString()));
    }
    catch (Exception ex) {
      responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);      
    }
    return responseBuilder.build();
  }

  private Organization getOrganization() {
    return Services.getInstance().getOrganizationService().getOrganizationByUniqueShortName(organizationUniqueShortName);
  }

  @Override
  protected String getAuthor() {
    return "Smart User";
  }
}
