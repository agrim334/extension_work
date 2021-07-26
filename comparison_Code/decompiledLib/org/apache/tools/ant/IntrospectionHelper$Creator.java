package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;

public final class IntrospectionHelper$Creator
{
  private final IntrospectionHelper.NestedCreator nestedCreator;
  private final Object parent;
  private final Project project;
  private Object nestedObject;
  private String polyType;
  
  private IntrospectionHelper$Creator(Project project, Object parent, IntrospectionHelper.NestedCreator nestedCreator)
  {
    this.project = project;
    this.parent = parent;
    this.nestedCreator = nestedCreator;
  }
  
  public void setPolyType(String polyType)
  {
    this.polyType = polyType;
  }
  
  public Object create()
  {
    if (polyType != null)
    {
      if (!nestedCreator.isPolyMorphic()) {
        throw new BuildException("Not allowed to use the polymorphic form for this element");
      }
      ComponentHelper helper = ComponentHelper.getComponentHelper(project);
      nestedObject = helper.createComponent(polyType);
      if (nestedObject == null) {
        throw new BuildException("Unable to create object of type " + polyType);
      }
    }
    try
    {
      nestedObject = nestedCreator.create(project, parent, nestedObject);
      if (project != null) {
        project.setProjectReference(nestedObject);
      }
      return nestedObject;
    }
    catch (IllegalAccessException|InstantiationException ex)
    {
      throw new BuildException(ex);
    }
    catch (IllegalArgumentException ex)
    {
      if (polyType == null) {
        throw ex;
      }
      throw new BuildException("Invalid type used " + polyType);
    }
    catch (InvocationTargetException ex)
    {
      throw IntrospectionHelper.access$400(ex);
    }
  }
  
  public Object getRealObject()
  {
    return nestedCreator.getRealObject();
  }
  
  public void store()
  {
    try
    {
      nestedCreator.store(parent, nestedObject);
    }
    catch (IllegalAccessException|InstantiationException ex)
    {
      throw new BuildException(ex);
    }
    catch (IllegalArgumentException ex)
    {
      if (polyType == null) {
        throw ex;
      }
      throw new BuildException("Invalid type used " + polyType);
    }
    catch (InvocationTargetException ex)
    {
      throw IntrospectionHelper.access$400(ex);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.Creator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */