/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.service.impl;

import com.smartitengineering.dao.common.queryparam.Order;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import com.smartitengineering.dao.common.queryparam.QueryParameterFactory;
import com.smartitengineering.dao.impl.hibernate.AbstractCommonDaoImpl;
import com.smartitengineering.user.domain.Organization;
import com.smartitengineering.user.domain.UniqueConstrainedField;
import com.smartitengineering.user.filter.OrganizationFilter;
import com.smartitengineering.user.observer.CRUDObservable;
import com.smartitengineering.user.observer.ObserverNotification;
import com.smartitengineering.user.service.ExceptionMessage;
import com.smartitengineering.user.service.OrganizationService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleStateException;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author modhu7
 */
public class OrganizationServiceImpl extends AbstractCommonDaoImpl<Organization> implements OrganizationService {

  private CRUDObservable observable;

  public CRUDObservable getObservable() {
    return observable;
  }

  public void setObservable(CRUDObservable observable) {
    this.observable = observable;
  }

  public OrganizationServiceImpl() {
    setEntityClass(Organization.class);
  }

  @Override
  public void save(Organization organization) {
    final Date date = new Date();
    organization.setCreationDate(date);
    organization.setLastModifiedDate(date);
    validateOrganization(organization);
    try {
      super.save(organization);
      observable.notifyObserver(ObserverNotification.CREATE_ORGANIZATION, organization);
    }
    catch (ConstraintViolationException e) {
      String message = ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.OTHER;
      throw new RuntimeException(message, e);
    }
    catch (StaleStateException e) {
      String message =
             ExceptionMessage.STALE_OBJECT_STATE_EXCEPTION.name() + "-" + UniqueConstrainedField.OTHER;
      throw new RuntimeException(message, e);
    }
  }

  @Override
  public void update(Organization organization) {
    final Date date = new Date();
    organization.setLastModifiedDate(date);
    validateOrganization(organization);
    try {
      super.update(organization);
    }
    catch (ConstraintViolationException e) {
      String message = ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.OTHER;
      throw new RuntimeException(message, e);
    }
    catch (StaleStateException e) {
      String message =
             ExceptionMessage.STALE_OBJECT_STATE_EXCEPTION.name() + "-" + UniqueConstrainedField.OTHER;

      throw new RuntimeException(message, e);
    }
  }

  @Override
  public Collection<Organization> getAllOrganization() {
    Collection<Organization> organizations = new HashSet<Organization>();
    try {
      organizations = super.getAll();
    }
    catch (Exception e) {
    }
    return organizations;
  }

  @Override
  public Collection<Organization> getOrganizations(String organizationNameLike, String shortName, boolean isSmallerThan,
                                                   int count) {

    List<QueryParameter> params = new ArrayList<QueryParameter>();


    if (StringUtils.isNotBlank(organizationNameLike)) {
      final QueryParameter orgNameLikeParam = QueryParameterFactory.getStringLikePropertyParam("name",
                                                                                               organizationNameLike);
      params.add(orgNameLikeParam);
    }

    if (StringUtils.isNotBlank(shortName)) {
      if (isSmallerThan) {
        params.add(QueryParameterFactory.getLesserThanPropertyParam("uniqueShortName", shortName));
      }
      else {
        params.add(QueryParameterFactory.getGreaterThanPropertyParam("uniqueShortName", shortName));
      }
    }

    params.add(QueryParameterFactory.getMaxResultsParam(count));
    params.add(QueryParameterFactory.getOrderByParam("uniqueShortName", isSmallerThan ? Order.DESC : Order.ASC));
    params.add(QueryParameterFactory.getDistinctPropProjectionParam("uniqueShortName"));

    List<String> organizationShortNames = getOtherList(params);

    if (organizationShortNames != null && !organizationShortNames.isEmpty()) {
      List<Organization> organizations = new ArrayList<Organization>(getByUniqueShortNames(organizationShortNames));
      Collections.sort(organizations, new Comparator<Organization>() {

        @Override
        public int compare(Organization o1, Organization o2) {
          return o1.getUniqueShortName().toUpperCase().compareTo(o2.getUniqueShortName().toUpperCase());
        }
      });

      return organizations;
    }
    else {
      return Collections.emptySet();
    }
  }

  @Override
  public Organization getOrganizationByUniqueShortName(String uniqueShortName) {
    QueryParameter qp;
    qp = QueryParameterFactory.getEqualPropertyParam("uniqueShortName", uniqueShortName);
    Organization organization = new Organization("", "");
    try {
      organization = super.getSingle(qp);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return organization;
  }

  public List<Organization> getByUniqueShortNames(List<String> shortNames) {

    QueryParameter<String> param = QueryParameterFactory.<String>getIsInPropertyParam("uniqueShortName", shortNames.
        toArray(new String[0]));

    Collection<Organization> result;
    try {
      result = getList(param);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      result = Collections.<Organization>emptyList();
    }
    return new ArrayList<Organization>(result);

  }

  @Override
  public void validateOrganization(Organization organization) {

    if (StringUtils.isEmpty(organization.getUniqueShortName())) {
      throw new RuntimeException(ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.ORGANIZATION_UNIQUE_SHORT_NAME.
          name());
    }

    if (organization.getId() == null) {
      Integer count = (Integer) super.getOther(
          QueryParameterFactory.getElementCountParam("uniqueShortName"), QueryParameterFactory.
          getStringLikePropertyParam(
          "uniqueShortName",
          organization.getUniqueShortName()));
      if (count.intValue() > 0) {
        throw new RuntimeException(ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.ORGANIZATION_UNIQUE_SHORT_NAME.
            name());
      }
    }
    else {
      Integer count = (Integer) super.getOther(
          QueryParameterFactory.getElementCountParam("uniqueShortName"),
          QueryParameterFactory.getConjunctionParam(
          QueryParameterFactory.getNotEqualPropertyParam("id",
                                                         organization.getId()), QueryParameterFactory.
          getStringLikePropertyParam(
          "uniqueShortName", organization.getUniqueShortName())));
      if (count.intValue() > 0) {
        throw new RuntimeException(ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.ORGANIZATION_UNIQUE_SHORT_NAME.
            name());
      }
    }
  }

  @Override
  public void delete(Organization organization) {
    try {
      observable.notifyObserver(ObserverNotification.DELETE_ORGNIZATION, organization);
      super.delete(organization);
    }
    catch (RuntimeException e) {
      String message = ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" +
          UniqueConstrainedField.ORGANIZATION;
      throw new RuntimeException(message, e);
    }
  }

  @Override
  public Collection<Organization> search(OrganizationFilter filter) {
    QueryParameter qp;
    List<QueryParameter> queryParameters = new ArrayList<QueryParameter>();
    if (!StringUtils.isEmpty(filter.getOrganizationUniqueShortName())) {
      qp = QueryParameterFactory.getEqualPropertyParam("uniqueShortName",
                                                       filter.getOrganizationUniqueShortName());
      queryParameters.add(qp);
    }
    Collection<Organization> organizations = new HashSet<Organization>();
    if (queryParameters.isEmpty()) {
      try {
        organizations = super.getAll();
      }
      catch (Exception e) {
      }
    }
    else {
      organizations = super.getList(queryParameters);
    }
    return organizations;
  }
}
