package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ClasspathUtils;

public class XSLTProcess$Factory$Attribute
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

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.XSLTProcess.Factory.Attribute
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */