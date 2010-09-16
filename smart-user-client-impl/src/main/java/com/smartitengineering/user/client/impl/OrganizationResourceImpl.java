/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.client.impl;

import com.smartitengineering.smartuser.client.api.Organization;
import com.smartitengineering.smartuser.client.api.OrganizationResource;
import com.smartitengineering.smartuser.client.api.PrivilegesResource;
import com.smartitengineering.smartuser.client.api.SecuredObjectsResource;
import com.smartitengineering.smartuser.client.api.UsersResource;
import com.smartitengineering.util.rest.atom.AbstractFeedClientResource;
import com.smartitengineering.util.rest.atom.AtomClientUtil;
import com.smartitengineering.util.rest.client.Resource;
import com.smartitengineering.util.rest.client.ResourceLink;
import com.smartitengineering.util.rest.client.SimpleResourceImpl;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import javax.ws.rs.core.MediaType;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

/**
 *
 * @author russel
 */
class OrganizationResourceImpl extends AbstractFeedClientResource<Resource<? extends Feed>> implements
    OrganizationResource {

  public static final String REL_ORGS = "organizations";
  public static final String REL_ORG = "Organization";
  public static final String REL_ALT = "alternate";
  public static final String REL_USERS = "users";
  public static final String REL_PRIVILEGES = "privileges";
  public static final String REL_SECUREDOBJECTS = "securedobjects";
  private Resource<? extends Organization> organization;

  public OrganizationResourceImpl(ResourceLink orgLink, Resource referrer) {
    super(referrer, orgLink);
    final ResourceLink altLink = getRelatedResourceUris().getFirst(Link.REL_ALTERNATE);
    organization = new SimpleResourceImpl<com.smartitengineering.user.client.impl.domain.Organization>(
        this, altLink.getUri(), altLink.getMimeType(), com.smartitengineering.user.client.impl.domain.Organization.class,
        null, false, null, null);
  }

  @Override
  public UsersResource getUsersResource() {
    return new UsersResourceImpl(getRelatedResourceUris().getFirst(REL_USERS), this);
  }

  @Override
  public SecuredObjectsResource getSecuredObjectsResource() {
    return new SecuredObjectsResourceImpl(getRelatedResourceUris().getFirst(REL_SECUREDOBJECTS), this);
  }

  @Override
  public PrivilegesResource getPrivilegesResource() {
    return new PrivilegesResourceImpl(getRelatedResourceUris().getFirst(REL_PRIVILEGES), this);
  }

  @Override
  public Organization getOrganization() {
    return organization.getLastReadStateOfEntity();
  }

  @Override
  protected void processClientConfig(ClientConfig clientConfig) {
  }

  @Override
  protected Resource<? extends Feed> instantiatePageableResource(ResourceLink link) {
    return null;
  }

  @Override
  public void update() {
    put(MediaType.APPLICATION_JSON, getOrganization(), ClientResponse.Status.OK, ClientResponse.Status.SEE_OTHER,
        ClientResponse.Status.FOUND);
  }
}
