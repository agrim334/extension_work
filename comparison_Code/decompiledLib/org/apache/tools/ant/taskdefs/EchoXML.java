package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DOMElementWriter.XmlNamespacePolicy;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.XMLFragment;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EchoXML
  extends XMLFragment
{
  private File file;
  private boolean append;
  private NamespacePolicy namespacePolicy = NamespacePolicy.DEFAULT;
  private static final String ERROR_NO_XML = "No nested XML specified";
  
  public void setFile(File f)
  {
    file = f;
  }
  
  public void setNamespacePolicy(NamespacePolicy n)
  {
    namespacePolicy = n;
  }
  
  public void setAppend(boolean b)
  {
    append = b;
  }
  
  public void execute()
  {
    Node n = getFragment().getFirstChild();
    if (n == null) {
      throw new BuildException("No nested XML specified");
    }
    DOMElementWriter writer = new DOMElementWriter(!append, namespacePolicy.getPolicy());
    try
    {
      OutputStream os = file == null ? new LogOutputStream(this, 2) : FileUtils.newOutputStream(file.toPath(), append);
      try
      {
        writer.write((Element)n, os);
        if (os == null) {
          return;
        }
        os.close();
      }
      catch (Throwable localThrowable)
      {
        if (os == null) {
          break label127;
        }
      }
      try
      {
        os.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      label127:
      throw localThrowable;
    }
    catch (BuildException be)
    {
      throw be;
    }
    catch (Exception e)
    {
      throw new BuildException(e);
    }
  }
  
  public static class NamespacePolicy
    extends EnumeratedAttribute
  {
    private static final String IGNORE = "ignore";
    private static final String ELEMENTS = "elementsOnly";
    private static final String ALL = "all";
    public static final NamespacePolicy DEFAULT = new NamespacePolicy("ignore");
    
    public NamespacePolicy() {}
    
    public NamespacePolicy(String s)
    {
      setValue(s);
    }
    
    public String[] getValues()
    {
      return new String[] { "ignore", "elementsOnly", "all" };
    }
    
    public DOMElementWriter.XmlNamespacePolicy getPolicy()
    {
      String s = getValue();
      if ("ignore".equalsIgnoreCase(s)) {
        return DOMElementWriter.XmlNamespacePolicy.IGNORE;
      }
      if ("elementsOnly".equalsIgnoreCase(s)) {
        return DOMElementWriter.XmlNamespacePolicy.ONLY_QUALIFY_ELEMENTS;
      }
      if ("all".equalsIgnoreCase(s)) {
        return DOMElementWriter.XmlNamespacePolicy.QUALIFY_ALL;
      }
      throw new BuildException("Invalid namespace policy: " + s);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.EchoXML
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */