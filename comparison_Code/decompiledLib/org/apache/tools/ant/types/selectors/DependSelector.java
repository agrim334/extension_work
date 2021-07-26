package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.types.Mapper;

public class DependSelector
  extends MappingSelector
{
  public String toString()
  {
    StringBuilder buf = new StringBuilder("{dependselector targetdir: ");
    if (targetdir == null) {
      buf.append("NOT YET SET");
    } else {
      buf.append(targetdir.getName());
    }
    buf.append(" granularity: ").append(granularity);
    if (map != null)
    {
      buf.append(" mapper: ");
      buf.append(map.toString());
    }
    else if (mapperElement != null)
    {
      buf.append(" mapper: ");
      buf.append(mapperElement.toString());
    }
    buf.append("}");
    return buf.toString();
  }
  
  public boolean selectionTest(File srcfile, File destfile)
  {
    return SelectorUtils.isOutOfDate(srcfile, destfile, granularity);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.DependSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */