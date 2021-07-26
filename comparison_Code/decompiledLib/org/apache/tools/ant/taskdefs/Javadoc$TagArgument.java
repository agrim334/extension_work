package org.apache.tools.ant.taskdefs;

import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

public class Javadoc$TagArgument
  extends FileSet
{
  private String name = null;
  private boolean enabled = true;
  private String scope = "a";
  
  public Javadoc$TagArgument(Javadoc this$0) {}
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setScope(String verboseScope)
    throws BuildException
  {
    verboseScope = verboseScope.toLowerCase(Locale.ENGLISH);
    
    boolean[] elements = new boolean[Javadoc.SCOPE_ELEMENTS.length];
    
    boolean gotAll = false;
    boolean gotNotAll = false;
    
    StringTokenizer tok = new StringTokenizer(verboseScope, ",");
    while (tok.hasMoreTokens())
    {
      String next = tok.nextToken().trim();
      if ("all".equals(next))
      {
        if (gotAll) {
          getProject().log("Repeated tag scope element: all", 3);
        }
        gotAll = true;
      }
      else
      {
        for (int i = 0; i < Javadoc.SCOPE_ELEMENTS.length; i++) {
          if (Javadoc.SCOPE_ELEMENTS[i].equals(next)) {
            break;
          }
        }
        if (i == Javadoc.SCOPE_ELEMENTS.length) {
          throw new BuildException("Unrecognised scope element: %s", new Object[] { next });
        }
        if (elements[i] != 0) {
          getProject().log("Repeated tag scope element: " + next, 3);
        }
        elements[i] = true;
        gotNotAll = true;
      }
    }
    if ((gotNotAll) && (gotAll)) {
      throw new BuildException("Mixture of \"all\" and other scope elements in tag parameter.");
    }
    if ((!gotNotAll) && (!gotAll)) {
      throw new BuildException("No scope elements specified in tag parameter.");
    }
    if (gotAll)
    {
      scope = "a";
    }
    else
    {
      StringBuilder buff = new StringBuilder(elements.length);
      for (int i = 0; i < elements.length; i++) {
        if (elements[i] != 0) {
          buff.append(Javadoc.SCOPE_ELEMENTS[i].charAt(0));
        }
      }
      scope = buff.toString();
    }
  }
  
  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }
  
  public String getParameter()
    throws BuildException
  {
    if ((name == null) || (name.isEmpty())) {
      throw new BuildException("No name specified for custom tag.");
    }
    if (getDescription() != null) {
      return 
        name + ":" + (enabled ? "" : "X") + scope + ":" + getDescription();
    }
    if ((!enabled) || (!"a".equals(scope))) {
      return name + ":" + (enabled ? "" : "X") + scope;
    }
    return name;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Javadoc.TagArgument
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */