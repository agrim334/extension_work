package org.apache.tools.ant.taskdefs.optional.depend;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ClassCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry;

public class ClassFile
{
  private static final int CLASS_MAGIC = -889275714;
  private ConstantPool constantPool;
  private String className;
  
  public void read(InputStream stream)
    throws IOException, ClassFormatError
  {
    DataInputStream classStream = new DataInputStream(stream);
    if (classStream.readInt() != -889275714) {
      throw new ClassFormatError("No Magic Code Found - probably not a Java class file.");
    }
    classStream.readUnsignedShort();
    classStream.readUnsignedShort();
    
    constantPool = new ConstantPool();
    
    constantPool.read(classStream);
    constantPool.resolve();
    
    classStream.readUnsignedShort();
    int thisClassIndex = classStream.readUnsignedShort();
    classStream.readUnsignedShort();
    
    ClassCPInfo classInfo = (ClassCPInfo)constantPool.getEntry(thisClassIndex);
    className = classInfo.getClassName();
  }
  
  public Vector<String> getClassRefs()
  {
    Vector<String> classRefs = new Vector();
    
    int size = constantPool.size();
    for (int i = 0; i < size; i++)
    {
      ConstantPoolEntry entry = constantPool.getEntry(i);
      if ((entry != null) && 
        (entry.getTag() == 7))
      {
        ClassCPInfo classEntry = (ClassCPInfo)entry;
        if (!classEntry.getClassName().equals(className)) {
          classRefs.add(
            ClassFileUtils.convertSlashName(classEntry.getClassName()));
        }
      }
    }
    return classRefs;
  }
  
  public String getFullClassName()
  {
    return ClassFileUtils.convertSlashName(className);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.ClassFile
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */