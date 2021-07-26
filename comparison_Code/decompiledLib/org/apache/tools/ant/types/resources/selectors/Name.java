package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class Name
  implements ResourceSelector
{
  private String regex = null;
  private String pattern;
  private boolean cs = true;
  private boolean handleDirSep = false;
  private RegularExpression reg;
  private Regexp expression;
  private Project project;
  
  public void setProject(Project p)
  {
    project = p;
  }
  
  public void setName(String n)
  {
    pattern = n;
  }
  
  public String getName()
  {
    return pattern;
  }
  
  public void setRegex(String r)
  {
    regex = r;
    reg = null;
  }
  
  public String getRegex()
  {
    return regex;
  }
  
  public void setCaseSensitive(boolean b)
  {
    cs = b;
  }
  
  public boolean isCaseSensitive()
  {
    return cs;
  }
  
  public void setHandleDirSep(boolean handleDirSep)
  {
    this.handleDirSep = handleDirSep;
  }
  
  public boolean doesHandledirSep()
  {
    return handleDirSep;
  }
  
  public boolean isSelected(Resource r)
  {
    String n = r.getName();
    if (matches(n)) {
      return true;
    }
    String s = r.toString();
    return (!s.equals(n)) && (matches(s));
  }
  
  private boolean matches(String name)
  {
    if (pattern != null) {
      return SelectorUtils.match(modify(pattern), modify(name), cs);
    }
    if (reg == null)
    {
      reg = new RegularExpression();
      reg.setPattern(regex);
      expression = reg.getRegexp(project);
    }
    return expression.matches(modify(name), RegexpUtil.asOptions(cs));
  }
  
  private String modify(String s)
  {
    if ((s == null) || (!handleDirSep) || (!s.contains("\\"))) {
      return s;
    }
    return s.replace('\\', '/');
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.selectors.Name
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */