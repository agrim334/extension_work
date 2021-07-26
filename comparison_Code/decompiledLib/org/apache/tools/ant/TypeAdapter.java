package org.apache.tools.ant;

public abstract interface TypeAdapter
{
  public abstract void setProject(Project paramProject);
  
  public abstract Project getProject();
  
  public abstract void setProxy(Object paramObject);
  
  public abstract Object getProxy();
  
  public abstract void checkProxyClass(Class<?> paramClass);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.TypeAdapter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */