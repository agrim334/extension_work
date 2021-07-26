package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

public class AntResolver
  implements ExtensionResolver
{
  private File antfile;
  private File destfile;
  private String target;
  
  public void setAntfile(File antfile)
  {
    this.antfile = antfile;
  }
  
  public void setDestfile(File destfile)
  {
    this.destfile = destfile;
  }
  
  public void setTarget(String target)
  {
    this.target = target;
  }
  
  public File resolve(Extension extension, Project project)
    throws BuildException
  {
    validate();
    
    Ant ant = new Ant();
    ant.setProject(project);
    ant.setInheritAll(false);
    ant.setAntfile(antfile.getName());
    try
    {
      File dir = antfile.getParentFile().getCanonicalFile();
      ant.setDir(dir);
    }
    catch (IOException ioe)
    {
      throw new BuildException(ioe.getMessage(), ioe);
    }
    if (null != target) {
      ant.setTarget(target);
    }
    ant.execute();
    
    return destfile;
  }
  
  private void validate()
  {
    if (null == antfile)
    {
      String message = "Must specify Buildfile";
      throw new BuildException("Must specify Buildfile");
    }
    if (null == destfile)
    {
      String message = "Must specify destination file";
      throw new BuildException("Must specify destination file");
    }
  }
  
  public String toString()
  {
    return "Ant[" + antfile + "==>" + destfile + "]";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.extension.resolvers.AntResolver
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */