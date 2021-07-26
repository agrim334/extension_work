package org.apache.tools.ant.taskdefs.optional.vss;

import java.io.File;
import java.text.SimpleDateFormat;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class MSVSSHISTORY
  extends MSVSS
{
  Commandline buildCmdLine()
  {
    Commandline commandLine = new Commandline();
    if (getVsspath() == null)
    {
      String msg = "vsspath attribute must be set!";
      throw new BuildException(msg, getLocation());
    }
    commandLine.setExecutable(getSSCommand());
    commandLine.createArgument().setValue("History");
    
    commandLine.createArgument().setValue(getVsspath());
    
    commandLine.createArgument().setValue("-I-");
    
    commandLine.createArgument().setValue(getVersionDate());
    
    commandLine.createArgument().setValue(getVersionLabel());
    
    commandLine.createArgument().setValue(getRecursive());
    
    commandLine.createArgument().setValue(getStyle());
    
    commandLine.createArgument().setValue(getLogin());
    
    commandLine.createArgument().setValue(getOutput());
    
    return commandLine;
  }
  
  public void setRecursive(boolean recursive)
  {
    super.setInternalRecursive(recursive);
  }
  
  public void setUser(String user)
  {
    super.setInternalUser(user);
  }
  
  public void setFromDate(String fromDate)
  {
    super.setInternalFromDate(fromDate);
  }
  
  public void setToDate(String toDate)
  {
    super.setInternalToDate(toDate);
  }
  
  public void setFromLabel(String fromLabel)
  {
    super.setInternalFromLabel(fromLabel);
  }
  
  public void setToLabel(String toLabel)
  {
    super.setInternalToLabel(toLabel);
  }
  
  public void setNumdays(int numd)
  {
    super.setInternalNumDays(numd);
  }
  
  public void setOutput(File outfile)
  {
    if (outfile != null) {
      super.setInternalOutputFilename(outfile.getAbsolutePath());
    }
  }
  
  public void setDateFormat(String dateFormat)
  {
    super.setInternalDateFormat(new SimpleDateFormat(dateFormat));
  }
  
  public void setStyle(BriefCodediffNofile attr)
  {
    String option = attr.getValue();
    switch (option)
    {
    case "brief": 
      super.setInternalStyle("-B");
      break;
    case "codediff": 
      super.setInternalStyle("-D");
      break;
    case "default": 
      super.setInternalStyle("");
      break;
    case "nofile": 
      super.setInternalStyle("-F-");
      break;
    default: 
      throw new BuildException("Style " + attr + " unknown.", getLocation());
    }
  }
  
  public static class BriefCodediffNofile
    extends EnumeratedAttribute
  {
    public String[] getValues()
    {
      return new String[] { "brief", "codediff", "nofile", "default" };
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.vss.MSVSSHISTORY
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */