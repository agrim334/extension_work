package org.apache.tools.ant.input;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Vector;

public class MultipleChoiceInputRequest
  extends InputRequest
{
  private final LinkedHashSet<String> choices;
  
  @Deprecated
  public MultipleChoiceInputRequest(String prompt, Vector<String> choices)
  {
    this(prompt, choices);
  }
  
  public MultipleChoiceInputRequest(String prompt, Collection<String> choices)
  {
    super(prompt);
    if (choices == null) {
      throw new IllegalArgumentException("choices must not be null");
    }
    this.choices = new LinkedHashSet(choices);
  }
  
  public Vector<String> getChoices()
  {
    return new Vector(choices);
  }
  
  public boolean isInputValid()
  {
    return (choices.contains(getInput())) || (
      (getInput().isEmpty()) && (getDefaultValue() != null));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.input.MultipleChoiceInputRequest
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */