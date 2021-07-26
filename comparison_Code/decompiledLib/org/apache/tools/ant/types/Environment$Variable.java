package org.apache.tools.ant.types;

import java.io.File;
import org.apache.tools.ant.BuildException;

public class Environment$Variable
{
  private String key;
  private String value;
  
  public void setKey(String key)
  {
    this.key = key;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public String getKey()
  {
    return key;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public void setPath(Path path)
  {
    value = path.toString();
  }
  
  public void setFile(File file)
  {
    value = file.getAbsolutePath();
  }
  
  public String getContent()
    throws BuildException
  {
    validate();
    return key.trim() + "=" + value.trim();
  }
  
  public void validate()
  {
    if ((key == null) || (value == null)) {
      throw new BuildException("key and value must be specified for environment variables.");
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Environment.Variable
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */