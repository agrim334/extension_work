package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class PackageCPInfo
  extends ConstantCPInfo
{
  private int packageNameIndex;
  private String packageName;
  
  public PackageCPInfo()
  {
    super(20, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    packageNameIndex = cpStream.readUnsignedShort();
  }
  
  public void resolve(ConstantPool constantPool)
  {
    packageName = ((Utf8CPInfo)constantPool.getEntry(packageNameIndex)).getValue();
    
    super.resolve(constantPool);
  }
  
  public String toString()
  {
    return "Package info Constant Pool Entry for " + packageName + "[" + packageNameIndex + "]";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.PackageCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */