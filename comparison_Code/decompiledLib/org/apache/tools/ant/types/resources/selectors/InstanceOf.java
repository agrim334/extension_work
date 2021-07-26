package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.types.Resource;

public class InstanceOf
  implements ResourceSelector
{
  private static final String ONE_ONLY = "Exactly one of class|type must be set.";
  private Project project;
  private Class<?> clazz;
  private String type;
  private String uri;
  
  public void setProject(Project p)
  {
    project = p;
  }
  
  public void setClass(Class<?> c)
  {
    if (clazz != null) {
      throw new BuildException("The class attribute has already been set.");
    }
    clazz = c;
  }
  
  public void setType(String s)
  {
    type = s;
  }
  
  public void setURI(String u)
  {
    uri = u;
  }
  
  public Class<?> getCheckClass()
  {
    return clazz;
  }
  
  public String getType()
  {
    return type;
  }
  
  public String getURI()
  {
    return uri;
  }
  
  public boolean isSelected(Resource r)
  {
    if ((clazz == null ? 1 : 0) == (type == null ? 1 : 0)) {
      throw new BuildException("Exactly one of class|type must be set.");
    }
    Class<?> c = clazz;
    if (type != null)
    {
      if (project == null) {
        throw new BuildException("No project set for InstanceOf ResourceSelector; the type attribute is invalid.");
      }
      AntTypeDefinition d = ComponentHelper.getComponentHelper(project).getDefinition(ProjectHelper.genComponentName(uri, type));
      if (d == null) {
        throw new BuildException("type %s not found.", new Object[] { type });
      }
      try
      {
        c = d.innerGetTypeClass();
      }
      catch (ClassNotFoundException e)
      {
        throw new BuildException(e);
      }
    }
    return c.isAssignableFrom(r.getClass());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.selectors.InstanceOf
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */