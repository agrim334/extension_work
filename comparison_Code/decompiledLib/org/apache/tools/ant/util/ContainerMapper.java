package org.apache.tools.ant.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.types.Mapper;

public abstract class ContainerMapper
  implements FileNameMapper
{
  private List<FileNameMapper> mappers = new ArrayList();
  
  public void addConfiguredMapper(Mapper mapper)
  {
    add(mapper.getImplementation());
  }
  
  public void addConfigured(FileNameMapper fileNameMapper)
  {
    add(fileNameMapper);
  }
  
  public synchronized void add(FileNameMapper fileNameMapper)
  {
    if (this != fileNameMapper)
    {
      if ((fileNameMapper instanceof ContainerMapper)) {
        if (!((ContainerMapper)fileNameMapper).contains(this)) {}
      }
    }
    else {
      throw new IllegalArgumentException("Circular mapper containment condition detected");
    }
    mappers.add(fileNameMapper);
  }
  
  protected synchronized boolean contains(FileNameMapper fileNameMapper)
  {
    for (FileNameMapper m : mappers)
    {
      if (m == fileNameMapper) {
        return true;
      }
      if (((m instanceof ContainerMapper)) && 
        (((ContainerMapper)m).contains(fileNameMapper))) {
        return true;
      }
    }
    return false;
  }
  
  public synchronized List<FileNameMapper> getMappers()
  {
    return Collections.unmodifiableList(mappers);
  }
  
  public void setFrom(String ignore) {}
  
  public void setTo(String ignore) {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.ContainerMapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */