package org.apache.tools.ant.types.resources;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;

public abstract class AbstractClasspathResource
  extends Resource
{
  private Path classpath;
  private Reference loader;
  private boolean parentFirst = true;
  
  public void setClasspath(Path classpath)
  {
    checkAttributesAllowed();
    if (this.classpath == null) {
      this.classpath = classpath;
    } else {
      this.classpath.append(classpath);
    }
    setChecked(false);
  }
  
  public Path createClasspath()
  {
    checkChildrenAllowed();
    if (classpath == null) {
      classpath = new Path(getProject());
    }
    setChecked(false);
    return classpath.createPath();
  }
  
  public void setClasspathRef(Reference r)
  {
    checkAttributesAllowed();
    createClasspath().setRefid(r);
  }
  
  public Path getClasspath()
  {
    if (isReference()) {
      return getRef().getClasspath();
    }
    dieOnCircularReference();
    return classpath;
  }
  
  public Reference getLoader()
  {
    if (isReference()) {
      return getRef().getLoader();
    }
    dieOnCircularReference();
    return loader;
  }
  
  public void setLoaderRef(Reference r)
  {
    checkAttributesAllowed();
    loader = r;
  }
  
  public void setParentFirst(boolean b)
  {
    parentFirst = b;
  }
  
  public void setRefid(Reference r)
  {
    if ((loader != null) || (classpath != null)) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public boolean isExists()
  {
    if (isReference()) {
      return getRef().isExists();
    }
    dieOnCircularReference();
    try
    {
      InputStream is = getInputStream();
      try
      {
        boolean bool = is != null;
        if (is != null) {
          is.close();
        }
        return bool;
      }
      catch (Throwable localThrowable2)
      {
        if (is != null) {
          try
          {
            is.close();
          }
          catch (Throwable localThrowable1)
          {
            localThrowable2.addSuppressed(localThrowable1);
          }
        }
        throw localThrowable2;
      }
      return false;
    }
    catch (IOException ex) {}
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getInputStream();
    }
    dieOnCircularReference();
    
    final ClassLoaderWithFlag classLoader = getClassLoader();
    !classLoader.needsCleanup() ? 
      openInputStream(classLoader.getLoader()) : 
      new FilterInputStream(openInputStream(classLoader.getLoader()))
      {
        public void close()
          throws IOException
        {
          FileUtils.close(in);
          classLoader.cleanup();
        }
        
        /* Error */
        protected void finalize()
          throws Throwable
        {
          // Byte code:
          //   0: aload_0
          //   1: invokevirtual 32	org/apache/tools/ant/types/resources/AbstractClasspathResource$1:close	()V
          //   4: aload_0
          //   5: invokespecial 34	java/lang/Object:finalize	()V
          //   8: goto +10 -> 18
          //   11: astore_1
          //   12: aload_0
          //   13: invokespecial 34	java/lang/Object:finalize	()V
          //   16: aload_1
          //   17: athrow
          //   18: return
          // Line number table:
          //   Java source line #189	-> byte code offset #0
          //   Java source line #191	-> byte code offset #4
          //   Java source line #192	-> byte code offset #8
          //   Java source line #191	-> byte code offset #11
          //   Java source line #192	-> byte code offset #16
          //   Java source line #193	-> byte code offset #18
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	19	0	this	1
          //   11	6	1	localObject	Object
          // Exception table:
          //   from	to	target	type
          //   0	4	11	finally
        }
      };
    }
    
    protected ClassLoaderWithFlag getClassLoader()
    {
      ClassLoader cl = null;
      if (loader != null) {
        cl = (ClassLoader)loader.getReferencedObject();
      }
      boolean clNeedsCleanup = false;
      if (cl == null)
      {
        if (getClasspath() != null)
        {
          Path p = getClasspath().concatSystemClasspath("ignore");
          if (parentFirst) {
            cl = getProject().createClassLoader(p);
          } else {
            cl = AntClassLoader.newAntClassLoader(getProject()
              .getCoreLoader(), 
              getProject(), p, false);
          }
          clNeedsCleanup = loader == null;
        }
        else
        {
          cl = JavaResource.class.getClassLoader();
        }
        if ((loader != null) && (cl != null)) {
          getProject().addReference(loader.getRefId(), cl);
        }
      }
      return new ClassLoaderWithFlag(cl, clNeedsCleanup);
    }
    
    protected abstract InputStream openInputStream(ClassLoader paramClassLoader)
      throws IOException;
    
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p)
    {
      if (isChecked()) {
        return;
      }
      if (isReference())
      {
        super.dieOnCircularReference(stk, p);
      }
      else
      {
        if (classpath != null) {
          pushAndInvokeCircularReferenceCheck(classpath, stk, p);
        }
        setChecked(true);
      }
    }
    
    protected AbstractClasspathResource getRef()
    {
      return (AbstractClasspathResource)getCheckedRef(AbstractClasspathResource.class);
    }
    
    public static class ClassLoaderWithFlag
    {
      private final ClassLoader loader;
      private final boolean cleanup;
      
      ClassLoaderWithFlag(ClassLoader l, boolean needsCleanup)
      {
        loader = l;
        cleanup = ((needsCleanup) && ((l instanceof AntClassLoader)));
      }
      
      public ClassLoader getLoader()
      {
        return loader;
      }
      
      public boolean needsCleanup()
      {
        return cleanup;
      }
      
      public void cleanup()
      {
        if (cleanup) {
          ((AntClassLoader)loader).cleanup();
        }
      }
    }
  }

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.AbstractClasspathResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */