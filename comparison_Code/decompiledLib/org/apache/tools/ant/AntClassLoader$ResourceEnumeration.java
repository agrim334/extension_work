package org.apache.tools.ant;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

class AntClassLoader$ResourceEnumeration
  implements Enumeration<URL>
{
  private final String resourceName;
  private int pathElementsIndex;
  private URL nextResource;
  
  AntClassLoader$ResourceEnumeration(AntClassLoader paramAntClassLoader, String name)
  {
    resourceName = name;
    pathElementsIndex = 0;
    findNextResource();
  }
  
  public boolean hasMoreElements()
  {
    return nextResource != null;
  }
  
  public URL nextElement()
  {
    URL ret = nextResource;
    if (ret == null) {
      throw new NoSuchElementException();
    }
    findNextResource();
    return ret;
  }
  
  private void findNextResource()
  {
    URL url = null;
    while ((pathElementsIndex < AntClassLoader.access$000(this$0).size()) && (url == null)) {
      try
      {
        File pathComponent = (File)AntClassLoader.access$000(this$0).elementAt(pathElementsIndex);
        url = this$0.getResourceURL(pathComponent, resourceName);
        pathElementsIndex += 1;
      }
      catch (BuildException localBuildException) {}
    }
    nextResource = url;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.AntClassLoader.ResourceEnumeration
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */