package org.apache.tools.ant.types;

import java.io.File;
import java.util.Iterator;
import org.apache.tools.ant.types.resources.FileResourceIterator;

public class Path$PathElement
  implements ResourceCollection
{
  private String[] parts;
  
  public Path$PathElement(Path this$0) {}
  
  public void setLocation(File loc)
  {
    parts = new String[] { Path.translateFile(loc.getAbsolutePath()) };
  }
  
  public void setPath(String path)
  {
    parts = Path.translatePath(this$0.getProject(), path);
  }
  
  public String[] getParts()
  {
    return parts;
  }
  
  public Iterator<Resource> iterator()
  {
    return new FileResourceIterator(this$0.getProject(), null, parts);
  }
  
  public boolean isFilesystemOnly()
  {
    return true;
  }
  
  public int size()
  {
    return parts == null ? 0 : parts.length;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Path.PathElement
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */