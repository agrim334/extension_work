package org.apache.tools.ant;

public abstract interface DynamicElement
{
  public abstract Object createDynamicElement(String paramString)
    throws BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.DynamicElement
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */