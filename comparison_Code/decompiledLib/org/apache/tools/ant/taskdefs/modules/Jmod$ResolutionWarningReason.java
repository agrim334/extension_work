package org.apache.tools.ant.taskdefs.modules;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class Jmod$ResolutionWarningReason
  extends EnumeratedAttribute
{
  public static final String DEPRECATED = "deprecated";
  public static final String LEAVING = "leaving";
  public static final String INCUBATING = "incubating";
  private static final Map<String, String> VALUES_TO_OPTIONS;
  
  static
  {
    Map<String, String> map = new LinkedHashMap();
    map.put("deprecated", "deprecated");
    map.put("leaving", "deprecated-for-removal");
    map.put("incubating", "incubating");
    
    VALUES_TO_OPTIONS = Collections.unmodifiableMap(map);
  }
  
  public String[] getValues()
  {
    return (String[])VALUES_TO_OPTIONS.keySet().toArray(new String[0]);
  }
  
  String toCommandLineOption()
  {
    return (String)VALUES_TO_OPTIONS.get(getValue());
  }
  
  public static ResolutionWarningReason valueOf(String s)
  {
    return 
      (ResolutionWarningReason)getInstance(ResolutionWarningReason.class, s);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.modules.Jmod.ResolutionWarningReason
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */