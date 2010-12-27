/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.client.impl.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 *
 * @author modhu7
 */
public class BasicIdentity extends AbstractClientDomain<Long> implements
    com.smartitengineering.user.client.api.BasicIdentity {

  private com.smartitengineering.user.client.api.Name name;
  private String nationalID;

  @Override
  public com.smartitengineering.user.client.api.Name getName() {
    return name;
  }

  @Override
  public String getNationalID() {
    return nationalID;
  }

  @Override
  public void setNationalID(String nationalID) {
    if (nationalID == null) {
      return;
    }
    this.nationalID = nationalID;
  }

  @Override
  @JsonDeserialize(as = Name.class)
  public void setName(com.smartitengineering.user.client.api.Name name) {
    if (name == null) {
      return;
    }
    this.name = name;
  }
}
