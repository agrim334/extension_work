package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.PropertyOutputStream;

public class Length
  extends Task
  implements Condition
{
  private static final String ALL = "all";
  private static final String EACH = "each";
  private static final String STRING = "string";
  private static final String LENGTH_REQUIRED = "Use of the Length condition requires that the length attribute be set.";
  private String property;
  private String string;
  private Boolean trim;
  private String mode = "all";
  private Comparison when = Comparison.EQUAL;
  private Long length;
  private Resources resources;
  
  public synchronized void setProperty(String property)
  {
    this.property = property;
  }
  
  public synchronized void setResource(Resource resource)
  {
    add(resource);
  }
  
  public synchronized void setFile(File file)
  {
    add(new FileResource(file));
  }
  
  public synchronized void add(FileSet fs)
  {
    add(fs);
  }
  
  public synchronized void add(ResourceCollection c)
  {
    if (c == null) {
      return;
    }
    resources = (resources == null ? new Resources() : resources);
    resources.add(c);
  }
  
  public synchronized void setLength(long ell)
  {
    length = Long.valueOf(ell);
  }
  
  public synchronized void setWhen(When w)
  {
    setWhen(w);
  }
  
  public synchronized void setWhen(Comparison c)
  {
    when = c;
  }
  
  public synchronized void setMode(FileMode m)
  {
    mode = m.getValue();
  }
  
  public synchronized void setString(String string)
  {
    this.string = string;
    mode = "string";
  }
  
  public synchronized void setTrim(boolean trim)
  {
    this.trim = Boolean.valueOf(trim);
  }
  
  public boolean getTrim()
  {
    return Boolean.TRUE.equals(trim);
  }
  
  public void execute()
  {
    validate();
    
    OutputStream out = property == null ? new LogOutputStream(this, 2) : new PropertyOutputStream(getProject(), property);
    PrintStream ps = new PrintStream(out);
    switch (mode)
    {
    case "string": 
      ps.print(getLength(string, getTrim()));
      ps.close();
      break;
    case "each": 
      handleResources(new EachHandler(ps));
      break;
    case "all": 
      handleResources(new AllHandler(ps));
    }
  }
  
  public boolean eval()
  {
    validate();
    if (length == null) {
      throw new BuildException("Use of the Length condition requires that the length attribute be set.");
    }
    Long ell;
    Long ell;
    if ("string".equals(mode))
    {
      ell = Long.valueOf(getLength(string, getTrim()));
    }
    else
    {
      AccumHandler h = new AccumHandler();
      handleResources(h);
      ell = Long.valueOf(h.getAccum());
    }
    return when.evaluate(ell.compareTo(length));
  }
  
  private void validate()
  {
    if (string != null)
    {
      if (resources != null) {
        throw new BuildException("the string length function is incompatible with the file/resource length function");
      }
      if (!"string".equals(mode)) {
        throw new BuildException("the mode attribute is for use with the file/resource length function");
      }
    }
    else if (resources != null)
    {
      if ((!"each".equals(mode)) && (!"all".equals(mode))) {
        throw new BuildException("invalid mode setting for file/resource length function: \"" + mode + "\"");
      }
      if (trim != null) {
        throw new BuildException("the trim attribute is for use with the string length function only");
      }
    }
    else
    {
      throw new BuildException("you must set either the string attribute or specify one or more files using the file attribute or nested resource collections");
    }
  }
  
  private void handleResources(Handler h)
  {
    for (Resource r : resources)
    {
      if (!r.isExists()) {
        log(r + " does not exist", 1);
      }
      if (r.isDirectory()) {
        log(r + " is a directory; length may not be meaningful", 1);
      }
      h.handle(r);
    }
    h.complete();
  }
  
  private static long getLength(String s, boolean t)
  {
    return (t ? s.trim() : s).length();
  }
  
  public static class FileMode
    extends EnumeratedAttribute
  {
    static final String[] MODES = { "each", "all" };
    
    public String[] getValues()
    {
      return MODES;
    }
  }
  
  private abstract class Handler
  {
    private PrintStream ps;
    
    Handler(PrintStream ps)
    {
      this.ps = ps;
    }
    
    protected PrintStream getPs()
    {
      return ps;
    }
    
    protected abstract void handle(Resource paramResource);
    
    void complete()
    {
      FileUtils.close(ps);
    }
  }
  
  private class EachHandler
    extends Length.Handler
  {
    EachHandler(PrintStream ps)
    {
      super(ps);
    }
    
    protected void handle(Resource r)
    {
      getPs().print(r.toString());
      getPs().print(" : ");
      
      long size = r.getSize();
      if (size == -1L) {
        getPs().println("unknown");
      } else {
        getPs().println(size);
      }
    }
  }
  
  private class AccumHandler
    extends Length.Handler
  {
    private long accum = 0L;
    
    AccumHandler()
    {
      super(null);
    }
    
    protected AccumHandler(PrintStream ps)
    {
      super(ps);
    }
    
    protected long getAccum()
    {
      return accum;
    }
    
    protected synchronized void handle(Resource r)
    {
      long size = r.getSize();
      if (size == -1L) {
        log("Size unknown for " + r.toString(), 1);
      } else {
        accum += size;
      }
    }
  }
  
  private class AllHandler
    extends Length.AccumHandler
  {
    AllHandler(PrintStream ps)
    {
      super(ps);
    }
    
    void complete()
    {
      getPs().print(getAccum());
      super.complete();
    }
  }
  
  public static class When
    extends Comparison
  {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Length
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */