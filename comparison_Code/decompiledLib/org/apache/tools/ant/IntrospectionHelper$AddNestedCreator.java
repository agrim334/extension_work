package org.apache.tools.ant;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.taskdefs.PreSetDef.PreSetDefinition;

class IntrospectionHelper$AddNestedCreator
  extends IntrospectionHelper.NestedCreator
{
  static final int ADD = 1;
  static final int ADD_CONFIGURED = 2;
  private final Constructor<?> constructor;
  private final int behavior;
  
  IntrospectionHelper$AddNestedCreator(Method m, Constructor<?> c, int behavior)
  {
    super(m);
    constructor = c;
    this.behavior = behavior;
  }
  
  boolean isPolyMorphic()
  {
    return true;
  }
  
  Object create(Project project, Object parent, Object child)
    throws InvocationTargetException, IllegalAccessException, InstantiationException
  {
    if (child == null) {
      child = constructor.newInstance(
      
        new Object[] {constructor.getParameterTypes().length == 0 ? new Object[0] : project });
    }
    if ((child instanceof PreSetDef.PreSetDefinition)) {
      child = ((PreSetDef.PreSetDefinition)child).createObject(project);
    }
    if (behavior == 1) {
      istore(parent, child);
    }
    return child;
  }
  
  void store(Object parent, Object child)
    throws InvocationTargetException, IllegalAccessException, InstantiationException
  {
    if (behavior == 2) {
      istore(parent, child);
    }
  }
  
  private void istore(Object parent, Object child)
    throws InvocationTargetException, IllegalAccessException
  {
    getMethod().invoke(parent, new Object[] { child });
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.AddNestedCreator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */