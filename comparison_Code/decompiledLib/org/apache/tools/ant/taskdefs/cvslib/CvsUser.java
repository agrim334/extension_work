package org.apache.tools.ant.taskdefs.cvslib;

import org.apache.tools.ant.BuildException;

public class CvsUser
{
  private String userID;
  private String displayName;
  
  public void setDisplayname(String displayName)
  {
    this.displayName = displayName;
  }
  
  public void setUserid(String userID)
  {
    this.userID = userID;
  }
  
  public String getUserID()
  {
    return userID;
  }
  
  public String getDisplayname()
  {
    return displayName;
  }
  
  public void validate()
    throws BuildException
  {
    if (null == userID) {
      throw new BuildException("Username attribute must be set.");
    }
    if (null == displayName) {
      throw new BuildException("Displayname attribute must be set for userID %s", new Object[] { userID });
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.cvslib.CvsUser
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */