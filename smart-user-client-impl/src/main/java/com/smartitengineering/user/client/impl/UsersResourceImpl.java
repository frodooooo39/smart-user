/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.client.impl;

import com.smartitengineering.user.client.api.UserFilter;
import com.smartitengineering.user.client.api.UserPerson;
import com.smartitengineering.user.client.api.UserResource;
import com.smartitengineering.user.client.api.UsersResource;
import com.smartitengineering.util.rest.atom.AbstractFeedClientResource;
import com.smartitengineering.util.rest.atom.AtomClientUtil;
import com.smartitengineering.util.rest.client.ClientUtil;
import com.smartitengineering.util.rest.client.Resource;
import com.smartitengineering.util.rest.client.ResourceLink;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

/**
 *
 * @author russel
 */
class UsersResourceImpl extends AbstractFeedClientResource<UsersResource> implements UsersResource {
  private String REL_USER = "User";

  public UsersResourceImpl(ResourceLink link, Resource referrer) {
    super(referrer, link);
  }

  @Override
  protected void processClientConfig(ClientConfig clientConfig) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected UsersResource instantiatePageableResource(ResourceLink link) {
    return new UsersResourceImpl(link, this);
  }

  @Override
  public List<UserResource> getUserResources() {
    List<UserResource> userResources = new ArrayList<UserResource>();
    for (Entry entry : getLastReadStateOfEntity().getEntries()) {
      userResources.add(new UserResourceImpl(AtomClientUtil.convertFromAtomLinkToResourceLink(
          entry.getLink(Link.REL_ALTERNATE)), this));
    }

    return userResources;
  }

  @Override
  public UserResource create(UserPerson user) {
    ClientResponse response = post(MediaType.APPLICATION_JSON, user, ClientResponse.Status.CREATED);
    final ResourceLink userLink = ClientUtil.createResourceLink(REL_USER, response.getLocation(),
                                                               MediaType.APPLICATION_ATOM_XML);
    return new UserResourceImpl(userLink, this);
  }

  @Override
  public UsersResource search(UserFilter filter) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
