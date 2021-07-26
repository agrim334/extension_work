package org.apache.tools.ant;

public class ExtensionPoint
  extends Target
{
  private static final String NO_CHILDREN_ALLOWED = "you must not nest child elements into an extension-point";
  
  public ExtensionPoint() {}
  
  public ExtensionPoint(Target other)
  {
    super(other);
  }
  
  public final void addTask(Task task)
  {
    throw new BuildException("you must not nest child elements into an extension-point");
  }
  
  public final void addDataType(RuntimeConfigurable r)
  {
    throw new BuildException("you must not nest child elements into an extension-point");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.ExtensionPoint
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */