package org.apache.tools.ant.util;

import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.regexp.RegexpMatcher;
import org.apache.tools.ant.util.regexp.RegexpMatcherFactory;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class RegexpPatternMapper
  implements FileNameMapper
{
  private static final int DECIMAL = 10;
  protected RegexpMatcher reg = null;
  protected char[] to = null;
  protected StringBuffer result = new StringBuffer();
  
  public RegexpPatternMapper()
    throws BuildException
  {
    reg = new RegexpMatcherFactory().newRegexpMatcher();
  }
  
  private boolean handleDirSep = false;
  private int regexpOptions = 0;
  
  public void setHandleDirSep(boolean handleDirSep)
  {
    this.handleDirSep = handleDirSep;
  }
  
  public void setCaseSensitive(boolean caseSensitive)
  {
    regexpOptions = RegexpUtil.asOptions(caseSensitive);
  }
  
  public void setFrom(String from)
    throws BuildException
  {
    if (from == null) {
      throw new BuildException("this mapper requires a 'from' attribute");
    }
    try
    {
      reg.setPattern(from);
    }
    catch (NoClassDefFoundError e)
    {
      throw new BuildException("Cannot load regular expression matcher", e);
    }
  }
  
  public void setTo(String to)
  {
    if (to == null) {
      throw new BuildException("this mapper requires a 'to' attribute");
    }
    this.to = to.toCharArray();
  }
  
  public String[] mapFileName(String sourceFileName)
  {
    if (sourceFileName == null) {
      return null;
    }
    if ((handleDirSep) && 
      (sourceFileName.contains("\\"))) {
      sourceFileName = sourceFileName.replace('\\', '/');
    }
    if ((reg == null) || (to == null) || 
      (!reg.matches(sourceFileName, regexpOptions))) {
      return null;
    }
    return new String[] { replaceReferences(sourceFileName) };
  }
  
  protected String replaceReferences(String source)
  {
    List<String> v = reg.getGroups(source, regexpOptions);
    
    result.setLength(0);
    for (int i = 0; i < to.length; i++) {
      if (to[i] == '\\')
      {
        i++;
        if (i < to.length)
        {
          int value = Character.digit(to[i], 10);
          if (value > -1) {
            result.append((String)v.get(value));
          } else {
            result.append(to[i]);
          }
        }
        else
        {
          result.append('\\');
        }
      }
      else
      {
        result.append(to[i]);
      }
    }
    return result.substring(0);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.RegexpPatternMapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */