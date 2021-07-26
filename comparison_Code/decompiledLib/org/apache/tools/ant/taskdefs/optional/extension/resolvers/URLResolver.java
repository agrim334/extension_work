package org.apache.tools.ant.taskdefs.optional.extension.resolvers;

import java.io.File;
import java.net.URL;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Get;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;

public class URLResolver
  implements ExtensionResolver
{
  private File destfile;
  private File destdir;
  private URL url;
  
  public void setUrl(URL url)
  {
    this.url = url;
  }
  
  public void setDestfile(File destfile)
  {
    this.destfile = destfile;
  }
  
  public void setDestdir(File destdir)
  {
    this.destdir = destdir;
  }
  
  public File resolve(Extension extension, Project project)
    throws BuildException
  {
    validate();
    
    File file = getDest();
    
    Get get = new Get();
    get.setProject(project);
    get.setDest(file);
    get.setSrc(url);
    get.execute();
    
    return file;
  }
  
  private File getDest()
  {
    File result;
    File result;
    if (null != destfile)
    {
      result = destfile;
    }
    else
    {
      String file = url.getFile();
      String filename;
      String filename;
      if ((null == file) || (file.length() <= 1))
      {
        filename = "default.file";
      }
      else
      {
        int index = file.lastIndexOf('/');
        if (-1 == index) {
          index = 0;
        }
        filename = file.substring(index);
      }
      result = new File(destdir, filename);
    }
    return result;
  }
  
  private void validate()
  {
    if (null == url) {
      throw new BuildException("Must specify URL");
    }
    if ((null == destdir) && (null == destfile)) {
      throw new BuildException("Must specify destination file or directory");
    }
    if ((null != destdir) && (null != destfile)) {
      throw new BuildException("Must not specify both destination file or directory");
    }
  }
  
  public String toString()
  {
    return "URL[" + url + "]";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.extension.resolvers.URLResolver
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */