package org.apache.tools.ant.taskdefs;

import java.io.OutputStream;

public final class XSLTProcess$TraceConfiguration
{
  private boolean elements;
  private boolean extension;
  private boolean generation;
  private boolean selection;
  private boolean templates;
  
  public XSLTProcess$TraceConfiguration(XSLTProcess this$0) {}
  
  public void setElements(boolean b)
  {
    elements = b;
  }
  
  public boolean getElements()
  {
    return elements;
  }
  
  public void setExtension(boolean b)
  {
    extension = b;
  }
  
  public boolean getExtension()
  {
    return extension;
  }
  
  public void setGeneration(boolean b)
  {
    generation = b;
  }
  
  public boolean getGeneration()
  {
    return generation;
  }
  
  public void setSelection(boolean b)
  {
    selection = b;
  }
  
  public boolean getSelection()
  {
    return selection;
  }
  
  public void setTemplates(boolean b)
  {
    templates = b;
  }
  
  public boolean getTemplates()
  {
    return templates;
  }
  
  public OutputStream getOutputStream()
  {
    return new LogOutputStream(this$0);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.XSLTProcess.TraceConfiguration
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */