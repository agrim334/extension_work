package org.apache.tools.ant.input;

import java.io.Console;
import java.util.Arrays;
import org.apache.tools.ant.BuildException;

public class SecureInputHandler
  extends DefaultInputHandler
{
  public void handleInput(InputRequest request)
    throws BuildException
  {
    String prompt = getPrompt(request);
    do
    {
      char[] input = System.console().readPassword(prompt, new Object[0]);
      if (input == null) {
        throw new BuildException("unexpected end of stream while reading input");
      }
      request.setInput(new String(input));
      Arrays.fill(input, ' ');
    } while (!request.isInputValid());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.input.SecureInputHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */