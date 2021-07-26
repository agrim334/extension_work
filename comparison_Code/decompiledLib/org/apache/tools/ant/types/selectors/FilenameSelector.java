package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class FilenameSelector
  extends BaseExtendSelector
{
  public static final String NAME_KEY = "name";
  public static final String CASE_KEY = "casesensitive";
  public static final String NEGATE_KEY = "negate";
  public static final String REGEX_KEY = "regex";
  private String pattern = null;
  private String regex = null;
  private boolean casesensitive = true;
  private boolean negated = false;
  private RegularExpression reg;
  private Regexp expression;
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder("{filenameselector name: ");
    if (pattern != null) {
      buf.append(pattern);
    }
    if (regex != null) {
      buf.append(regex).append(" [as regular expression]");
    }
    buf.append(" negate: ").append(negated);
    buf.append(" casesensitive: ").append(casesensitive);
    buf.append("}");
    return buf.toString();
  }
  
  public void setName(String pattern)
  {
    pattern = pattern.replace('/', File.separatorChar).replace('\\', File.separatorChar);
    if (pattern.endsWith(File.separator)) {
      pattern = pattern + "**";
    }
    this.pattern = pattern;
  }
  
  public void setRegex(String pattern)
  {
    regex = pattern;
    reg = null;
  }
  
  public void setCasesensitive(boolean casesensitive)
  {
    this.casesensitive = casesensitive;
  }
  
  public void setNegate(boolean negated)
  {
    this.negated = negated;
  }
  
  public void setParameters(Parameter... parameters)
  {
    super.setParameters(parameters);
    if (parameters != null) {
      for (Parameter parameter : parameters)
      {
        String paramname = parameter.getName();
        if ("name".equalsIgnoreCase(paramname)) {
          setName(parameter.getValue());
        } else if ("casesensitive".equalsIgnoreCase(paramname)) {
          setCasesensitive(Project.toBoolean(parameter
            .getValue()));
        } else if ("negate".equalsIgnoreCase(paramname)) {
          setNegate(Project.toBoolean(parameter.getValue()));
        } else if ("regex".equalsIgnoreCase(paramname)) {
          setRegex(parameter.getValue());
        } else {
          setError("Invalid parameter " + paramname);
        }
      }
    }
  }
  
  public void verifySettings()
  {
    if ((pattern == null) && (regex == null)) {
      setError("The name or regex attribute is required");
    } else if ((pattern != null) && (regex != null)) {
      setError("Only one of name and regex attribute is allowed");
    }
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    validate();
    if (pattern != null) {
      return SelectorUtils.matchPath(pattern, filename, casesensitive) == (!negated);
    }
    if (reg == null)
    {
      reg = new RegularExpression();
      reg.setPattern(regex);
      expression = reg.getRegexp(getProject());
    }
    int options = RegexpUtil.asOptions(casesensitive);
    return expression.matches(filename, options) == (!negated);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.FilenameSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */