package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;

public class SQLExec$Transaction
{
  private Resource tSrcResource = null;
  private String tSqlCommand = "";
  
  public SQLExec$Transaction(SQLExec this$0) {}
  
  public void setSrc(File src)
  {
    if (src != null) {
      setSrcResource(new FileResource(src));
    }
  }
  
  public void setSrcResource(Resource src)
  {
    if (tSrcResource != null) {
      throw new BuildException("only one resource per transaction");
    }
    tSrcResource = src;
  }
  
  public void addText(String sql)
  {
    if (sql != null) {
      tSqlCommand += sql;
    }
  }
  
  public void addConfigured(ResourceCollection a)
  {
    if (a.size() != 1) {
      throw new BuildException("only single argument resource collections are supported.");
    }
    setSrcResource((Resource)a.iterator().next());
  }
  
  private void runTransaction(PrintStream out)
    throws IOException, SQLException
  {
    if (!tSqlCommand.isEmpty())
    {
      this$0.log("Executing commands", 2);
      this$0.runStatements(new StringReader(tSqlCommand), out);
    }
    if (tSrcResource != null)
    {
      this$0.log("Executing resource: " + tSrcResource.toString(), 2);
      
      Charset charset = SQLExec.access$100(this$0) == null ? Charset.defaultCharset() : Charset.forName(SQLExec.access$100(this$0));
      
      Reader reader = new InputStreamReader(tSrcResource.getInputStream(), charset);
      try
      {
        this$0.runStatements(reader, out);
        reader.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        reader.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.SQLExec.Transaction
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */