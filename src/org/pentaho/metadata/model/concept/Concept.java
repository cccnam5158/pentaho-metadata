/*
 * Copyright 2009 Pentaho Corporation.  All rights reserved.
 * This software was developed by Pentaho Corporation and is provided under the terms
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.metadata.model.concept;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.metadata.model.concept.types.LocalizedString;

/**
 * This is the base implementation of a concept, and may be used in generic terms
 * when defining parent concepts or modeling metadata.  More concrete implementations
 * extend the Concept class within Pentaho Metadata, found in the 
 * org.pentaho.metadata.model package.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class Concept implements IConcept, Serializable, Cloneable, Comparable {
  
  public Concept() {
    super();
  }

  private static final long serialVersionUID = -6912836203678095834L;

  public static String NAME_PROPERTY = "name"; //$NON-NLS-1$
  public static String DESCRIPTION_PROPERTY = "description"; //$NON-NLS-1$
  public static String SECURITY_PROPERTY = "security"; //$NON-NLS-1$
  
  Map<String, Object> properties = new HashMap<String, Object>();
  String id;
  IConcept parent;
  
  public Map<String, Object> getChildProperties() {
    return properties;
  }
  
  public void setChildProperties(Map<String, Object> properties) {
    this.properties = properties;
  }

  public Object getChildProperty(String name) {
    return properties.get(name);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public IConcept getInheritedConcept() {
    return null;
  }
  

  public IConcept getParentConcept() {
    return parent;
  }
  
  public void setParentConcept(IConcept parent) {
    this.parent = parent;
  }

  
  public IConcept getSecurityParentConcept() {
    return null;
  }
  
  public Map<String, Object> getProperties() {
    Map<String,Object> all = new HashMap<String,Object>();

    // Properties inherited from the "logical relationship": 
    // BusinessColumn inherits from Physical Column, B.Table from Ph.Table
    if (getInheritedConcept() != null) {
      all.putAll(getInheritedConcept().getProperties());
    }

    // Properties inherited from the pre-defined concepts like 
    // "Base", "ID", "Name", "Description", etc.
    //
    if (parent != null) {
      all.putAll(parent.getProperties());
    }

    // The security settings from the security parent: 
    // Business table inherits from Business model, business column from business table
    if (getSecurityParentConcept() != null) {
      // Only take over the security information, nothing else
      Object securityProperty = (Object) getSecurityParentConcept().getProperty(SECURITY_PROPERTY);
      if (securityProperty!=null) {
        all.put(SECURITY_PROPERTY, securityProperty);
      }
    }

    // The child properties overwrite everything else.
    all.putAll(properties);

    return all;
  }

  public Object getProperty(String name) {
    return getProperties().get(name);
  }

  public void setProperty(String name, Object property) {
    properties.put(name, property);
  }

  public void removeChildProperty(String name) {
    properties.remove(name);
  }
  
  public LocalizedString getName() {
    return (LocalizedString)getProperty(NAME_PROPERTY);
  }

  public String getName(String locale) {
    LocalizedString locName = getName();
    if (locName == null) {
      return getId();
    }
    String name = locName.getLocalizedString(locale);
    if (name == null || name.trim().length() == 0) {
      return getId();
    }
    return name;
  }
  
  public void setName(LocalizedString name) {
    setProperty(NAME_PROPERTY, name);
  }
  
  public String getDescription(String locale) {
    LocalizedString locDesc = getDescription();
    if (locDesc == null) {
      return getId();
    }
    String name = locDesc.getLocalizedString(locale);
    if (name == null || name.trim().length() == 0) {
      return getId();
    }
    return name;
  }

  
  public LocalizedString getDescription() {
    return (LocalizedString)getProperty(DESCRIPTION_PROPERTY);
  }
  
  public void setDescription(LocalizedString description) {
    setProperty(DESCRIPTION_PROPERTY, description);
  }

  public int compareTo(Object o) {
    Concept c = (Concept)o;
    return getId().compareTo(c.getId());
  }
  
  public Object clone() {
    return clone(new Concept());
  }
  
  protected Object clone(Concept clone) {
    clone.setId(getId());
    
    // shallow references
    clone.setChildProperties(getChildProperties());
    clone.setParentConcept(getParentConcept());
    return clone;
  }
}
