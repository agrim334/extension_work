package org.apache.tools.ant.taskdefs.optional.depend;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.tools.ant.BuildException;

public class JarFileIterator
  implements ClassFileIterator
{
  private ZipInputStream jarStream;
  
  public JarFileIterator(InputStream stream)
    throws IOException
  {
    jarStream = new ZipInputStream(stream);
  }
  
  public ClassFile getNextClassFile()
  {
    ClassFile nextElement = null;
    try
    {
      ZipEntry jarEntry = jarStream.getNextEntry();
      while ((nextElement == null) && (jarEntry != null))
      {
        String entryName = jarEntry.getName();
        if ((!jarEntry.isDirectory()) && (entryName.endsWith(".class")))
        {
          ClassFile javaClass = new ClassFile();
          
          javaClass.read(jarStream);
          
          nextElement = javaClass;
        }
        else
        {
          jarEntry = jarStream.getNextEntry();
        }
      }
    }
    catch (IOException e)
    {
      String message = e.getMessage();
      String text = e.getClass().getName();
      if (message != null) {
        text = text + ": " + message;
      }
      throw new BuildException("Problem reading JAR file: " + text);
    }
    ZipEntry jarEntry;
    return nextElement;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.JarFileIterator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */