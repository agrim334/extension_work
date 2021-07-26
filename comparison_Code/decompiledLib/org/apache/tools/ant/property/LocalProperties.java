package org.apache.tools.ant.property;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.PropertyHelper.PropertyEvaluator;
import org.apache.tools.ant.PropertyHelper.PropertySetter;

public class LocalProperties
  extends InheritableThreadLocal<LocalPropertyStack>
  implements PropertyHelper.PropertyEvaluator, PropertyHelper.PropertySetter
{
  public static synchronized LocalProperties get(Project project)
  {
    LocalProperties l = (LocalProperties)project.getReference("ant.LocalProperties");
    if (l == null)
    {
      l = new LocalProperties();
      project.addReference("ant.LocalProperties", l);
      PropertyHelper.getPropertyHelper(project).add(l);
    }
    return l;
  }
  
  protected synchronized LocalPropertyStack initialValue()
  {
    return new LocalPropertyStack();
  }
  
  public void addLocal(String property)
  {
    ((LocalPropertyStack)get()).addLocal(property);
  }
  
  public void enterScope()
  {
    ((LocalPropertyStack)get()).enterScope();
  }
  
  public void exitScope()
  {
    ((LocalPropertyStack)get()).exitScope();
  }
  
  public void copy()
  {
    set(((LocalPropertyStack)get()).copy());
  }
  
  public Object evaluate(String property, PropertyHelper helper)
  {
    return ((LocalPropertyStack)get()).evaluate(property, helper);
  }
  
  public boolean setNew(String property, Object value, PropertyHelper propertyHelper)
  {
    return ((LocalPropertyStack)get()).setNew(property, value, propertyHelper);
  }
  
  public boolean set(String property, Object value, PropertyHelper propertyHelper)
  {
    return ((LocalPropertyStack)get()).set(property, value, propertyHelper);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.property.LocalProperties
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */