package org.apache.tools.ant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.tools.ant.attribute.EnableAttribute;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroDef.Attribute;
import org.apache.tools.ant.taskdefs.MacroInstance;
import org.xml.sax.AttributeList;
import org.xml.sax.helpers.AttributeListImpl;

public class RuntimeConfigurable
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String elementTag = null;
  private List<RuntimeConfigurable> children = null;
  private transient Object wrappedObject = null;
  @Deprecated
  private transient AttributeList attributes;
  private transient boolean namespacedAttribute = false;
  private LinkedHashMap<String, Object> attributeMap = null;
  private StringBuffer characters = null;
  private boolean proxyConfigured = false;
  private String polyType = null;
  private String id = null;
  
  public RuntimeConfigurable(Object proxy, String elementTag)
  {
    setProxy(proxy);
    setElementTag(elementTag);
    if ((proxy instanceof Task)) {
      ((Task)proxy).setRuntimeConfigurableWrapper(this);
    }
  }
  
  public synchronized void setProxy(Object proxy)
  {
    wrappedObject = proxy;
    proxyConfigured = false;
  }
  
  private static class AttributeComponentInformation
  {
    String componentName;
    boolean restricted;
    
    private AttributeComponentInformation(String componentName, boolean restricted)
    {
      this.componentName = componentName;
      this.restricted = restricted;
    }
    
    public String getComponentName()
    {
      return componentName;
    }
    
    public boolean isRestricted()
    {
      return restricted;
    }
  }
  
  private AttributeComponentInformation isRestrictedAttribute(String name, ComponentHelper componentHelper)
  {
    if (!name.contains(":")) {
      return new AttributeComponentInformation(null, false, null);
    }
    String componentName = attrToComponent(name);
    String ns = ProjectHelper.extractUriFromComponentName(componentName);
    if (componentHelper.getRestrictedDefinitions(
      ProjectHelper.nsToComponentName(ns)) == null) {
      return new AttributeComponentInformation(null, false, null);
    }
    return new AttributeComponentInformation(componentName, true, null);
  }
  
  public boolean isEnabled(UnknownElement owner)
  {
    if (!namespacedAttribute) {
      return true;
    }
    ComponentHelper componentHelper = ComponentHelper.getComponentHelper(owner.getProject());
    
    IntrospectionHelper ih = IntrospectionHelper.getHelper(owner.getProject(), EnableAttributeConsumer.class);
    for (Map.Entry<String, Object> entry : attributeMap.entrySet())
    {
      AttributeComponentInformation attributeComponentInformation = isRestrictedAttribute((String)entry.getKey(), componentHelper);
      if (attributeComponentInformation.isRestricted())
      {
        String value = (String)entry.getValue();
        EnableAttribute enable = null;
        try
        {
          enable = (EnableAttribute)ih.createElement(owner
            .getProject(), new EnableAttributeConsumer(null), attributeComponentInformation
            .getComponentName());
        }
        catch (BuildException ex)
        {
          throw new BuildException("Unsupported attribute " + attributeComponentInformation.getComponentName());
        }
        if (enable != null)
        {
          value = owner.getProject().replaceProperties(value);
          if (!enable.isEnabled(owner, value)) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  private String attrToComponent(String a)
  {
    int p1 = a.lastIndexOf(':');
    int p2 = a.lastIndexOf(':', p1 - 1);
    return a.substring(0, p2) + a.substring(p1);
  }
  
  synchronized void setCreator(IntrospectionHelper.Creator creator) {}
  
  public synchronized Object getProxy()
  {
    return wrappedObject;
  }
  
  public synchronized String getId()
  {
    return id;
  }
  
  public synchronized String getPolyType()
  {
    return polyType;
  }
  
  public synchronized void setPolyType(String polyType)
  {
    this.polyType = polyType;
  }
  
  @Deprecated
  public synchronized void setAttributes(AttributeList attributes)
  {
    this.attributes = new AttributeListImpl(attributes);
    for (int i = 0; i < attributes.getLength(); i++) {
      setAttribute(attributes.getName(i), attributes.getValue(i));
    }
  }
  
  public synchronized void setAttribute(String name, String value)
  {
    if (name.contains(":")) {
      namespacedAttribute = true;
    }
    setAttribute(name, value);
  }
  
  public synchronized void setAttribute(String name, Object value)
  {
    if (name.equalsIgnoreCase("ant-type"))
    {
      polyType = (value == null ? null : value.toString());
    }
    else
    {
      if (attributeMap == null) {
        attributeMap = new LinkedHashMap();
      }
      if (("refid".equalsIgnoreCase(name)) && (!attributeMap.isEmpty()))
      {
        LinkedHashMap<String, Object> newAttributeMap = new LinkedHashMap();
        newAttributeMap.put(name, value);
        newAttributeMap.putAll(attributeMap);
        attributeMap = newAttributeMap;
      }
      else
      {
        attributeMap.put(name, value);
      }
      if ("id".equals(name)) {
        id = (value == null ? null : value.toString());
      }
    }
  }
  
  public synchronized void removeAttribute(String name)
  {
    attributeMap.remove(name);
  }
  
  public synchronized Hashtable<String, Object> getAttributeMap()
  {
    return new Hashtable(attributeMap == null ? Collections.emptyMap() : attributeMap);
  }
  
  @Deprecated
  public synchronized AttributeList getAttributes()
  {
    return attributes;
  }
  
  public synchronized void addChild(RuntimeConfigurable child)
  {
    children = (children == null ? new ArrayList() : children);
    children.add(child);
  }
  
  synchronized RuntimeConfigurable getChild(int index)
  {
    return (RuntimeConfigurable)children.get(index);
  }
  
  public synchronized Enumeration<RuntimeConfigurable> getChildren()
  {
    return children == null ? Collections.emptyEnumeration() : 
      Collections.enumeration(children);
  }
  
  public synchronized void addText(String data)
  {
    if (data.isEmpty()) {
      return;
    }
    characters = (characters == null ? new StringBuffer(data) : characters.append(data));
  }
  
  public synchronized void addText(char[] buf, int start, int count)
  {
    if (count == 0) {
      return;
    }
    characters = (characters == null ? new StringBuffer(count) : characters).append(buf, start, count);
  }
  
  public synchronized StringBuffer getText()
  {
    return characters == null ? new StringBuffer(0) : characters;
  }
  
  public synchronized void setElementTag(String elementTag)
  {
    this.elementTag = elementTag;
  }
  
  public synchronized String getElementTag()
  {
    return elementTag;
  }
  
  public void maybeConfigure(Project p)
    throws BuildException
  {
    maybeConfigure(p, true);
  }
  
  public synchronized void maybeConfigure(Project p, boolean configureChildren)
    throws BuildException
  {
    if (proxyConfigured) {
      return;
    }
    Object target = (wrappedObject instanceof TypeAdapter) ? ((TypeAdapter)wrappedObject).getProxy() : wrappedObject;
    
    IntrospectionHelper ih = IntrospectionHelper.getHelper(p, target.getClass());
    ComponentHelper componentHelper = ComponentHelper.getComponentHelper(p);
    if (attributeMap != null) {
      for (Map.Entry<String, Object> entry : attributeMap.entrySet())
      {
        String name = (String)entry.getKey();
        
        AttributeComponentInformation attributeComponentInformation = isRestrictedAttribute(name, componentHelper);
        if (!attributeComponentInformation.isRestricted())
        {
          Object value = entry.getValue();
          Object attrValue;
          Object attrValue;
          if ((value instanceof Evaluable)) {
            attrValue = ((Evaluable)value).eval();
          } else {
            attrValue = PropertyHelper.getPropertyHelper(p).parseProperties(value.toString());
          }
          if ((target instanceof MacroInstance)) {
            for (MacroDef.Attribute attr : ((MacroInstance)target).getMacroDef().getAttributes()) {
              if (attr.getName().equals(name))
              {
                if (attr.isDoubleExpanding()) {
                  break;
                }
                attrValue = value; break;
              }
            }
          }
          try
          {
            ih.setAttribute(p, target, name, attrValue);
          }
          catch (UnsupportedAttributeException be)
          {
            if (!"id".equals(name))
            {
              if (getElementTag() == null) {
                throw be;
              }
              throw new BuildException(getElementTag() + " doesn't support the \"" + be.getAttribute() + "\" attribute", be);
            }
          }
          catch (BuildException be)
          {
            if (!"id".equals(name)) {
              throw be;
            }
          }
        }
      }
    }
    if (characters != null) {
      ProjectHelper.addText(p, wrappedObject, characters.substring(0));
    }
    if (id != null) {
      p.addReference(id, wrappedObject);
    }
    proxyConfigured = true;
  }
  
  public void reconfigure(Project p)
  {
    proxyConfigured = false;
    maybeConfigure(p);
  }
  
  public void applyPreSet(RuntimeConfigurable r)
  {
    if (attributeMap != null) {
      for (String name : attributeMap.keySet()) {
        if ((attributeMap == null) || (attributeMap.get(name) == null)) {
          setAttribute(name, (String)attributeMap.get(name));
        }
      }
    }
    polyType = (polyType == null ? polyType : polyType);
    if (children != null)
    {
      Object newChildren = new ArrayList(children);
      if (children != null) {
        ((List)newChildren).addAll(children);
      }
      children = ((List)newChildren);
    }
    if ((characters != null) && (
      (characters == null) || 
      (characters.toString().trim().isEmpty()))) {
      characters = new StringBuffer(characters.toString());
    }
  }
  
  private static class EnableAttributeConsumer
  {
    public void add(EnableAttribute b) {}
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.RuntimeConfigurable
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */