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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilderException;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author russel
 */
@Path("/orgs/sn/{uniqueShortName}/usergroups/name/{name}")
public class OrganizationUserGroupResource extends AbstractResource {

  static final Method ORGANIZATION_USER_GROUP_CONTENT_METHOD;

  static {
    try {
      ORGANIZATION_USER_GROUP_CONTENT_METHOD = OrganizationUserGroupResource.class.getMethod("getContent");
    }
    catch (Exception ex) {
      throw new InstantiationError();
    }
  }
  private String orgShortName;
  private String name;
  private Organization organization;
  private UserGroup userGroup;
  private final String REL_USER_GROUP_PRIVILEGES = "privileges";
  private final String REL_USER_GROUP_ROLES = "roles";
  private final String REL_USER_GROUP_USERS = "users";

  public OrganizationUserGroupResource(@PathParam("uniqueShortName") String orgName, @PathParam("name") String groupName) {
    this.orgShortName = orgName;
    this.name = groupName;
    organization = getOrganization();
    userGroup = getUserGroup();
  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    ResponseBuilder responseBuilder = Response.ok();
    if (organization == null || userGroup == null) {
      return responseBuilder.status(Status.NOT_FOUND).build();
    }
    Feed userFeed = getUserGroupFeed();
    responseBuilder = Response.ok(userFeed);
    return responseBuilder.build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/content")
  public Response getContent() {
    ResponseBuilder responseBuilder = Response.ok();
    if (organization == null || userGroup == null) {
      return responseBuilder.status(Status.NOT_FOUND).build();
    }
    responseBuilder = Response.ok(userGroup);
    return responseBuilder.build();
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHtml() {
    ResponseBuilder responseBuilder = Response.ok();
    if (organization == null || userGroup == null) {
      return responseBuilder.status(Status.NOT_FOUND).build();
    }
    Viewable view = new Viewable("OrganizationUserDetails", userGroup, OrganizationResource.class);
    responseBuilder.entity(view);
    return responseBuilder.build();
  }

  @PUT
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response update(UserGroup newUserGroup) {
    ResponseBuilder responseBuilder = Response.status(Status.SERVICE_UNAVAILABLE);
    if (organization == null || userGroup == null) {
      return responseBuilder.status(Status.NOT_FOUND).build();
    }
    try {
      newUserGroup.setOrganization(organization);
      Services.getInstance().getUserGroupService().update(newUserGroup);
      responseBuilder = Response.ok(getUserGroupFeed());
    }
    catch (Exception ex) {
      responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);

    }
    return responseBuilder.build();
  }

  private Feed getUserGroupFeed() throws UriBuilderException, IllegalArgumentException {
    UserGroup userGroupForFeed = userGroup;
    Feed userFeed = getFeed(userGroupForFeed.getName(), new Date());
    userFeed.setTitle(userGroupForFeed.getName());

    // add a self link
    userFeed.addLink(getSelfLink());

    // add a edit link
    Link editLink = getAbderaFactory().newLink();
    editLink.setHref(getUriInfo().getRequestUri().toString());
    editLink.setRel(Link.REL_EDIT);
    editLink.setMimeType(MediaType.APPLICATION_JSON);
    userFeed.addLink(editLink);

    // add a alternate link
    Link altLink = getAbderaFactory().newLink();
    altLink.setHref(getRelativeURIBuilder().path(OrganizationUserGroupResource.class).path(
        ORGANIZATION_USER_GROUP_CONTENT_METHOD).build(orgShortName, userGroup.getName()).toString());
    altLink.setRel(Link.REL_ALTERNATE);
    altLink.setMimeType(MediaType.APPLICATION_JSON);
    userFeed.addLink(altLink);

    Link privilegesLink = getAbderaFactory().newLink();
    privilegesLink.setHref(getRelativeURIBuilder().path(UserGroupPrivilegesResource.class).build(orgShortName, name).
        toString());
    privilegesLink.setRel(REL_USER_GROUP_PRIVILEGES);
    privilegesLink.setMimeType(MediaType.APPLICATION_JSON);
    userFeed.addLink(privilegesLink);

    Link rolesLink = getAbderaFactory().newLink();
    rolesLink.setHref(getRelativeURIBuilder().path(UserGroupRolesResource.class).build(orgShortName, name).toString());
    rolesLink.setRel(REL_USER_GROUP_ROLES);
    rolesLink.setMimeType(MediaType.APPLICATION_JSON);
    userFeed.addLink(rolesLink);

    Link usersLink = getAbderaFactory().newLink();
    usersLink.setHref(getRelativeURIBuilder().path(UserGroupUsersResource.class).build(orgShortName, name).toString());
    usersLink.setRel(REL_USER_GROUP_USERS);
    usersLink.setMimeType(MediaType.APPLICATION_JSON);
    userFeed.addLink(usersLink);


    return userFeed;
  }

  @DELETE
  public Response delete() {
    ResponseBuilder responseBuilder = Response.ok();
    if (organization == null || userGroup == null) {
      return responseBuilder.status(Status.NOT_FOUND).build();
    }
    Services.getInstance().getUserGroupService().delete(userGroup);
    return responseBuilder.build();
  }

  @POST
  @Path("/delete")
  public Response deletePost() {
    ResponseBuilder responseBuilder = Response.ok();
    if (organization == null || userGroup == null) {
      return responseBuilder.status(Status.NOT_FOUND).build();
    }
    try {
      Services.getInstance().getUserGroupService().delete(userGroup);
    }
    catch (Exception ex) {
      responseBuilder = Response.ok(Status.INTERNAL_SERVER_ERROR);
    }
    return responseBuilder.build();
  }

  @POST
  @Path("/update")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response updatePost(@HeaderParam("Content-type") String contentType, String message) {
    ResponseBuilder responseBuilder = Response.status(Status.SERVICE_UNAVAILABLE);
    if (organization == null || userGroup == null) {
      return responseBuilder.status(Status.NOT_FOUND).build();
    }
    if (StringUtils.isBlank(message)) {
      responseBuilder = Response.status(Status.BAD_REQUEST);
      responseBuilder.build();
    }

    final boolean isHtmlPost;
    if (StringUtils.isBlank(contentType)) {
      contentType = MediaType.APPLICATION_OCTET_STREAM;
      isHtmlPost = false;
    }
    else if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
      contentType = MediaType.APPLICATION_OCTET_STREAM;
      isHtmlPost = true;
      try {
        //Will search for the first '=' if not found will take the whole string
        final int startIndex = 0;//message.indexOf("=") + 1;
        //Consider the first '=' as the start of a value point and take rest as value
        final String realMsg = message.substring(startIndex);
        //Decode the message to ignore the form encodings and make them human readable
        message = URLDecoder.decode(realMsg, "UTF-8");
      }
      catch (UnsupportedEncodingException ex) {
      }
    }
    else {
      isHtmlPost = false;
    }

    if (isHtmlPost) {
      UserGroup newUserGroup = getUserGroupFromContent(message);
      try {
        newUserGroup.setOrganization(organization);
        Services.getInstance().getUserGroupService().update(newUserGroup);
        responseBuilder = Response.ok(getUserGroupFeed());
      }
      catch (Exception ex) {
        responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
      }
    }
    return responseBuilder.build();
  }

  private UserGroup getUserGroupFromContent(String message) {
    Map<String, String> keyValueMap = new HashMap<String, String>();

    String[] keyValuePairs = message.split("&");

    for (int i = 0; i < keyValuePairs.length; i++) {
      String[] keyValuePair = keyValuePairs[i].split("=");
      keyValueMap.put(keyValuePair[0], keyValuePair[1]);
    }

    UserGroup newUserGroup = new UserGroup();
    if (keyValueMap.get("id") != null) {
      newUserGroup.setId(NumberUtils.toLong(keyValueMap.get("id")));
    }
    if (keyValueMap.get("name") != null) {
      newUserGroup.setName(keyValueMap.get("name"));
    }
    return newUserGroup;
  }

  private UserGroup getUserGroup() {
    return Services.getInstance().getUserGroupService().getByOrganizationAndUserGroupName(orgShortName, name);
  }

  private Organization getOrganization() {
    return Services.getInstance().getOrganizationService().getOrganizationByUniqueShortName(orgShortName);
  }

  @Override
  protected String getAuthor() {
    return "Smart User";
  }
}
