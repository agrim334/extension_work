package org.apache.tools.ant.taskdefs.optional.depend;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract interface ClassFileIterator
  extends Iterable<ClassFile>
{
  public abstract ClassFile getNextClassFile();
  
  public Iterator<ClassFile> iterator()
  {
    new Iterator()
    {
      ClassFile next;
      
      public boolean hasNext()
      {
        return next != null;
      }
      
      public ClassFile next()
      {
        if (next == null) {
          throw new NoSuchElementException();
        }
        try
        {
          return next;
        }
        finally
        {
          next = getNextClassFile();
        }
      }
    };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.ClassFileIterator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */