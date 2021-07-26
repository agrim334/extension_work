package org.apache.tools.ant.taskdefs.optional.ccm;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.FileSet;

public class CCMCheck
  extends Continuus
{
  public static final String FLAG_COMMENT = "/comment";
  public static final String FLAG_TASK = "/task";
  private File file = null;
  private String comment = null;
  private String task = null;
  protected Vector<FileSet> filesets = new Vector();
  
  public File getFile()
  {
    return file;
  }
  
  public void setFile(File v)
  {
    log("working file " + v, 3);
    file = v;
  }
  
  public String getComment()
  {
    return comment;
  }
  
  public void setComment(String v)
  {
    comment = v;
  }
  
  public String getTask()
  {
    return task;
  }
  
  public void setTask(String v)
  {
    task = v;
  }
  
  public void addFileset(FileSet set)
  {
    filesets.addElement(set);
  }
  
  public void execute()
    throws BuildException
  {
    if ((file == null) && (filesets.isEmpty())) {
      throw new BuildException("Specify at least one source - a file or a fileset.");
    }
    if ((file != null) && (file.exists()) && (file.isDirectory())) {
      throw new BuildException("CCMCheck cannot be generated for directories");
    }
    if ((file != null) && (!filesets.isEmpty())) {
      throw new BuildException("Choose between file and fileset !");
    }
    if (getFile() != null)
    {
      doit();
      return;
    }
    for (FileSet fs : filesets)
    {
      File basedir = fs.getDir(getProject());
      DirectoryScanner ds = fs.getDirectoryScanner(getProject());
      for (String srcFile : ds.getIncludedFiles())
      {
        setFile(new File(basedir, srcFile));
        doit();
      }
    }
  }
  
  private void doit()
  {
    Commandline commandLine = new Commandline();
    
    commandLine.setExecutable(getCcmCommand());
    commandLine.createArgument().setValue(getCcmAction());
    
    checkOptions(commandLine);
    
    int result = run(commandLine);
    if (Execute.isFailure(result)) {
      throw new BuildException("Failed executing: " + commandLine, getLocation());
    }
  }
  
  private void checkOptions(Commandline cmd)
  {
    if (getComment() != null)
    {
      cmd.createArgument().setValue("/comment");
      cmd.createArgument().setValue(getComment());
    }
    if (getTask() != null)
    {
      cmd.createArgument().setValue("/task");
      cmd.createArgument().setValue(getTask());
    }
    if (getFile() != null) {
      cmd.createArgument().setValue(file.getAbsolutePath());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ccm.CCMCheck
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */