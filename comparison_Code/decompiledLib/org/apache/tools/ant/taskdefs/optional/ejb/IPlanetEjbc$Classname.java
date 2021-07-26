package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;

class IPlanetEjbc$Classname
{
  private String qualifiedName;
  private String packageName;
  private String className;
  
  public IPlanetEjbc$Classname(String qualifiedName)
  {
    if (qualifiedName == null) {
      return;
    }
    this.qualifiedName = qualifiedName;
    
    int index = qualifiedName.lastIndexOf('.');
    if (index == -1)
    {
      className = qualifiedName;
      packageName = "";
    }
    else
    {
      packageName = qualifiedName.substring(0, index);
      className = qualifiedName.substring(index + 1);
    }
  }
  
  public String getQualifiedClassName()
  {
    return qualifiedName;
  }
  
  public String getPackageName()
  {
    return packageName;
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public String getQualifiedWithUnderscores()
  {
    return qualifiedName.replace('.', '_');
  }
  
  public File getClassFile(File directory)
  {
    String pathToFile = qualifiedName.replace('.', File.separatorChar) + ".class";
    
    return new File(directory, pathToFile);
  }
  
  public String toString()
  {
    return getQualifiedClassName();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbc.Classname
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */