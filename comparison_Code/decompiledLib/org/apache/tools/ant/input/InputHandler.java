package org.apache.tools.ant.input;

import org.apache.tools.ant.BuildException;

public abstract interface InputHandler
{
  public abstract void handleInput(InputRequest paramInputRequest)
    throws BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.input.InputHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */