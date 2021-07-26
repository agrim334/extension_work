package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public final class LineContainsRegExp
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final String REGEXP_KEY = "regexp";
  private static final String NEGATE_KEY = "negate";
  private static final String CS_KEY = "casesensitive";
  private Vector<RegularExpression> regexps = new Vector();
  private String line = null;
  private boolean negate = false;
  private int regexpOptions = 0;
  
  public LineContainsRegExp() {}
  
  public LineContainsRegExp(Reader in)
  {
    super(in);
  }
  
  public int read()
    throws IOException
  {
    if (!getInitialized())
    {
      initialize();
      setInitialized(true);
    }
    int ch = -1;
    if (line != null)
    {
      ch = line.charAt(0);
      if (line.length() == 1) {
        line = null;
      } else {
        line = line.substring(1);
      }
    }
    else
    {
      for (line = readLine(); line != null; line = readLine())
      {
        boolean matches = true;
        for (RegularExpression regexp : regexps) {
          if (!regexp.getRegexp(getProject()).matches(line, regexpOptions))
          {
            matches = false;
            break;
          }
        }
        if ((matches ^ isNegated())) {
          break;
        }
      }
      if (line != null) {
        return read();
      }
    }
    return ch;
  }
  
  public void addConfiguredRegexp(RegularExpression regExp)
  {
    regexps.addElement(regExp);
  }
  
  private void setRegexps(Vector<RegularExpression> regexps)
  {
    this.regexps = regexps;
  }
  
  private Vector<RegularExpression> getRegexps()
  {
    return regexps;
  }
  
  public Reader chain(Reader rdr)
  {
    LineContainsRegExp newFilter = new LineContainsRegExp(rdr);
    newFilter.setRegexps(getRegexps());
    newFilter.setNegate(isNegated());
    newFilter.setCaseSensitive(!RegexpUtil.hasFlag(regexpOptions, 256));
    
    return newFilter;
  }
  
  public void setNegate(boolean b)
  {
    negate = b;
  }
  
  public void setCaseSensitive(boolean b)
  {
    regexpOptions = RegexpUtil.asOptions(b);
  }
  
  public boolean isNegated()
  {
    return negate;
  }
  
  public void setRegexp(String pattern)
  {
    RegularExpression regexp = new RegularExpression();
    regexp.setPattern(pattern);
    regexps.addElement(regexp);
  }
  
  private void initialize()
  {
    Parameter[] params = getParameters();
    if (params != null) {
      for (Parameter param : params) {
        if ("regexp".equals(param.getType())) {
          setRegexp(param.getValue());
        } else if ("negate".equals(param.getType())) {
          setNegate(Project.toBoolean(param.getValue()));
        } else if ("casesensitive".equals(param.getType())) {
          setCaseSensitive(Project.toBoolean(param.getValue()));
        }
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.LineContainsRegExp
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */