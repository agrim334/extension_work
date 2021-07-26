package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

public class LocationResolver
  implements ExtensionResolver
{
  private String location;
  
  public void setLocation(String location)
  {
    this.location = location;
  }
  
  public File resolve(Extension extension, Project project)
    throws BuildException
  {
    if (null == location) {
      throw new BuildException("No location specified for resolver");
    }
    return project.resolveFile(location);
  }
  
  public String toString()
  {
    return "Location[" + location + "]";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.extension.resolvers.LocationResolver
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */