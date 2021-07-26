package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.input.GreedyInputHandler;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.PropertyFileInputHandler;
import org.apache.tools.ant.input.SecureInputHandler;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class Input$HandlerType
  extends EnumeratedAttribute
{
  private static final String[] VALUES = { "default", "propertyfile", "greedy", "secure" };
  private static final InputHandler[] HANDLERS = { new DefaultInputHandler(), new PropertyFileInputHandler(), new GreedyInputHandler(), new SecureInputHandler() };
  
  public String[] getValues()
  {
    return VALUES;
  }
  
  private InputHandler getInputHandler()
  {
    return HANDLERS[getIndex()];
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Input.HandlerType
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */