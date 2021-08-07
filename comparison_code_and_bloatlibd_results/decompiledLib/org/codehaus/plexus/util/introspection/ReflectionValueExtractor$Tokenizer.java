package org.codehaus.plexus.util.introspection;

class ReflectionValueExtractor$Tokenizer
{
  final String expression;
  int idx;
  
  public ReflectionValueExtractor$Tokenizer(String expression)
  {
    this.expression = expression;
  }
  
  public int peekChar()
  {
    return idx < expression.length() ? expression.charAt(idx) : -1;
  }
  
  public int skipChar()
  {
    return idx < expression.length() ? expression.charAt(idx++) : -1;
  }
  
  public String nextToken(char delimiter)
  {
    int start = idx;
    while ((idx < expression.length()) && (delimiter != expression.charAt(idx))) {
      idx += 1;
    }
    if ((idx <= start) || (idx >= expression.length())) {
      return null;
    }
    return expression.substring(start, idx++);
  }
  
  public String nextPropertyName()
  {
    int start = idx;
    while ((idx < expression.length()) && (Character.isJavaIdentifierPart(expression.charAt(idx)))) {
      idx += 1;
    }
    if ((idx <= start) || (idx > expression.length())) {
      return null;
    }
    return expression.substring(start, idx);
  }
  
  public int getPosition()
  {
    return idx < expression.length() ? idx : -1;
  }
  
  public String toString()
  {
    return idx < expression.length() ? expression.substring(idx) : "<EOF>";
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.introspection.ReflectionValueExtractor.Tokenizer
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */