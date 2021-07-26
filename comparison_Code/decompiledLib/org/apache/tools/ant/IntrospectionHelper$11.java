package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.util.StringUtils;

class IntrospectionHelper$11
  extends IntrospectionHelper.AttributeSetter
{
  IntrospectionHelper$11(IntrospectionHelper this$0, Method m, Class type, Method paramMethod1, String paramString)
  {
    super(m, type);
  }
  
  public void set(Project p, Object parent, String value)
    throws InvocationTargetException, IllegalAccessException, BuildException
  {
    try
    {
      val$m.invoke(parent, new Object[] { Long.valueOf(StringUtils.parseHumanSizes(value)) });
    }
    catch (NumberFormatException e)
    {
      throw new BuildException("Can't assign non-numeric value '" + value + "' to attribute " + val$attrName);
    }
    catch (InvocationTargetException|IllegalAccessException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new BuildException(e);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.11
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */