package org.apache.tools.ant.util.regexp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;

public class Jdk14RegexpRegexp
  extends Jdk14RegexpMatcher
  implements Regexp
{
  private static final int DECIMAL = 10;
  
  protected int getSubsOptions(int options)
  {
    int subsOptions = 1;
    if (RegexpUtil.hasFlag(options, 16)) {
      subsOptions = 16;
    }
    return subsOptions;
  }
  
  public String substitute(String input, String argument, int options)
    throws BuildException
  {
    StringBuilder subst = new StringBuilder();
    for (int i = 0; i < argument.length(); i++)
    {
      char c = argument.charAt(i);
      if (c == '$')
      {
        subst.append('\\');
        subst.append('$');
      }
      else if (c == '\\')
      {
        i++;
        if (i < argument.length())
        {
          c = argument.charAt(i);
          int value = Character.digit(c, 10);
          if (value > -1) {
            subst.append('$').append(value);
          } else {
            subst.append(c);
          }
        }
        else
        {
          subst.append('\\');
        }
      }
      else
      {
        subst.append(c);
      }
    }
    int sOptions = getSubsOptions(options);
    Pattern p = getCompiledPattern(options);
    StringBuffer sb = new StringBuffer();
    
    Matcher m = p.matcher(input);
    if (RegexpUtil.hasFlag(sOptions, 16))
    {
      sb.append(m.replaceAll(subst.toString()));
    }
    else if (m.find())
    {
      m.appendReplacement(sb, subst.toString());
      m.appendTail(sb);
    }
    else
    {
      sb.append(input);
    }
    return sb.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.regexp.Jdk14RegexpRegexp
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */