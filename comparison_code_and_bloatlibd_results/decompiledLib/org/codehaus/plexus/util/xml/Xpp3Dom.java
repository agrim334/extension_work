package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class Xpp3Dom
  implements Serializable
{
  private static final long serialVersionUID = 2567894443061173996L;
  protected String name;
  protected String value;
  protected Map<String, String> attributes;
  protected final List<Xpp3Dom> childList;
  protected Xpp3Dom parent;
  protected Object inputLocation;
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static final Xpp3Dom[] EMPTY_DOM_ARRAY = new Xpp3Dom[0];
  public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
  public static final String CHILDREN_COMBINATION_MERGE = "merge";
  public static final String CHILDREN_COMBINATION_APPEND = "append";
  public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
  public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
  public static final String SELF_COMBINATION_OVERRIDE = "override";
  public static final String SELF_COMBINATION_MERGE = "merge";
  public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";
  
  public Xpp3Dom(String name)
  {
    this.name = name;
    childList = new ArrayList();
  }
  
  public Xpp3Dom(String name, Object inputLocation)
  {
    this(name);
    this.inputLocation = inputLocation;
  }
  
  public Xpp3Dom(Xpp3Dom src)
  {
    this(src, src.getName());
  }
  
  public Xpp3Dom(Xpp3Dom src, String name)
  {
    this.name = name;
    inputLocation = inputLocation;
    
    int childCount = src.getChildCount();
    
    childList = new ArrayList(childCount);
    
    setValue(src.getValue());
    
    String[] attributeNames = src.getAttributeNames();
    for (String attributeName : attributeNames) {
      setAttribute(attributeName, src.getAttribute(attributeName));
    }
    for (int i = 0; i < childCount; i++) {
      addChild(new Xpp3Dom(src.getChild(i)));
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public String[] getAttributeNames()
  {
    if ((null == attributes) || (attributes.isEmpty())) {
      return EMPTY_STRING_ARRAY;
    }
    return (String[])attributes.keySet().toArray(EMPTY_STRING_ARRAY);
  }
  
  public String getAttribute(String name)
  {
    return null != attributes ? (String)attributes.get(name) : null;
  }
  
  public void setAttribute(String name, String value)
  {
    if (null == value) {
      throw new NullPointerException("Attribute value can not be null");
    }
    if (null == name) {
      throw new NullPointerException("Attribute name can not be null");
    }
    if (null == attributes) {
      attributes = new HashMap();
    }
    attributes.put(name, value);
  }
  
  public Xpp3Dom getChild(int i)
  {
    return (Xpp3Dom)childList.get(i);
  }
  
  public Xpp3Dom getChild(String name)
  {
    if (name != null)
    {
      ListIterator<Xpp3Dom> it = childList.listIterator(childList.size());
      while (it.hasPrevious())
      {
        Xpp3Dom child = (Xpp3Dom)it.previous();
        if (name.equals(child.getName())) {
          return child;
        }
      }
    }
    return null;
  }
  
  public void addChild(Xpp3Dom xpp3Dom)
  {
    xpp3Dom.setParent(this);
    childList.add(xpp3Dom);
  }
  
  public Xpp3Dom[] getChildren()
  {
    if ((null == childList) || (childList.isEmpty())) {
      return EMPTY_DOM_ARRAY;
    }
    return (Xpp3Dom[])childList.toArray(EMPTY_DOM_ARRAY);
  }
  
  public Xpp3Dom[] getChildren(String name)
  {
    return (Xpp3Dom[])getChildrenAsList(name).toArray(EMPTY_DOM_ARRAY);
  }
  
  private List<Xpp3Dom> getChildrenAsList(String name)
  {
    if (null == childList) {
      return Collections.emptyList();
    }
    ArrayList<Xpp3Dom> children = null;
    for (Xpp3Dom configuration : childList) {
      if (name.equals(configuration.getName()))
      {
        if (children == null) {
          children = new ArrayList();
        }
        children.add(configuration);
      }
    }
    if (children != null) {
      return children;
    }
    return Collections.emptyList();
  }
  
  public int getChildCount()
  {
    if (null == childList) {
      return 0;
    }
    return childList.size();
  }
  
  public void removeChild(int i)
  {
    Xpp3Dom child = getChild(i);
    childList.remove(i);
    
    child.setParent(null);
  }
  
  public Xpp3Dom getParent()
  {
    return parent;
  }
  
  public void setParent(Xpp3Dom parent)
  {
    this.parent = parent;
  }
  
  public Object getInputLocation()
  {
    return inputLocation;
  }
  
  public void setInputLocation(Object inputLocation)
  {
    this.inputLocation = inputLocation;
  }
  
  public void writeToSerializer(String namespace, XmlSerializer serializer)
    throws IOException
  {
    SerializerXMLWriter xmlWriter = new SerializerXMLWriter(namespace, serializer);
    Xpp3DomWriter.write(xmlWriter, this);
    if (xmlWriter.getExceptions().size() > 0) {
      throw ((IOException)xmlWriter.getExceptions().get(0));
    }
  }
  
  private static void mergeIntoXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride)
  {
    if (recessive == null) {
      return;
    }
    boolean mergeSelf = true;
    
    String selfMergeMode = dominant.getAttribute("combine.self");
    if ("override".equals(selfMergeMode)) {
      mergeSelf = false;
    }
    if (mergeSelf)
    {
      if ((isEmpty(dominant.getValue())) && (!isEmpty(recessive.getValue())))
      {
        dominant.setValue(recessive.getValue());
        dominant.setInputLocation(recessive.getInputLocation());
      }
      if (attributes != null) {
        for (String attr : attributes.keySet()) {
          if (isEmpty(dominant.getAttribute(attr))) {
            dominant.setAttribute(attr, recessive.getAttribute(attr));
          }
        }
      }
      if (recessive.getChildCount() > 0)
      {
        boolean mergeChildren = true;
        if (childMergeOverride != null)
        {
          mergeChildren = childMergeOverride.booleanValue();
        }
        else
        {
          String childMergeMode = dominant.getAttribute("combine.children");
          if ("append".equals(childMergeMode)) {
            mergeChildren = false;
          }
        }
        if (!mergeChildren)
        {
          Xpp3Dom[] dominantChildren = dominant.getChildren();
          
          childList.clear();
          
          int i = 0;
          for (int recessiveChildCount = recessive.getChildCount(); i < recessiveChildCount; i++)
          {
            Xpp3Dom recessiveChild = recessive.getChild(i);
            dominant.addChild(new Xpp3Dom(recessiveChild));
          }
          for (Xpp3Dom aDominantChildren : dominantChildren) {
            dominant.addChild(aDominantChildren);
          }
        }
        else
        {
          Map<String, Iterator<Xpp3Dom>> commonChildren = new HashMap();
          for (Xpp3Dom recChild : childList) {
            if (!commonChildren.containsKey(name))
            {
              List<Xpp3Dom> dominantChildren = dominant.getChildrenAsList(name);
              if (dominantChildren.size() > 0) {
                commonChildren.put(name, dominantChildren.iterator());
              }
            }
          }
          int i = 0;
          for (int recessiveChildCount = recessive.getChildCount(); i < recessiveChildCount; i++)
          {
            Xpp3Dom recessiveChild = recessive.getChild(i);
            Iterator<Xpp3Dom> it = (Iterator)commonChildren.get(recessiveChild.getName());
            if (it == null)
            {
              dominant.addChild(new Xpp3Dom(recessiveChild));
            }
            else if (it.hasNext())
            {
              Xpp3Dom dominantChild = (Xpp3Dom)it.next();
              mergeIntoXpp3Dom(dominantChild, recessiveChild, childMergeOverride);
            }
          }
        }
      }
    }
  }
  
  public static Xpp3Dom mergeXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride)
  {
    if (dominant != null)
    {
      mergeIntoXpp3Dom(dominant, recessive, childMergeOverride);
      return dominant;
    }
    return recessive;
  }
  
  public static Xpp3Dom mergeXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive)
  {
    if (dominant != null)
    {
      mergeIntoXpp3Dom(dominant, recessive, null);
      return dominant;
    }
    return recessive;
  }
  
  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Xpp3Dom)) {
      return false;
    }
    Xpp3Dom dom = (Xpp3Dom)obj;
    if (name == null ? name != null : !name.equals(name)) {
      return false;
    }
    if (value == null ? value != null : !value.equals(value)) {
      return false;
    }
    if (attributes == null ? attributes != null : !attributes.equals(attributes)) {
      return false;
    }
    if (childList == null ? childList != null : !childList.equals(childList)) {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int result = 17;
    result = 37 * result + (name != null ? name.hashCode() : 0);
    result = 37 * result + (value != null ? value.hashCode() : 0);
    result = 37 * result + (attributes != null ? attributes.hashCode() : 0);
    result = 37 * result + (childList != null ? childList.hashCode() : 0);
    return result;
  }
  
  public String toString()
  {
    StringWriter writer = new StringWriter();
    XMLWriter xmlWriter = new PrettyPrintXMLWriter(writer, "UTF-8", null);
    Xpp3DomWriter.write(xmlWriter, this);
    return writer.toString();
  }
  
  public String toUnescapedString()
  {
    StringWriter writer = new StringWriter();
    XMLWriter xmlWriter = new PrettyPrintXMLWriter(writer, "UTF-8", null);
    Xpp3DomWriter.write(xmlWriter, this, false);
    return writer.toString();
  }
  
  public static boolean isNotEmpty(String str)
  {
    return (str != null) && (str.length() > 0);
  }
  
  public static boolean isEmpty(String str)
  {
    return (str == null) || (str.trim().length() == 0);
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.xml.Xpp3Dom
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */