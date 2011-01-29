/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.domain;

/**
 *
 * @author modhu7
 */
public class Name{

  private String firstName;
  private String lastName;
  private String middleInitial;

  public String getFirstName() {
    if (firstName == null) {
      firstName = "";
    }
    return firstName;
  }

  public void setFirstName(String firstName) {
    if (firstName == null) {
      return;
    }
    this.firstName = firstName;
  }

  public String getLastName() {
    if (lastName == null) {
      lastName = "";
    }
    return lastName;
  }

  public void setLastName(String lastName) {
    if (lastName == null) {
      return;
    }
    this.lastName = lastName;
  }

  public String getMiddleInitial() {
    if (middleInitial == null) {
      middleInitial = "";
    }
    return middleInitial;
  }

  public void setMiddleInitial(String middleInitial) {
    if (middleInitial == null) {
      return;
    }
    this.middleInitial = middleInitial;
  }

  @Override
  public String toString() {
    return "" + firstName + " " + middleInitial + " " + lastName;
  }

}
