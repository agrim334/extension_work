package org.apache.tools.ant.taskdefs.cvslib;

import java.util.Date;
import java.util.Vector;

public class CVSEntry
{
  private Date date;
  private String author;
  private final String comment;
  private final Vector<RCSFile> files = new Vector();
  
  public CVSEntry(Date date, String author, String comment)
  {
    this.date = date;
    this.author = author;
    this.comment = comment;
  }
  
  public void addFile(String file, String revision)
  {
    files.add(new RCSFile(file, revision));
  }
  
  public void addFile(String file, String revision, String previousRevision)
  {
    files.add(new RCSFile(file, revision, previousRevision));
  }
  
  public Date getDate()
  {
    return date;
  }
  
  public void setAuthor(String author)
  {
    this.author = author;
  }
  
  public String getAuthor()
  {
    return author;
  }
  
  public String getComment()
  {
    return comment;
  }
  
  public Vector<RCSFile> getFiles()
  {
    return files;
  }
  
  public String toString()
  {
    return 
      getAuthor() + "\n" + getDate() + "\n" + getFiles() + "\n" + getComment();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.cvslib.CVSEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */