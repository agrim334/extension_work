package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResourceIterator;

class Delete$ReverseDirs
  implements ResourceCollection
{
  private Project project;
  private File basedir;
  private String[] dirs;
  
  Delete$ReverseDirs(Project project, File basedir, String[] dirs)
  {
    this.project = project;
    this.basedir = basedir;
    this.dirs = dirs;
    Arrays.sort(this.dirs, Comparator.reverseOrder());
  }
  
  public Iterator<Resource> iterator()
  {
    return new FileResourceIterator(project, basedir, dirs);
  }
  
  public boolean isFilesystemOnly()
  {
    return true;
  }
  
  public int size()
  {
    return dirs.length;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Delete.ReverseDirs
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */