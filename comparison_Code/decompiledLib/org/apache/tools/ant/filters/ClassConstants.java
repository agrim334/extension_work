package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import org.apache.tools.ant.BuildException;

public final class ClassConstants
  extends BaseFilterReader
  implements ChainableReader
{
  private String queuedData = null;
  private static final String JAVA_CLASS_HELPER = "org.apache.tools.ant.filters.util.JavaClassHelper";
  
  public ClassConstants() {}
  
  public ClassConstants(Reader in)
  {
    super(in);
  }
  
  public int read()
    throws IOException
  {
    int ch = -1;
    if ((queuedData != null) && (queuedData.isEmpty())) {
      queuedData = null;
    }
    if (queuedData == null)
    {
      String clazz = readFully();
      if ((clazz == null) || (clazz.isEmpty()))
      {
        ch = -1;
      }
      else
      {
        byte[] bytes = clazz.getBytes(StandardCharsets.ISO_8859_1);
        try
        {
          Class<?> javaClassHelper = Class.forName("org.apache.tools.ant.filters.util.JavaClassHelper");
          if (javaClassHelper != null)
          {
            Method getConstants = javaClassHelper.getMethod("getConstants", new Class[] { byte[].class });
            
            StringBuffer sb = (StringBuffer)getConstants.invoke(null, new Object[] { bytes });
            if (sb.length() > 0)
            {
              queuedData = sb.toString();
              return read();
            }
          }
        }
        catch (NoClassDefFoundError|RuntimeException ex)
        {
          throw ex;
        }
        catch (InvocationTargetException ex)
        {
          Throwable t = ex.getTargetException();
          if ((t instanceof NoClassDefFoundError)) {
            throw ((NoClassDefFoundError)t);
          }
          if ((t instanceof RuntimeException)) {
            throw ((RuntimeException)t);
          }
          throw new BuildException(t);
        }
        catch (Exception ex)
        {
          throw new BuildException(ex);
        }
      }
    }
    else
    {
      ch = queuedData.charAt(0);
      queuedData = queuedData.substring(1);
      if (queuedData.isEmpty()) {
        queuedData = null;
      }
    }
    return ch;
  }
  
  public Reader chain(Reader rdr)
  {
    return new ClassConstants(rdr);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.ClassConstants
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */