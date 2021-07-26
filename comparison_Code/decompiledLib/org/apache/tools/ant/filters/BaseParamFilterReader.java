package org.apache.tools.ant.filters;

import java.io.Reader;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Parameterizable;

public abstract class BaseParamFilterReader
  extends BaseFilterReader
  implements Parameterizable
{
  private Parameter[] parameters;
  
  public BaseParamFilterReader() {}
  
  public BaseParamFilterReader(Reader in)
  {
    super(in);
  }
  
  public final void setParameters(Parameter... parameters)
  {
    this.parameters = parameters;
    setInitialized(false);
  }
  
  protected final Parameter[] getParameters()
  {
    return parameters;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.BaseParamFilterReader
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */