package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Iterator;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResourceIterator;

class Delete$1
  implements ResourceCollection
{
  public boolean isFilesystemOnly()
  {
    return true;
  }
  
  public int size()
  {
    return val$files.length;
  }
  
  public Iterator<Resource> iterator()
  {
    return new FileResourceIterator(this$0.getProject(), val$fsDir, val$files);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Delete.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */