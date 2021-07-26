package org.apache.tools.ant.taskdefs;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class Javadoc$DocletInfo
  extends Javadoc.ExtensionInfo
{
  private final List<Javadoc.DocletParam> params = new Vector();
  
  public Javadoc$DocletInfo(Javadoc this$0) {}
  
  public Javadoc.DocletParam createParam()
  {
    Javadoc.DocletParam param = new Javadoc.DocletParam(this$0);
    params.add(param);
    return param;
  }
  
  public Enumeration<Javadoc.DocletParam> getParams()
  {
    return Collections.enumeration(params);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Javadoc.DocletInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */