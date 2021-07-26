package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.DOMElementWriter.XmlNamespacePolicy;

public class EchoXML$NamespacePolicy
  extends EnumeratedAttribute
{
  private static final String IGNORE = "ignore";
  private static final String ELEMENTS = "elementsOnly";
  private static final String ALL = "all";
  public static final NamespacePolicy DEFAULT = new NamespacePolicy("ignore");
  
  public EchoXML$NamespacePolicy() {}
  
  public EchoXML$NamespacePolicy(String s)
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

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.EchoXML.NamespacePolicy
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */