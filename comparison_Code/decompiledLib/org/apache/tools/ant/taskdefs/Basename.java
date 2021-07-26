package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class Basename
  extends Task
{
  private File file;
  private String property;
  private String suffix;
  
  public void setFile(File file)
  {
    this.file = file;
  }
  
  public void setProperty(String property)
  {
    this.property = property;
  }
  
  public void setSuffix(String suffix)
  {
    this.suffix = suffix;
  }
  
  public void execute()
    throws BuildException
  {
    if (property == null) {
      throw new BuildException("property attribute required", getLocation());
    }
    if (file == null) {
      throw new BuildException("file attribute required", getLocation());
    }
    getProject().setNewProperty(property, 
      removeExtension(file.getName(), suffix));
  }
  
  private String removeExtension(String s, String ext)
  {
    if ((ext == null) || (!s.endsWith(ext))) {
      return s;
    }
    int clipFrom = s.length() - ext.length();
    if ((ext.charAt(0) != '.') && (clipFrom > 0) && (s.charAt(clipFrom - 1) == '.')) {
      clipFrom--;
    }
    return s.substring(0, clipFrom);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Basename
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */