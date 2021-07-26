package org.apache.tools.ant.taskdefs.modules;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class Link$CompressionLevel
  extends EnumeratedAttribute
{
  private static final Map<String, String> KEYWORDS;
  
  static
  {
    Map<String, String> map = new LinkedHashMap();
    map.put("0", "0");
    map.put("1", "1");
    map.put("2", "2");
    map.put("none", "0");
    map.put("strings", "1");
    map.put("zip", "2");
    
    KEYWORDS = Collections.unmodifiableMap(map);
  }
  
  public String[] getValues()
  {
    return (String[])KEYWORDS.keySet().toArray(new String[0]);
  }
  
  String toCommandLineOption()
  {
    return (String)KEYWORDS.get(getValue());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.modules.Link.CompressionLevel
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */