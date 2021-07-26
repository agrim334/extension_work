package org.apache.tools.ant;

import java.util.Hashtable;

class Project$AntRefTable
  extends Hashtable<String, Object>
{
  private static final long serialVersionUID = 1L;
  
  private Object getReal(Object key)
  {
    return super.get(key);
  }
  
  public Object get(Object key)
  {
    Object o = getReal(key);
    if ((o instanceof UnknownElement))
    {
      UnknownElement ue = (UnknownElement)o;
      ue.maybeConfigure();
      o = ue.getRealThing();
    }
    return o;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.Project.AntRefTable
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */