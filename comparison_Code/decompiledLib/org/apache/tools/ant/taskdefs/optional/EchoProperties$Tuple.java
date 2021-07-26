package org.apache.tools.ant.taskdefs.optional;

import java.util.Comparator;
import java.util.Objects;

final class EchoProperties$Tuple
  implements Comparable<Tuple>
{
  private String key;
  private String value;
  
  private EchoProperties$Tuple(String key, String value)
  {
    this.key = key;
    this.value = value;
  }
  
  public int compareTo(Tuple o)
  {
    return Comparator.naturalOrder().compare(key, key);
  }
  
  public boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    if ((o == null) || (o.getClass() != getClass())) {
      return false;
    }
    Tuple that = (Tuple)o;
    return (Objects.equals(key, key)) && 
      (Objects.equals(value, value));
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { key });
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.EchoProperties.Tuple
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */