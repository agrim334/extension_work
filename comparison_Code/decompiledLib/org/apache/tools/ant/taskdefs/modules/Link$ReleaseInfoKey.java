package org.apache.tools.ant.taskdefs.modules;

import org.apache.tools.ant.BuildException;

public class Link$ReleaseInfoKey
{
  private String key;
  
  public Link$ReleaseInfoKey(Link this$0) {}
  
  public Link$ReleaseInfoKey(Link this$0, String key)
  {
    setKey(key);
  }
  
  public String getKey()
  {
    return key;
  }
  
  public void setKey(String key)
  {
    this.key = key;
  }
  
  public void validate()
  {
    if (key == null) {
      throw new BuildException("Release info key must define a 'key' attribute.", this$0.getLocation());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.modules.Link.ReleaseInfoKey
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */