package org.apache.tools.ant.taskdefs.optional.depend;

public class ClassFileUtils
{
  public static String convertSlashName(String name)
  {
    return name.replace('\\', '.').replace('/', '.');
  }
  
  public static String convertDotName(String dotName)
  {
    return dotName.replace('.', '/');
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.ClassFileUtils
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */