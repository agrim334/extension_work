package org.apache.tools.ant.taskdefs.cvslib;

class RCSFile
{
  private String name;
  private String revision;
  private String previousRevision;
  
  RCSFile(String name, String revision)
  {
    this(name, revision, null);
  }
  
  RCSFile(String name, String revision, String previousRevision)
  {
    this.name = name;
    this.revision = revision;
    if (!revision.equals(previousRevision)) {
      this.previousRevision = previousRevision;
    }
  }
  
  String getName()
  {
    return name;
  }
  
  String getRevision()
  {
    return revision;
  }
  
  String getPreviousRevision()
  {
    return previousRevision;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.cvslib.RCSFile
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */