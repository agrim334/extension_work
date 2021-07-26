package org.apache.tools.ant.taskdefs.email;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;

public class Message
  extends ProjectComponent
{
  private File messageSource = null;
  private StringBuffer buffer = new StringBuffer();
  private String mimeType = "text/plain";
  private boolean specified = false;
  private String charset = null;
  private String inputEncoding;
  
  public Message() {}
  
  public Message(String text)
  {
    addText(text);
  }
  
  public Message(File file)
  {
    messageSource = file;
  }
  
  public void addText(String text)
  {
    buffer.append(text);
  }
  
  public void setSrc(File src)
  {
    messageSource = src;
  }
  
  public void setMimeType(String mimeType)
  {
    this.mimeType = mimeType;
    specified = true;
  }
  
  public String getMimeType()
  {
    return mimeType;
  }
  
  public void print(PrintStream ps)
    throws IOException
  {
    BufferedWriter out = null;
    
    out = charset == null ? new BufferedWriter(new OutputStreamWriter(ps)) : new BufferedWriter(new OutputStreamWriter(ps, charset));
    if (messageSource != null)
    {
      BufferedReader in = new BufferedReader(getReader(messageSource));
      try
      {
        String line;
        while ((line = in.readLine()) != null)
        {
          out.write(getProject().replaceProperties(line));
          out.newLine();
        }
        in.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        in.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    else
    {
      out.write(getProject().replaceProperties(buffer.substring(0)));
      out.newLine();
    }
    out.flush();
  }
  
  public boolean isMimeTypeSpecified()
  {
    return specified;
  }
  
  public void setCharset(String charset)
  {
    this.charset = charset;
  }
  
  public String getCharset()
  {
    return charset;
  }
  
  public void setInputEncoding(String encoding)
  {
    inputEncoding = encoding;
  }
  
  private Reader getReader(File f)
    throws IOException
  {
    if (inputEncoding != null)
    {
      InputStream fis = Files.newInputStream(f.toPath(), new OpenOption[0]);
      try
      {
        return new InputStreamReader(fis, inputEncoding);
      }
      catch (IOException ex)
      {
        fis.close();
        throw ex;
      }
    }
    return new FileReader(f);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.email.Message
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */