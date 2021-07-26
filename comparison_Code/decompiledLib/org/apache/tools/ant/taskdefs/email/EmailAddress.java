package org.apache.tools.ant.taskdefs.email;

public class EmailAddress
{
  private String name;
  private String address;
  
  public EmailAddress() {}
  
  public EmailAddress(String email)
  {
    int minLen = 9;
    int len = email.length();
    if ((len > 9) && 
      ((email.charAt(0) == '<') || (email.charAt(1) == '<')) && (
      (email.charAt(len - 1) == '>') || (email.charAt(len - 2) == '>')))
    {
      address = trim(email, true);
      return;
    }
    int paramDepth = 0;
    int start = 0;
    int end = 0;
    int nStart = 0;
    int nEnd = 0;
    for (int i = 0; i < len; i++)
    {
      char c = email.charAt(i);
      if (c == '(')
      {
        paramDepth++;
        if (start == 0)
        {
          end = i;
          nStart = i + 1;
        }
      }
      else if (c == ')')
      {
        paramDepth--;
        if (end == 0)
        {
          start = i + 1;
          nEnd = i;
        }
      }
      else if ((paramDepth == 0) && (c == '<'))
      {
        if (start == 0) {
          nEnd = i;
        }
        start = i + 1;
      }
      else if ((paramDepth == 0) && (c == '>'))
      {
        end = i;
        if (end != len - 1) {
          nStart = i + 1;
        }
      }
    }
    if (end == 0) {
      end = len;
    }
    if (nEnd == 0) {
      nEnd = len;
    }
    address = trim(email.substring(start, end), true);
    name = trim(email.substring(nStart, nEnd), false);
    if (name.length() + address.length() > len) {
      name = null;
    }
  }
  
  private String trim(String t, boolean trimAngleBrackets)
  {
    int start = 0;
    int end = t.length();
    boolean trim;
    do
    {
      trim = false;
      if ((t.charAt(end - 1) == ')') || 
        ((t.charAt(end - 1) == '>') && (trimAngleBrackets)) || 
        ((t.charAt(end - 1) == '"') && (t.charAt(end - 2) != '\\')) || 
        (t.charAt(end - 1) <= ' '))
      {
        trim = true;
        end--;
      }
      if ((t.charAt(start) == '(') || 
        ((t.charAt(start) == '<') && (trimAngleBrackets)) || 
        (t.charAt(start) == '"') || 
        (t.charAt(start) <= ' '))
      {
        trim = true;
        start++;
      }
    } while (trim);
    return t.substring(start, end);
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setAddress(String address)
  {
    this.address = address;
  }
  
  public String toString()
  {
    if (name == null) {
      return address;
    }
    return name + " <" + address + ">";
  }
  
  public String getAddress()
  {
    return address;
  }
  
  public String getName()
  {
    return name;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.email.EmailAddress
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */