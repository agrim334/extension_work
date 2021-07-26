package org.apache.tools.ant.taskdefs.modules;

import java.io.File;
import org.apache.tools.ant.BuildException;

public class Link$PatternListEntry
{
  private String pattern;
  private File file;
  
  public Link$PatternListEntry(Link this$0) {}
  
  public Link$PatternListEntry(Link this$0, String pattern)
  {
    if (pattern.startsWith("@")) {
      setListFile(new File(pattern.substring(1)));
    } else {
      setPattern(pattern);
    }
  }
  
  public String getPattern()
  {
    return pattern;
  }
  
  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }
  
  public File getListFile()
  {
    return file;
  }
  
  public void setListFile(File file)
  {
    this.file = file;
  }
  
  public void validate()
  {
    if (((pattern == null) && (file == null)) || ((pattern != null) && (file != null))) {
      throw new BuildException("Each entry in a pattern list must specify exactly one of pattern or file.", this$0.getLocation());
    }
  }
  
  public String toOptionValue()
  {
    return "@" + file;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.modules.Link.PatternListEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */