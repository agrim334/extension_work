package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class ClassCPInfo
  extends ConstantPoolEntry
{
  private String className;
  private int index;
  
  public ClassCPInfo()
  {
    super(7, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    index = cpStream.readUnsignedShort();
    className = "unresolved";
  }
  
  public String toString()
  {
    return "Class Constant Pool Entry for " + className + "[" + index + "]";
  }
  
  public void resolve(ConstantPool constantPool)
  {
    className = ((Utf8CPInfo)constantPool.getEntry(index)).getValue();
    
    super.resolve(constantPool);
  }
  
  public String getClassName()
  {
    return className;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.ClassCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */