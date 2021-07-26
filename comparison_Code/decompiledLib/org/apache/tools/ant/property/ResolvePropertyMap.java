package org.apache.tools.ant.property;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class ResolvePropertyMap
  implements GetProperty
{
  private final Set<String> seen = new HashSet();
  private final ParseProperties parseProperties;
  private final GetProperty master;
  private Map<String, Object> map;
  private String prefix;
  private boolean prefixValues = false;
  private boolean expandingLHS = true;
  
  public ResolvePropertyMap(Project project, GetProperty master, Collection<PropertyExpander> expanders)
  {
    this.master = master;
    parseProperties = new ParseProperties(project, expanders, this);
  }
  
  public Object getProperty(String name)
  {
    if (seen.contains(name)) {
      throw new BuildException("Property %s was circularly defined.", new Object[] { name });
    }
    try
    {
      String fullKey = name;
      if ((prefix != null) && ((expandingLHS) || (prefixValues))) {
        fullKey = prefix + name;
      }
      Object masterValue = master.getProperty(fullKey);
      if (masterValue != null) {
        return masterValue;
      }
      seen.add(name);
      
      String recursiveCallKey = name;
      if ((prefix != null) && (!expandingLHS) && (!prefixValues)) {
        recursiveCallKey = prefix + name;
      }
      expandingLHS = false;
      
      return parseProperties.parseProperties((String)map.get(recursiveCallKey));
    }
    finally
    {
      seen.remove(name);
    }
  }
  
  @Deprecated
  public void resolveAllProperties(Map<String, Object> map)
  {
    resolveAllProperties(map, null, false);
  }
  
  @Deprecated
  public void resolveAllProperties(Map<String, Object> map, String prefix)
  {
    resolveAllProperties(map, null, false);
  }
  
  public void resolveAllProperties(Map<String, Object> map, String prefix, boolean prefixValues)
  {
    this.map = map;
    this.prefix = prefix;
    this.prefixValues = prefixValues;
    for (String key : map.keySet())
    {
      expandingLHS = true;
      Object result = getProperty(key);
      String value = result == null ? "" : result.toString();
      map.put(key, value);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.property.ResolvePropertyMap
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */