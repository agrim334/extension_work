package org.apache.tools.ant.util.regexp;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.ClasspathUtils;

public class RegexpMatcherFactory
{
  public RegexpMatcher newRegexpMatcher()
    throws BuildException
  {
    return newRegexpMatcher(null);
  }
  
  public RegexpMatcher newRegexpMatcher(Project p)
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
      return createInstance(systemDefault);
    }
    return new Jdk14RegexpMatcher();
  }
  
  protected RegexpMatcher createInstance(String className)
    throws BuildException
  {
    return (RegexpMatcher)ClasspathUtils.newInstance(className, RegexpMatcherFactory.class
      .getClassLoader(), RegexpMatcher.class);
  }
  
  protected void testAvailability(String className)
    throws BuildException
  {
    try
    {
      Class.forName(className);
    }
    catch (Throwable t)
    {
      throw new BuildException(t);
    }
  }
  
  public static boolean regexpMatcherPresent(Project project)
  {
    try
    {
      new RegexpMatcherFactory().newRegexpMatcher(project);
      return true;
    }
    catch (Throwable ex) {}
    return false;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.regexp.RegexpMatcherFactory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */