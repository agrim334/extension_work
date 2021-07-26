package org.apache.tools.ant.util.regexp;

import org.apache.tools.ant.BuildException;

public abstract interface Regexp
  extends RegexpMatcher
{
  public static final int REPLACE_FIRST = 1;
  public static final int REPLACE_ALL = 16;
  
  public abstract String substitute(String paramString1, String paramString2, int paramInt)
    throws BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.regexp.Regexp
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */