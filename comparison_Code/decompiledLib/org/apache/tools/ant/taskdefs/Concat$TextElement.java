package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.FileUtils;

public class Concat$TextElement
  extends ProjectComponent
{
  private String value = "";
  private boolean trimLeading = false;
  private boolean trim = false;
  private boolean filtering = true;
  private String encoding = null;
  
  public void setFiltering(boolean filtering)
  {
    this.filtering = filtering;
  }
  
  private boolean getFiltering()
  {
    return filtering;
  }
  
  public void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  public void setFile(File file)
    throws BuildException
  {
    if (!file.exists()) {
      throw new BuildException("File %s does not exist.", new Object[] { file });
    }
    BufferedReader reader = null;
    try
    {
      if (encoding == null) {
        reader = new BufferedReader(new FileReader(file));
      } else {
        reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath(), new OpenOption[0]), encoding));
      }
      value = FileUtils.safeReadFully(reader);
    }
    catch (IOException ex)
    {
      throw new BuildException(ex);
    }
    finally
    {
      FileUtils.close(reader);
    }
  }
  
  public void addText(String value)
  {
    this.value += getProject().replaceProperties(value);
  }
  
  public void setTrimLeading(boolean strip)
  {
    trimLeading = strip;
  }
  
  public void setTrim(boolean trim)
  {
    this.trim = trim;
  }
  
  public String getValue()
  {
    if (value == null) {
      value = "";
    }
    if (value.trim().isEmpty()) {
      value = "";
    }
    if (trimLeading)
    {
      StringBuilder b = new StringBuilder();
      boolean startOfLine = true;
      for (char ch : value.toCharArray()) {
        if (startOfLine)
        {
          if ((ch != ' ') && (ch != '\t')) {
            startOfLine = false;
          }
        }
        else
        {
          b.append(ch);
          if ((ch == '\n') || (ch == '\r')) {
            startOfLine = true;
          }
        }
      }
      value = b.toString();
    }
    if (trim) {
      value = value.trim();
    }
    return value;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Concat.TextElement
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */