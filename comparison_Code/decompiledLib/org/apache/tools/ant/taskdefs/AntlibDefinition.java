package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AntlibDefinition
  extends Task
{
  private String uri = "";
  private ClassLoader antlibClassLoader;
  
  public void setURI(String uri)
    throws BuildException
  {
    if ("antlib:org.apache.tools.ant".equals(uri)) {
      uri = "";
    }
    if (uri.startsWith("ant:")) {
      throw new BuildException("Attempt to use a reserved URI %s", new Object[] { uri });
    }
    this.uri = uri;
  }
  
  public String getURI()
  {
    return uri;
  }
  
  public void setAntlibClassLoader(ClassLoader classLoader)
  {
    antlibClassLoader = classLoader;
  }
  
  public ClassLoader getAntlibClassLoader()
  {
    return antlibClassLoader;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.AntlibDefinition
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */