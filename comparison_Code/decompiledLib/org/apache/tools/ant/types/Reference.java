package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class Reference
{
  private String refid;
  private Project project;
  
  @Deprecated
  public Reference() {}
  
  @Deprecated
  public Reference(String id)
  {
    setRefId(id);
  }
  
  public Reference(Project p, String id)
  {
    setRefId(id);
    setProject(p);
  }
  
  public void setRefId(String id)
  {
    refid = id;
  }
  
  public String getRefId()
  {
    return refid;
  }
  
  public void setProject(Project p)
  {
    project = p;
  }
  
  public Project getProject()
  {
    return project;
  }
  
  public <T> T getReferencedObject(Project fallback)
    throws BuildException
  {
    if (refid == null) {
      throw new BuildException("No reference specified");
    }
    T o = project == null ? fallback.getReference(refid) : project.getReference(refid);
    if (o == null) {
      throw new BuildException("Reference " + refid + " not found.");
    }
    return o;
  }
  
  public <T> T getReferencedObject()
    throws BuildException
  {
    if (project == null) {
      throw new BuildException("No project set on reference to " + refid);
    }
    return (T)getReferencedObject(project);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Reference
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */