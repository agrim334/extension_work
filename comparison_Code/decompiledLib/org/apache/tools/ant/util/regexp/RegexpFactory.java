package org.apache.tools.ant.util.regexp;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.ClasspathUtils;

public class RegexpFactory
  extends RegexpMatcherFactory
{
  public Regexp newRegexp()
    throws BuildException
  {
    return newRegexp(null);
  }
  
  public Regexp newRegexp(Project p)
    throws BuildException
  {
    String systemDefault;
    String systemDefault;
    if (p == null) {
      systemDefault = System.getProperty("ant.regexp.regexpimpl");
    } else {
      systemDefault = p.getProperty("ant.regexp.regexpimpl");
    }
    if (systemDefault != null) {
      return createRegexpInstance(systemDefault);
    }
    return new Jdk14RegexpRegexp();
  }
  
  protected Regexp createRegexpInstance(String classname)
    throws BuildException
  {
    return (Regexp)ClasspathUtils.newInstance(classname, RegexpFactory.class
      .getClassLoader(), Regexp.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.regexp.RegexpFactory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */