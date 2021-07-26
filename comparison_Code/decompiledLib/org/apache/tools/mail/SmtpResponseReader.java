package org.apache.tools.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SmtpResponseReader
{
  protected BufferedReader reader = null;
  
  public SmtpResponseReader(InputStream in)
  {
    reader = new BufferedReader(new InputStreamReader(in));
  }
  
  public String getResponse()
    throws IOException
  {
    StringBuilder result = new StringBuilder();
    String line = reader.readLine();
    if ((line != null) && (line.length() >= 3))
    {
      result.append(line, 0, 3);
      result.append(" ");
    }
    while (line != null)
    {
      appendTo(result, line);
      if (!hasMoreLines(line)) {
        break;
      }
      line = reader.readLine();
    }
    return result.toString().trim();
  }
  
  public void close()
    throws IOException
  {
    reader.close();
  }
  
  protected boolean hasMoreLines(String line)
  {
    return (line.length() > 3) && (line.charAt(3) == '-');
  }
  
  private static void appendTo(StringBuilder target, String line)
  {
    if (line.length() > 4) {
      target.append(line.substring(4)).append(' ');
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.mail.SmtpResponseReader
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */