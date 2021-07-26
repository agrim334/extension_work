package org.apache.tools.ant.taskdefs.cvslib;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ChangeLogWriter
{
  private final SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd");
  private SimpleDateFormat outputTime = new SimpleDateFormat("HH:mm");
  private static final DOMElementWriter DOM_WRITER = new DOMElementWriter();
  
  public ChangeLogWriter()
  {
    TimeZone utc = TimeZone.getTimeZone("UTC");
    outputDate.setTimeZone(utc);
    outputTime.setTimeZone(utc);
  }
  
  public void printChangeLog(PrintWriter output, CVSEntry[] entries)
  {
    try
    {
      output.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      Document doc = DOMUtils.newDocument();
      Element root = doc.createElement("changelog");
      DOM_WRITER.openElement(root, output, 0, "\t");
      output.println();
      for (CVSEntry entry : entries) {
        printEntry(doc, output, entry);
      }
      DOM_WRITER.closeElement(root, output, 0, "\t", true);
      output.flush();
      output.close();
    }
    catch (IOException e)
    {
      throw new BuildException(e);
    }
  }
  
  private void printEntry(Document doc, PrintWriter output, CVSEntry entry)
    throws IOException
  {
    Element ent = doc.createElement("entry");
    DOMUtils.appendTextElement(ent, "date", outputDate
      .format(entry.getDate()));
    DOMUtils.appendTextElement(ent, "time", outputTime
      .format(entry.getDate()));
    DOMUtils.appendCDATAElement(ent, "author", entry.getAuthor());
    for (RCSFile file : entry.getFiles())
    {
      Element f = DOMUtils.createChildElement(ent, "file");
      DOMUtils.appendCDATAElement(f, "name", file.getName());
      DOMUtils.appendTextElement(f, "revision", file.getRevision());
      
      String previousRevision = file.getPreviousRevision();
      if (previousRevision != null) {
        DOMUtils.appendTextElement(f, "prevrevision", previousRevision);
      }
    }
    DOMUtils.appendCDATAElement(ent, "msg", entry.getComment());
    DOM_WRITER.write(ent, output, 1, "\t");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.cvslib.ChangeLogWriter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */