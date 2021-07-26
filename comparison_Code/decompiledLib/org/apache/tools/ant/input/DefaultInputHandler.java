package org.apache.tools.ant.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.KeepAliveInputStream;

public class DefaultInputHandler
  implements InputHandler
{
  public void handleInput(InputRequest request)
    throws BuildException
  {
    String prompt = getPrompt(request);
    BufferedReader r = null;
    boolean success = false;
    try
    {
      r = new BufferedReader(new InputStreamReader(getInputStream()));
      do
      {
        System.err.println(prompt);
        System.err.flush();
        try
        {
          String input = r.readLine();
          if (input == null) {
            throw new BuildException("unexpected end of stream while reading input");
          }
          request.setInput(input);
        }
        catch (IOException e)
        {
          throw new BuildException("Failed to read input from Console.", e);
        }
      } while (!request.isInputValid());
      success = true; return;
    }
    finally
    {
      if (r != null) {
        try
        {
          r.close();
        }
        catch (IOException e)
        {
          if (success) {
            throw new BuildException("Failed to close input.", e);
          }
        }
      }
    }
  }
  
  protected String getPrompt(InputRequest request)
  {
    String prompt = request.getPrompt();
    String def = request.getDefaultValue();
    if ((request instanceof MultipleChoiceInputRequest))
    {
      StringBuilder sb = new StringBuilder(prompt).append(" (");
      boolean first = true;
      for (String next : ((MultipleChoiceInputRequest)request).getChoices())
      {
        if (!first) {
          sb.append(", ");
        }
        if (next.equals(def)) {
          sb.append('[');
        }
        sb.append(next);
        if (next.equals(def)) {
          sb.append(']');
        }
        first = false;
      }
      sb.append(")");
      return sb.toString();
    }
    if (def != null) {
      return prompt + " [" + def + "]";
    }
    return prompt;
  }
  
  protected InputStream getInputStream()
  {
    return KeepAliveInputStream.wrapSystemIn();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.input.DefaultInputHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */