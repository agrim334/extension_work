package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class ModuleCPInfo
  extends ConstantCPInfo
{
  private int moduleNameIndex;
  private String moduleName;
  
  public ModuleCPInfo()
  {
    super(19, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    moduleNameIndex = cpStream.readUnsignedShort();
  }
  
  public void resolve(ConstantPool constantPool)
  {
    moduleName = ((Utf8CPInfo)constantPool.getEntry(moduleNameIndex)).getValue();
    
    super.resolve(constantPool);
  }
  
  public String toString()
  {
    return "Module info Constant Pool Entry for " + moduleName + "[" + moduleNameIndex + "]";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.ModuleCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */