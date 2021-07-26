package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

public abstract class ConstantCPInfo
  extends ConstantPoolEntry
{
  private Object value;
  
  protected ConstantCPInfo(int tagValue, int entries)
  {
    super(tagValue, entries);
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public void setValue(Object newValue)
  {
    value = newValue;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */