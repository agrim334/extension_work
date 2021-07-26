package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.filters.util.ChainReaderHelper.ChainReader;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.JavaResource;

public class LoadProperties
  extends Task
{
  private Resource src = null;
  private final List<FilterChain> filterChains = new Vector();
  private String encoding = null;
  private String prefix = null;
  private boolean prefixValues = true;
  
  public final void setSrcFile(File srcFile)
  {
    addConfigured(new FileResource(srcFile));
  }
  
  public void setResource(String resource)
  {
    getRequiredJavaResource().setName(resource);
  }
  
  public final void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  public void setClasspath(Path classpath)
  {
    getRequiredJavaResource().setClasspath(classpath);
  }
  
  public Path createClasspath()
  {
    return getRequiredJavaResource().createClasspath();
  }
  
  public void setClasspathRef(Reference r)
  {
    getRequiredJavaResource().setClasspathRef(r);
  }
  
  public Path getClasspath()
  {
    return getRequiredJavaResource().getClasspath();
  }
  
  public void setPrefix(String prefix)
  {
    this.prefix = prefix;
  }
  
  public void setPrefixValues(boolean b)
  {
    prefixValues = b;
  }
  
  public final void execute()
    throws BuildException
  {
    if (src == null) {
      throw new BuildException("A source resource is required.");
    }
    if (!src.isExists())
    {
      if ((src instanceof JavaResource))
      {
        log("Unable to find resource " + src, 1);
        return;
      }
      throw new BuildException("Source resource does not exist: " + src);
    }
    Charset charset = encoding == null ? Charset.defaultCharset() : Charset.forName(encoding);
    try
    {
      ChainReaderHelper.ChainReader instream = new ChainReaderHelper(getProject(), new InputStreamReader(new BufferedInputStream(src.getInputStream()), charset), filterChains).getAssembledReader();
      try
      {
        String text = instream.readFully();
        if ((text != null) && (!text.isEmpty()))
        {
          if (!text.endsWith("\n")) {
            text = text + "\n";
          }
          ByteArrayInputStream tis = new ByteArrayInputStream(text.getBytes(StandardCharsets.ISO_8859_1));
          Properties props = new Properties();
          props.load(tis);
          
          Property propertyTask = new Property();
          propertyTask.bindToOwner(this);
          propertyTask.setPrefix(prefix);
          propertyTask.setPrefixValues(prefixValues);
          propertyTask.addProperties(props);
        }
        if (instream == null) {
          return;
        }
        instream.close();
      }
      catch (Throwable localThrowable)
      {
        if (instream == null) {
          break label305;
        }
      }
      try
      {
        instream.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      label305:
      throw localThrowable;
    }
    catch (IOException ioe)
    {
      throw new BuildException("Unable to load file: " + ioe, ioe, getLocation());
    }
  }
  
  public final void addFilterChain(FilterChain filter)
  {
    filterChains.add(filter);
  }
  
  public synchronized void addConfigured(ResourceCollection a)
  {
    if (src != null) {
      throw new BuildException("only a single source is supported");
    }
    if (a.size() != 1) {
      throw new BuildException("only single-element resource collections are supported");
    }
    src = ((Resource)a.iterator().next());
  }
  
  private synchronized JavaResource getRequiredJavaResource()
  {
    if (src == null)
    {
      src = new JavaResource();
      src.setProject(getProject());
    }
    else if (!(src instanceof JavaResource))
    {
      throw new BuildException("expected a java resource as source");
    }
    return (JavaResource)src;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.LoadProperties
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */