package org.apache.tools.ant.taskdefs.optional.depend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class DirectoryIterator
  implements ClassFileIterator
{
  private Deque<Iterator<File>> enumStack;
  private Iterator<File> currentIterator;
  
  public DirectoryIterator(File rootDirectory, boolean changeInto)
    throws IOException
  {
    enumStack = new ArrayDeque();
    currentIterator = getDirectoryEntries(rootDirectory).iterator();
  }
  
  private List<File> getDirectoryEntries(File directory)
  {
    File[] filesInDir = directory.listFiles();
    if (filesInDir == null) {
      return Collections.emptyList();
    }
    return Arrays.asList(filesInDir);
  }
  
  public ClassFile getNextClassFile()
  {
    ClassFile nextElement = null;
    try
    {
      while (nextElement == null) {
        if (currentIterator.hasNext())
        {
          File element = (File)currentIterator.next();
          if (element.isDirectory())
          {
            enumStack.push(currentIterator);
            
            List<File> files = getDirectoryEntries(element);
            
            currentIterator = files.iterator();
          }
          else
          {
            InputStream inFileStream = Files.newInputStream(element.toPath(), new OpenOption[0]);
            try
            {
              if (element.getName().endsWith(".class"))
              {
                ClassFile javaClass = new ClassFile();
                
                javaClass.read(inFileStream);
                
                nextElement = javaClass;
              }
              if (inFileStream == null) {
                break label148;
              }
              inFileStream.close();
            }
            catch (Throwable localThrowable)
            {
              if (inFileStream == null) {
                break label145;
              }
            }
            try
            {
              inFileStream.close();
            }
            catch (Throwable localThrowable1)
            {
              localThrowable.addSuppressed(localThrowable1);
            }
            label145:
            throw localThrowable;
          }
        }
        else
        {
          label148:
          if (enumStack.isEmpty()) {
            break;
          }
          currentIterator = ((Iterator)enumStack.pop());
        }
      }
    }
    catch (IOException e)
    {
      nextElement = null;
    }
    return nextElement;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.DirectoryIterator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */