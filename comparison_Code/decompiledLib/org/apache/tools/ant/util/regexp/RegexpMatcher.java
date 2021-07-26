package org.apache.tools.ant.util.regexp;

import java.util.Vector;
import org.apache.tools.ant.BuildException;

public abstract interface RegexpMatcher
{
  public static final int MATCH_DEFAULT = 0;
  public static final int MATCH_CASE_INSENSITIVE = 256;
  public static final int MATCH_MULTILINE = 4096;
  public static final int MATCH_SINGLELINE = 65536;
  
  public abstract void setPattern(String paramString)
    throws BuildException;
  
  public abstract String getPattern()
    throws BuildException;
  
  public abstract boolean matches(String paramString)
    throws BuildException;
  
  public abstract Vector<String> getGroups(String paramString)
    throws BuildException;
  
  public abstract boolean matches(String paramString, int paramInt)
    throws BuildException;
  
  public abstract Vector<String> getGroups(String paramString, int paramInt)
    throws BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.regexp.RegexpMatcher
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */