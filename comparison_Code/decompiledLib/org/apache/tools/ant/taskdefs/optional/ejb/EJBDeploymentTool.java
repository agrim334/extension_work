package org.apache.tools.ant.taskdefs.optional.ejb;

import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public abstract interface EJBDeploymentTool
{
  public abstract void processDescriptor(String paramString, SAXParser paramSAXParser)
    throws BuildException;
  
  public abstract void validateConfigured()
    throws BuildException;
  
  public abstract void setTask(Task paramTask);
  
  public abstract void configure(EjbJar.Config paramConfig);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.EJBDeploymentTool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */