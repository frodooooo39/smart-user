/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.service.impl;

import com.smartitengineering.dao.common.queryparam.FetchMode;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import com.smartitengineering.dao.common.queryparam.QueryParameterFactory;
import com.smartitengineering.dao.impl.hibernate.AbstractCommonDaoImpl;
import com.smartitengineering.user.domain.SecuredObject;
import com.smartitengineering.user.domain.UniqueConstrainedField;
import com.smartitengineering.user.service.ExceptionMessage;
import com.smartitengineering.user.service.SecuredObjectService;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleStateException;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author russel
 */
public class SecuredObjectServiceImpl extends AbstractCommonDaoImpl<SecuredObject> implements SecuredObjectService {

  public SecuredObjectServiceImpl() {
    setEntityClass(SecuredObject.class);
  }

  @Override
  public void save(SecuredObject securedObject) {
    validateSecuredObject(securedObject);
    final Date date = new Date();
    securedObject.setCreationDate(date);
    securedObject.setLastModifiedDate(date);
    try {
      super.save(securedObject);
    }
    catch (ConstraintViolationException ex) {
      String message = ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.OTHER;
      throw new RuntimeException(message, ex);
    }
    catch (StaleStateException ex) {
      String message =
             ExceptionMessage.STALE_OBJECT_STATE_EXCEPTION.name() + "-" + UniqueConstrainedField.OTHER;
      throw new RuntimeException(message, ex);
    }
  }

  @Override
  public void update(SecuredObject securedObject) {
    validateSecuredObject(securedObject);
    final Date date = new Date();
    securedObject.setLastModifiedDate(date);
    try {
      super.update(securedObject);
    }
    catch (ConstraintViolationException ex) {
      String message = ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.OTHER;
      throw new RuntimeException(message, ex);
    }
    catch (StaleStateException ex) {
      String message =
             ExceptionMessage.STALE_OBJECT_STATE_EXCEPTION.name() + "-" + UniqueConstrainedField.OTHER;
      throw new RuntimeException(message, ex);
    }
  }

  @Override
  public void delete(SecuredObject securedObject) {
    try {
      super.delete(securedObject);
    }
    catch (RuntimeException e) {
      String message = ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" +
          UniqueConstrainedField.ORGANIZATION;
      throw new RuntimeException(message, e);
    }
  }

  @Override
  public Collection<SecuredObject> getByOrganization(String organizationName) {
    Collection<SecuredObject> securedObjects = new HashSet<SecuredObject>();
    QueryParameter qp = QueryParameterFactory.getNestedParametersParam("organization", FetchMode.DEFAULT, QueryParameterFactory.
        getEqualPropertyParam("uniqueShortName", organizationName));
    return super.getList(qp);
  }

  @Override
  public SecuredObject getByOrganizationAndObjectID(String organizationName, String objectID) {
    return super.getSingle(QueryParameterFactory.getEqualPropertyParam("objectID", objectID), QueryParameterFactory.
        getNestedParametersParam("organization", FetchMode.DEFAULT, QueryParameterFactory.getEqualPropertyParam(
        "uniqueShortName", organizationName)));

  }

  public void validateSecuredObject(SecuredObject securedObject) {
    if (StringUtils.isEmpty(securedObject.getObjectID())) {
      throw new RuntimeException(ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.SECURED_OBJECT_OBJECT_ID.
          name());
    }
    if (securedObject.getId() == null) {
      Integer count = (Integer) super.getOther(
          QueryParameterFactory.getElementCountParam("objectID"), QueryParameterFactory.getConjunctionParam(
          QueryParameterFactory.getEqualPropertyParam("organization.id", securedObject.getOrganization().getId()), QueryParameterFactory.
          getEqualPropertyParam("objectID", securedObject.getObjectID())), QueryParameterFactory.getEqualPropertyParam(
          "name", securedObject.getName()));
      if (count.intValue() > 0) {
        throw new RuntimeException(ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.SECURED_OBJECT_OBJECT_ID.
            name());
      }
    }
    else {
      Integer count = (Integer) super.getOther(
          QueryParameterFactory.getElementCountParam("objectID"),
          QueryParameterFactory.getConjunctionParam(
          QueryParameterFactory.getNotEqualPropertyParam("id", securedObject.getId()), QueryParameterFactory.
          getEqualPropertyParam("organization.id", securedObject.getOrganization().getId()), QueryParameterFactory.
          getEqualPropertyParam("objectID", securedObject.getObjectID()), QueryParameterFactory.getEqualPropertyParam(
          "name", securedObject.getName())));
      if (count.intValue() > 0) {
        throw new RuntimeException(ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.SECURED_OBJECT_OBJECT_ID.
            name());
      }

    }
  }

  @Override
  public SecuredObject getByOrganizationAndName(String organizationName, String name) {
    return super.getSingle(QueryParameterFactory.getEqualPropertyParam("name", name), QueryParameterFactory.
        getNestedParametersParam("organization", FetchMode.DEFAULT, QueryParameterFactory.getEqualPropertyParam(
        "uniqueShortName", organizationName)));
  }

  @Override
  public SecuredObject getById(Long id) {
    return getById(id);
  }
}
