package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.Move;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Mapper.MapperType;

@Deprecated
public class RenameExtensions
  extends MatchingTask
{
  private String fromExtension = "";
  private String toExtension = "";
  private boolean replace = false;
  private File srcDir;
  private Mapper.MapperType globType;
  
  public RenameExtensions()
  {
    globType = new Mapper.MapperType();
    globType.setValue("glob");
  }
  
  public void setFromExtension(String from)
  {
    fromExtension = from;
  }
  
  public void setToExtension(String to)
  {
    toExtension = to;
  }
  
  public void setReplace(boolean replace)
  {
    this.replace = replace;
  }
  
  public void setSrcDir(File srcDir)
  {
    this.srcDir = srcDir;
  }
  
  public void execute()
    throws BuildException
  {
    if ((fromExtension == null) || (toExtension == null) || (srcDir == null)) {
      throw new BuildException("srcDir, fromExtension and toExtension attributes must be set!");
    }
    log("DEPRECATED - The renameext task is deprecated.  Use move instead.", 1);
    
    log("Replace this with:", 2);
    log("<move todir=\"" + srcDir + "\" overwrite=\"" + replace + "\">", 2);
    
    log("  <fileset dir=\"" + srcDir + "\" />", 2);
    log("  <mapper type=\"glob\"", 2);
    log("          from=\"*" + fromExtension + "\"", 2);
    log("          to=\"*" + toExtension + "\" />", 2);
    log("</move>", 2);
    log("using the same patterns on <fileset> as you've used here", 2);
    
    Move move = new Move();
    move.bindToOwner(this);
    move.setOwningTarget(getOwningTarget());
    move.setTaskName(getTaskName());
    move.setLocation(getLocation());
    move.setTodir(srcDir);
    move.setOverwrite(replace);
    
    fileset.setDir(srcDir);
    move.addFileset(fileset);
    
    Mapper me = move.createMapper();
    me.setType(globType);
    me.setFrom("*" + fromExtension);
    me.setTo("*" + toExtension);
    
    move.execute();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.RenameExtensions
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */