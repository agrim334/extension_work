package org.apache.tools.ant.taskdefs.optional.depend;

import java.util.Iterator;
import java.util.NoSuchElementException;

class ClassFileIterator$1
  implements Iterator<ClassFile>
{
  ClassFile next;
  
  ClassFileIterator$1(ClassFileIterator this$0)
  {
    next = this.this$0.getNextClassFile();
  }
  
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
      next = this$0.getNextClassFile();
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.ClassFileIterator.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */