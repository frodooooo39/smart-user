/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.client.api;

import com.smartitengineering.util.rest.client.ResourceLink;

/**
 *
 * @author russel
 */
public interface RootResource {

  //public OrganizationsResource getOrganizationsResource();
  LoginResource getLoginResource();

  ResourceLink getLoginLink();

  UriTemplateResource getTemplateResource();
}
