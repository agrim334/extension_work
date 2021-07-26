package org.apache.tools.ant.taskdefs.modules;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import org.apache.tools.ant.BuildException;

public class Link$ReleaseInfoEntry
{
  private String key;
  private String value;
  private File file;
  private String charset = StandardCharsets.ISO_8859_1.name();
  
  public Link$ReleaseInfoEntry(Link this$0) {}
  
  public Link$ReleaseInfoEntry(Link this$0, String key, String value)
  {
    setKey(key);
    setValue(value);
  }
  
  public String getKey()
  {
    return key;
  }
  
  public void setKey(String key)
  {
    this.key = key;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public File getFile()
  {
    return file;
  }
  
  public void setFile(File file)
  {
    this.file = file;
  }
  
  public String getCharset()
  {
    return charset;
  }
  
  public void setCharset(String charset)
  {
    this.charset = charset;
  }
  
  public void validate()
  {
    if ((file == null) && ((key == null) || (value == null))) {
      throw new BuildException("Release info must define 'key' and 'value' attributes, or a 'file' attribute.", this$0.getLocation());
    }
    if ((file != null) && ((key != null) || (value != null))) {
      throw new BuildException("Release info cannot define both a file attribute and key/value attributes.", this$0.getLocation());
    }
    if (charset == null) {
      throw new BuildException("Charset cannot be null.", this$0.getLocation());
    }
    try
    {
      Charset.forName(charset);
    }
    catch (IllegalArgumentException e)
    {
      throw new BuildException(e, this$0.getLocation());
    }
  }
  
  public Properties toProperties()
  {
    Properties props = new Properties();
    if (file != null)
    {
      try
      {
        Reader reader = Files.newBufferedReader(file
          .toPath(), Charset.forName(charset));
        try
        {
          props.load(reader);
          if (reader == null) {
            break label71;
          }
          reader.close();
        }
        catch (Throwable localThrowable)
        {
          if (reader == null) {
            break label69;
          }
        }
        try
        {
          reader.close();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable.addSuppressed(localThrowable1);
        }
        label69:
        throw localThrowable;
      }
      catch (IOException e)
      {
        label71:
        throw new BuildException("Cannot read release info file \"" + file + "\": " + e, e, this$0.getLocation());
      }
      return props;
    }
    props.setProperty(key, value);
    
    return props;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.modules.Link.ReleaseInfoEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */