/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.client.impl.domain;

/**
 *
 * @author modhu7
 */
public class Name implements com.smartitengineering.user.client.api.Name {

  private String firstName;
  private String lastName;
  private String middleInitial;

  @Override
  public String getFirstName() {
    return firstName;
  }

  @Override
  public String getLastName() {
    return lastName;
  }

  @Override
  public String getMiddleInitial() {
    return middleInitial;
  }

  @Override
  public void setFirstName(String firstName) {
    if (firstName == null) {
      return;
    }
    this.firstName = firstName;
  }

  @Override
  public void setLastName(String lastName) {
    if (lastName == null) {
      return;
    }
    this.lastName = lastName;
  }

  @Override
  public void setMiddleInitial(String middleInitial) {
    if (middleInitial == null) {
      return;
    }
    this.middleInitial = middleInitial;
  }
}
