package org.apache.tools.ant.input;

public class InputRequest
{
  private final String prompt;
  private String input;
  private String defaultValue;
  
  public InputRequest(String prompt)
  {
    if (prompt == null) {
      throw new IllegalArgumentException("prompt must not be null");
    }
    this.prompt = prompt;
  }
  
  public String getPrompt()
  {
    return prompt;
  }
  
  public void setInput(String input)
  {
    this.input = input;
  }
  
  public boolean isInputValid()
  {
    return true;
  }
  
  public String getInput()
  {
    return input;
  }
  
  public String getDefaultValue()
  {
    return defaultValue;
  }
  
  public void setDefaultValue(String d)
  {
    defaultValue = d;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.input.InputRequest
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */