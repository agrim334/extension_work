package org.apache.tools.ant.types.optional;

import java.util.ArrayList;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.ScriptRunnerBase;

public class ScriptMapper
  extends AbstractScriptComponent
  implements FileNameMapper
{
  private ArrayList<String> files;
  
  public void setFrom(String from) {}
  
  public void setTo(String to) {}
  
  public void clear()
  {
    files = new ArrayList(1);
  }
  
  public void addMappedName(String mapping)
  {
    files.add(mapping);
  }
  
  public String[] mapFileName(String sourceFileName)
  {
    initScriptRunner();
    getRunner().addBean("source", sourceFileName);
    clear();
    executeScript("ant_mapper");
    if (files.isEmpty()) {
      return null;
    }
    return (String[])files.toArray(new String[files.size()]);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.optional.ScriptMapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */