package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.StringUtils;

public class Classloader
  extends Task
{
  public static final String SYSTEM_LOADER_REF = "ant.coreLoader";
  private String name = null;
  private Path classpath;
  private boolean reset = false;
  private boolean parentFirst = true;
  private String parentName = null;
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setReset(boolean b)
  {
    reset = b;
  }
  
  @Deprecated
  public void setReverse(boolean b)
  {
    parentFirst = (!b);
  }
  
  public void setParentFirst(boolean b)
  {
    parentFirst = b;
  }
  
  public void setParentName(String name)
  {
    parentName = name;
  }
  
  public void setClasspathRef(Reference pathRef)
    throws BuildException
  {
    classpath = ((Path)pathRef.getReferencedObject(getProject()));
  }
  
  public void setClasspath(Path classpath)
  {
    if (this.classpath == null) {
      this.classpath = classpath;
    } else {
      this.classpath.append(classpath);
    }
  }
  
  public Path createClasspath()
  {
    if (classpath == null) {
      classpath = new Path(null);
    }
    return classpath.createPath();
  }
  
  public void execute()
  {
    try
    {
      if (("only".equals(getProject().getProperty("build.sysclasspath"))) && ((name == null) || 
        ("ant.coreLoader".equals(name))))
      {
        log("Changing the system loader is disabled by build.sysclasspath=only", 1);
        
        return;
      }
      String loaderName = name == null ? "ant.coreLoader" : name;
      
      Object obj = getProject().getReference(loaderName);
      if (reset) {
        obj = null;
      }
      if ((obj != null) && (!(obj instanceof AntClassLoader)))
      {
        log("Referenced object is not an AntClassLoader", 0);
        
        return;
      }
      AntClassLoader acl = (AntClassLoader)obj;
      boolean existingLoader = acl != null;
      Object parent;
      if (acl == null)
      {
        parent = null;
        if (parentName != null)
        {
          parent = getProject().getReference(parentName);
          if (!(parent instanceof ClassLoader)) {
            parent = null;
          }
        }
        if (parent == null) {
          parent = getClass().getClassLoader();
        }
        if (name == null) {}
        getProject().log("Setting parent loader " + name + " " + parent + " " + parentFirst, 4);
        
        acl = AntClassLoader.newAntClassLoader((ClassLoader)parent, 
          getProject(), classpath, parentFirst);
        
        getProject().addReference(loaderName, acl);
        if (name == null)
        {
          acl.addLoaderPackageRoot("org.apache.tools.ant.taskdefs.optional");
          getProject().setCoreLoader(acl);
        }
      }
      if ((existingLoader) && (classpath != null)) {
        for (String path : classpath.list())
        {
          File f = new File(path);
          if (f.exists())
          {
            log("Adding to class loader " + acl + " " + f.getAbsolutePath(), 4);
            
            acl.addPathElement(f.getAbsolutePath());
          }
        }
      }
    }
    catch (Exception ex)
    {
      log(StringUtils.getStackTrace(ex), 0);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Classloader
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */