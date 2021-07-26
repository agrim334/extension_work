package org.apache.tools.ant.types;

import java.util.Properties;

public class Mapper$MapperType
  extends EnumeratedAttribute
{
  private Properties implementations;
  
  public Mapper$MapperType()
  {
    implementations = new Properties();
    implementations.put("identity", "org.apache.tools.ant.util.IdentityMapper");
    
    implementations.put("flatten", "org.apache.tools.ant.util.FlatFileNameMapper");
    
    implementations.put("glob", "org.apache.tools.ant.util.GlobPatternMapper");
    
    implementations.put("merge", "org.apache.tools.ant.util.MergingMapper");
    
    implementations.put("regexp", "org.apache.tools.ant.util.RegexpPatternMapper");
    
    implementations.put("package", "org.apache.tools.ant.util.PackageNameMapper");
    
    implementations.put("unpackage", "org.apache.tools.ant.util.UnPackageNameMapper");
  }
  
  public String[] getValues()
  {
    return new String[] { "identity", "flatten", "glob", "merge", "regexp", "package", "unpackage" };
  }
  
  public String getImplementation()
  {
    return implementations.getProperty(getValue());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Mapper.MapperType
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */