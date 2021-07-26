package org.apache.tools.ant.taskdefs.cvslib;

public class CvsTagEntry
{
  private String filename;
  private String prevRevision;
  private String revision;
  
  public CvsTagEntry(String filename)
  {
    this(filename, null, null);
  }
  
  public CvsTagEntry(String filename, String revision)
  {
    this(filename, revision, null);
  }
  
  public CvsTagEntry(String filename, String revision, String prevRevision)
  {
    this.filename = filename;
    this.revision = revision;
    this.prevRevision = prevRevision;
  }
  
  public String getFile()
  {
    return filename;
  }
  
  public String getRevision()
  {
    return revision;
  }
  
  public String getPreviousRevision()
  {
    return prevRevision;
  }
  
  public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    buffer.append(filename);
    if (revision == null)
    {
      buffer.append(" was removed");
      if (prevRevision != null) {
        buffer.append("; previous revision was ").append(prevRevision);
      }
    }
    else if (prevRevision == null)
    {
      buffer.append(" is new; current revision is ").append(revision);
    }
    else
    {
      buffer.append(" has changed from ").append(prevRevision).append(" to ").append(revision);
    }
    return buffer.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.cvslib.CvsTagEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */