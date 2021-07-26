package org.apache.tools.ant.types;

import java.io.File;
import org.apache.tools.ant.ProjectComponent;

public class Commandline$Argument
  extends ProjectComponent
{
  private String[] parts;
  private String prefix = "";
  private String suffix = "";
  
  public void setValue(String value)
  {
    parts = new String[] { value };
  }
  
  public void setLine(String line)
  {
    if (line == null) {
      return;
    }
    parts = Commandline.translateCommandline(line);
  }
  
  public void setPath(Path value)
  {
    parts = new String[] { value.toString() };
  }
  
  public void setPathref(Reference value)
  {
    Path p = new Path(getProject());
    p.setRefid(value);
    parts = new String[] { p.toString() };
  }
  
  public void setFile(File value)
  {
    parts = new String[] { value.getAbsolutePath() };
  }
  
  public void setPrefix(String prefix)
  {
    this.prefix = (prefix != null ? prefix : "");
  }
  
  public void setSuffix(String suffix)
  {
    this.suffix = (suffix != null ? suffix : "");
  }
  
  public void copyFrom(Argument other)
  {
    parts = parts;
    prefix = prefix;
    suffix = suffix;
  }
  
  public String[] getParts()
  {
    if ((parts == null) || (parts.length == 0) || ((prefix.isEmpty()) && (suffix.isEmpty()))) {
      return parts;
    }
    String[] fullParts = new String[parts.length];
    for (int i = 0; i < fullParts.length; i++) {
      fullParts[i] = (prefix + parts[i] + suffix);
    }
    return fullParts;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Commandline.Argument
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */