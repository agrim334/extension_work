package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;

public class PathConvert$MapEntry
{
  private String from = null;
  private String to = null;
  
  public PathConvert$MapEntry(PathConvert this$0) {}
  
  public void setFrom(String from)
  {
    this.from = from;
  }
  
  public void setTo(String to)
  {
    this.to = to;
  }
  
  public String apply(String elem)
  {
    if ((from == null) || (to == null)) {
      throw new BuildException("Both 'from' and 'to' must be set in a map entry");
    }
    String cmpElem = PathConvert.access$000() ? elem.toLowerCase().replace('\\', '/') : elem;
    
    String cmpFrom = PathConvert.access$000() ? from.toLowerCase().replace('\\', '/') : from;
    
    return cmpElem.startsWith(cmpFrom) ? 
      to + elem.substring(from.length()) : elem;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.PathConvert.MapEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */