package org.apache.tools.ant.util.regexp;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.tools.ant.BuildException;

public class Jdk14RegexpMatcher
  implements RegexpMatcher
{
  private String pattern;
  
  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }
  
  public String getPattern()
  {
    return pattern;
  }
  
  protected Pattern getCompiledPattern(int options)
    throws BuildException
  {
    try
    {
      return Pattern.compile(pattern, getCompilerOptions(options));
    }
    catch (PatternSyntaxException e)
    {
      throw new BuildException(e);
    }
  }
  
  public boolean matches(String argument)
    throws BuildException
  {
    return matches(argument, 0);
  }
  
  public boolean matches(String input, int options)
    throws BuildException
  {
    try
    {
      return getCompiledPattern(options).matcher(input).find();
    }
    catch (Exception e)
    {
      throw new BuildException(e);
    }
  }
  
  public Vector<String> getGroups(String argument)
    throws BuildException
  {
    return getGroups(argument, 0);
  }
  
  public Vector<String> getGroups(String input, int options)
    throws BuildException
  {
    Pattern p = getCompiledPattern(options);
    Matcher matcher = p.matcher(input);
    if (!matcher.find()) {
      return null;
    }
    Vector<String> v = new Vector();
    int cnt = matcher.groupCount();
    for (int i = 0; i <= cnt; i++)
    {
      String match = matcher.group(i);
      if (match == null) {
        match = "";
      }
      v.add(match);
    }
    return v;
  }
  
  protected int getCompilerOptions(int options)
  {
    int cOptions = 1;
    if (RegexpUtil.hasFlag(options, 256)) {
      cOptions |= 0x2;
    }
    if (RegexpUtil.hasFlag(options, 4096)) {
      cOptions |= 0x8;
    }
    if (RegexpUtil.hasFlag(options, 65536)) {
      cOptions |= 0x20;
    }
    return cOptions;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.regexp.Jdk14RegexpMatcher
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */