package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

public class XSLTProcess$Factory
{
  private String name;
  private final List<Attribute> attributes = new ArrayList();
  private final List<Feature> features = new ArrayList();
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void addAttribute(Attribute attr)
  {
    attributes.add(attr);
  }
  
  public Enumeration<Attribute> getAttributes()
  {
    return Collections.enumeration(attributes);
  }
  
  public void addFeature(Feature feature)
  {
    features.add(feature);
  }
  
  public Iterable<Feature> getFeatures()
  {
    return features;
  }
  
  public static class Attribute
    extends ProjectComponent
    implements DynamicConfigurator
  {
    private String name;
    private Object value;
    
    public String getName()
    {
      return name;
    }
    
    public Object getValue()
    {
      return value;
    }
    
    public Object createDynamicElement(String name)
      throws BuildException
    {
      return null;
    }
    
    public void setDynamicAttribute(String name, String value)
      throws BuildException
    {
      if ("name".equalsIgnoreCase(name)) {
        this.name = value;
      } else if ("value".equalsIgnoreCase(name))
      {
        if ("true".equalsIgnoreCase(value)) {
          this.value = Boolean.TRUE;
        } else if ("false".equalsIgnoreCase(value)) {
          this.value = Boolean.FALSE;
        } else {
          try
          {
            this.value = Integer.valueOf(value);
          }
          catch (NumberFormatException e)
          {
            this.value = value;
          }
        }
      }
      else if ("valueref".equalsIgnoreCase(name)) {
        this.value = getProject().getReference(value);
      } else if ("classloaderforpath".equalsIgnoreCase(name)) {
        this.value = ClasspathUtils.getClassLoaderForPath(getProject(), new Reference(
          getProject(), value));
      } else {
        throw new BuildException("Unsupported attribute: %s", new Object[] { name });
      }
    }
  }
  
  public static class Feature
  {
    private String name;
    private boolean value;
    
    public Feature() {}
    
    public Feature(String name, boolean value)
    {
      this.name = name;
      this.value = value;
    }
    
    public void setName(String name)
    {
      this.name = name;
    }
    
    public void setValue(boolean value)
    {
      this.value = value;
    }
    
    public String getName()
    {
      return name;
    }
    
    public boolean getValue()
    {
      return value;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.XSLTProcess.Factory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */