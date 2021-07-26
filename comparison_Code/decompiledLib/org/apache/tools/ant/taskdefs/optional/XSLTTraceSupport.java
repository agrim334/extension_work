package org.apache.tools.ant.taskdefs.optional;

import javax.xml.transform.Transformer;
import org.apache.tools.ant.taskdefs.XSLTProcess.TraceConfiguration;

public abstract interface XSLTTraceSupport
{
  public abstract void configureTrace(Transformer paramTransformer, XSLTProcess.TraceConfiguration paramTraceConfiguration);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.XSLTTraceSupport
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */