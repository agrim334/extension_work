package org.apache.tools.ant.taskdefs.optional.windows;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.FileSet;

public class Attrib
  extends ExecuteOn
{
  private static final String ATTR_READONLY = "R";
  private static final String ATTR_ARCHIVE = "A";
  private static final String ATTR_SYSTEM = "S";
  private static final String ATTR_HIDDEN = "H";
  private static final String SET = "+";
  private static final String UNSET = "-";
  private boolean haveAttr = false;
  
  public Attrib()
  {
    super.setExecutable("attrib");
    super.setParallel(false);
  }
  
  public void setFile(File src)
  {
    FileSet fs = new FileSet();
    fs.setFile(src);
    addFileset(fs);
  }
  
  public void setReadonly(boolean value)
  {
    addArg(value, "R");
  }
  
  public void setArchive(boolean value)
  {
    addArg(value, "A");
  }
  
  public void setSystem(boolean value)
  {
    addArg(value, "S");
  }
  
  public void setHidden(boolean value)
  {
    addArg(value, "H");
  }
  
  protected void checkConfiguration()
  {
    if (!haveAttr()) {
      throw new BuildException("Missing attribute parameter", getLocation());
    }
    super.checkConfiguration();
  }
  
  public void setExecutable(String e)
  {
    throw new BuildException(getTaskType() + " doesn't support the executable attribute", getLocation());
  }
  
  public void setCommand(String e)
  {
    throw new BuildException(getTaskType() + " doesn't support the command attribute", getLocation());
  }
  
  public void setAddsourcefile(boolean b)
  {
    throw new BuildException(getTaskType() + " doesn't support the addsourcefile attribute", getLocation());
  }
  
  public void setSkipEmptyFilesets(boolean skip)
  {
    throw new BuildException(getTaskType() + " doesn't support the skipemptyfileset attribute", getLocation());
  }
  
  public void setParallel(boolean parallel)
  {
    throw new BuildException(getTaskType() + " doesn't support the parallel attribute", getLocation());
  }
  
  public void setMaxParallel(int max)
  {
    throw new BuildException(getTaskType() + " doesn't support the maxparallel attribute", getLocation());
  }
  
  protected boolean isValidOs()
  {
    return (getOs() == null) && (getOsFamily() == null) ? 
      Os.isFamily("windows") : super.isValidOs();
  }
  
  private static String getSignString(boolean attr)
  {
    return attr ? "+" : "-";
  }
  
  private void addArg(boolean sign, String attribute)
  {
    createArg().setValue(getSignString(sign) + attribute);
    haveAttr = true;
  }
  
  private boolean haveAttr()
  {
    return haveAttr;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.windows.Attrib
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */