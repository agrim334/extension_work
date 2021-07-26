package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;

public abstract class Unpack
  extends Task
{
  protected File source;
  protected File dest;
  protected Resource srcResource;
  
  @Deprecated
  public void setSrc(String src)
  {
    log("DEPRECATED - The setSrc(String) method has been deprecated. Use setSrc(File) instead.");
    
    setSrc(getProject().resolveFile(src));
  }
  
  @Deprecated
  public void setDest(String dest)
  {
    log("DEPRECATED - The setDest(String) method has been deprecated. Use setDest(File) instead.");
    
    setDest(getProject().resolveFile(dest));
  }
  
  public void setSrc(File src)
  {
    setSrcResource(new FileResource(src));
  }
  
  public void setSrcResource(Resource src)
  {
    if (!src.isExists()) {
      throw new BuildException("the archive %s doesn't exist", new Object[] {src.getName() });
    }
    if (src.isDirectory()) {
      throw new BuildException("the archive %s can't be a directory", new Object[] {src.getName() });
    }
    FileProvider fp = (FileProvider)src.as(FileProvider.class);
    if (fp != null) {
      source = fp.getFile();
    } else if (!supportsNonFileResources()) {
      throw new BuildException("The source %s is not a FileSystem Only FileSystem resources are supported.", new Object[] {src.getName() });
    }
    srcResource = src;
  }
  
  public void addConfigured(ResourceCollection a)
  {
    if (a.size() != 1) {
      throw new BuildException("only single argument resource collections are supported as archives");
    }
    setSrcResource((Resource)a.iterator().next());
  }
  
  public void setDest(File dest)
  {
    this.dest = dest;
  }
  
  private void validate()
    throws BuildException
  {
    if (srcResource == null) {
      throw new BuildException("No Src specified", getLocation());
    }
    if (dest == null)
    {
      if (source == null) {
        throw new BuildException("dest is required when using a non-filesystem source", getLocation());
      }
      dest = new File(source.getParent());
    }
    if (dest.isDirectory())
    {
      String defaultExtension = getDefaultExtension();
      createDestFile(defaultExtension);
    }
  }
  
  private void createDestFile(String defaultExtension)
  {
    String sourceName = source == null ? getLastNamePart(srcResource) : source.getName();
    int len = sourceName.length();
    if ((defaultExtension != null) && 
      (len > defaultExtension.length()) && 
      (defaultExtension.equalsIgnoreCase(sourceName
      .substring(len - defaultExtension.length())))) {
      dest = new File(dest, sourceName.substring(0, len - defaultExtension
        .length()));
    } else {
      dest = new File(dest, sourceName);
    }
  }
  
  /* Error */
  public void execute()
    throws BuildException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 109	org/apache/tools/ant/taskdefs/Unpack:dest	Ljava/io/File;
    //   4: astore_1
    //   5: aload_0
    //   6: invokespecial 160	org/apache/tools/ant/taskdefs/Unpack:validate	()V
    //   9: aload_0
    //   10: invokevirtual 163	org/apache/tools/ant/taskdefs/Unpack:extract	()V
    //   13: aload_0
    //   14: aload_1
    //   15: putfield 109	org/apache/tools/ant/taskdefs/Unpack:dest	Ljava/io/File;
    //   18: goto +11 -> 29
    //   21: astore_2
    //   22: aload_0
    //   23: aload_1
    //   24: putfield 109	org/apache/tools/ant/taskdefs/Unpack:dest	Ljava/io/File;
    //   27: aload_2
    //   28: athrow
    //   29: return
    // Line number table:
    //   Java source line #168	-> byte code offset #0
    //   Java source line #170	-> byte code offset #5
    //   Java source line #171	-> byte code offset #9
    //   Java source line #173	-> byte code offset #13
    //   Java source line #174	-> byte code offset #18
    //   Java source line #173	-> byte code offset #21
    //   Java source line #174	-> byte code offset #27
    //   Java source line #175	-> byte code offset #29
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	30	0	this	Unpack
    //   4	20	1	savedDest	File
    //   21	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	13	21	finally
  }
  
  protected abstract String getDefaultExtension();
  
  protected abstract void extract();
  
  protected boolean supportsNonFileResources()
  {
    return false;
  }
  
  private String getLastNamePart(Resource r)
  {
    String n = r.getName();
    int idx = n.lastIndexOf('/');
    return idx < 0 ? n : n.substring(idx + 1);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Unpack
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */