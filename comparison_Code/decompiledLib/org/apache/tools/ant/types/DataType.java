package org.apache.tools.ant.types;

import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.IdentityStack;

public abstract class DataType
  extends ProjectComponent
  implements Cloneable
{
  @Deprecated
  protected Reference ref;
  @Deprecated
  protected boolean checked = true;
  
  public boolean isReference()
  {
    return ref != null;
  }
  
  public void setRefid(Reference ref)
  {
    this.ref = ref;
    checked = false;
  }
  
  protected String getDataTypeName()
  {
    return ComponentHelper.getElementName(getProject(), this, true);
  }
  
  protected void dieOnCircularReference()
  {
    dieOnCircularReference(getProject());
  }
  
  protected void dieOnCircularReference(Project p)
  {
    if ((checked) || (!isReference())) {
      return;
    }
    dieOnCircularReference(new IdentityStack(this), p);
  }
  
  protected void dieOnCircularReference(Stack<Object> stack, Project project)
    throws BuildException
  {
    if ((checked) || (!isReference())) {
      return;
    }
    Object o = ref.getReferencedObject(project);
    if ((o instanceof DataType))
    {
      IdentityStack<Object> id = IdentityStack.getInstance(stack);
      if (id.contains(o)) {
        throw circularReference();
      }
      id.push(o);
      ((DataType)o).dieOnCircularReference(id, project);
      id.pop();
    }
    checked = true;
  }
  
  public static void invokeCircularReferenceCheck(DataType dt, Stack<Object> stk, Project p)
  {
    dt.dieOnCircularReference(stk, p);
  }
  
  public static void pushAndInvokeCircularReferenceCheck(DataType dt, Stack<Object> stk, Project p)
  {
    stk.push(dt);
    dt.dieOnCircularReference(stk, p);
    stk.pop();
  }
  
  @Deprecated
  protected <T> T getCheckedRef()
  {
    return (T)getCheckedRef(getProject());
  }
  
  protected <T> T getCheckedRef(Class<T> requiredClass)
  {
    return (T)getCheckedRef(requiredClass, getDataTypeName(), getProject());
  }
  
  @Deprecated
  protected <T> T getCheckedRef(Project p)
  {
    return (T)getCheckedRef(getClass(), getDataTypeName(), p);
  }
  
  protected <T> T getCheckedRef(Class<T> requiredClass, String dataTypeName)
  {
    return (T)getCheckedRef(requiredClass, dataTypeName, getProject());
  }
  
  protected <T> T getCheckedRef(Class<T> requiredClass, String dataTypeName, Project project)
  {
    if (project == null) {
      throw new BuildException("No Project specified");
    }
    dieOnCircularReference(project);
    T o = ref.getReferencedObject(project);
    if (requiredClass.isAssignableFrom(o.getClass())) {
      return o;
    }
    log("Class " + displayName(o.getClass()) + " is not a subclass of " + 
    
      displayName(requiredClass), 3);
    
    throw new BuildException(ref.getRefId() + " doesn't denote a " + dataTypeName);
  }
  
  protected BuildException tooManyAttributes()
  {
    return new BuildException("You must not specify more than one attribute when using refid");
  }
  
  protected BuildException noChildrenAllowed()
  {
    return new BuildException("You must not specify nested elements when using refid");
  }
  
  protected BuildException circularReference()
  {
    return new BuildException("This data type contains a circular reference.");
  }
  
  protected boolean isChecked()
  {
    return checked;
  }
  
  protected void setChecked(boolean checked)
  {
    this.checked = checked;
  }
  
  public Reference getRefid()
  {
    return ref;
  }
  
  protected void checkAttributesAllowed()
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
  }
  
  protected void checkChildrenAllowed()
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
  }
  
  public String toString()
  {
    String d = getDescription();
    return getDataTypeName() + " " + d;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    DataType dt = (DataType)super.clone();
    dt.setDescription(getDescription());
    if (getRefid() != null) {
      dt.setRefid(getRefid());
    }
    dt.setChecked(isChecked());
    return dt;
  }
  
  private String displayName(Class<?> clazz)
  {
    return clazz.getName() + " (loaded via " + clazz.getClassLoader() + ")";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.DataType
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */