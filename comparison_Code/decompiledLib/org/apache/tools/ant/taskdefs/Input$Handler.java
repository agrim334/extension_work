package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.util.ClasspathUtils;

public class Input$Handler
  extends DefBase
{
  private String refid = null;
  private Input.HandlerType type = null;
  private String classname = null;
  
  public Input$Handler(Input this$0) {}
  
  public void setRefid(String refid)
  {
    this.refid = refid;
  }
  
  public String getRefid()
  {
    return refid;
  }
  
  public void setClassname(String classname)
  {
    this.classname = classname;
  }
  
  public String getClassname()
  {
    return classname;
  }
  
  public void setType(Input.HandlerType type)
  {
    this.type = type;
  }
  
  public Input.HandlerType getType()
  {
    return type;
  }
  
  private InputHandler getInputHandler()
  {
    if (type != null) {
      return Input.HandlerType.access$000(type);
    }
    if (refid != null) {
      try
      {
        return (InputHandler)getProject().getReference(refid);
      }
      catch (ClassCastException e)
      {
        throw new BuildException(refid + " does not denote an InputHandler", e);
      }
    }
    if (classname != null) {
      return (InputHandler)ClasspathUtils.newInstance(classname, 
        createLoader(), InputHandler.class);
    }
    throw new BuildException("Must specify refid, classname or type");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Input.Handler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */