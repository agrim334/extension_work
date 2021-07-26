package org.apache.tools.ant.filters;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Substitution;
import org.apache.tools.ant.util.regexp.Regexp;

public class TokenFilter$ContainsRegex
  extends TokenFilter.ChainableReaderFilter
{
  private String from;
  private String to;
  private RegularExpression regularExpression;
  private Substitution substitution;
  private boolean initialized = false;
  private String flags = "";
  private int options;
  private Regexp regexp;
  
  public void setPattern(String from)
  {
    this.from = from;
  }
  
  public void setReplace(String to)
  {
    this.to = to;
  }
  
  public void setFlags(String flags)
  {
    this.flags = flags;
  }
  
  private void initialize()
  {
    if (initialized) {
      return;
    }
    options = TokenFilter.convertRegexOptions(flags);
    if (from == null) {
      throw new BuildException("Missing from in containsregex");
    }
    regularExpression = new RegularExpression();
    regularExpression.setPattern(from);
    regexp = regularExpression.getRegexp(getProject());
    if (to == null) {
      return;
    }
    substitution = new Substitution();
    substitution.setExpression(to);
  }
  
  public String filter(String string)
  {
    initialize();
    if (!regexp.matches(string, options)) {
      return null;
    }
    if (substitution == null) {
      return string;
    }
    return regexp.substitute(string, substitution
      .getExpression(getProject()), options);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TokenFilter.ContainsRegex
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */