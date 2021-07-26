package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class Javadoc$ExtensionInfo
  extends ProjectComponent
{
  private String name;
  private Path path;
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setPath(Path path)
  {
    if (this.path == null) {
      this.path = path;
    } else {
      this.path.append(path);
    }
  }
  
  public Path getPath()
  {
    return path;
  }
  
  public Path createPath()
  {
    if (path == null) {
      path = new Path(getProject());
    }
    return path.createPath();
  }
  
  public void setPathRef(Reference r)
  {
    createPath().setRefid(r);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Javadoc.ExtensionInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */