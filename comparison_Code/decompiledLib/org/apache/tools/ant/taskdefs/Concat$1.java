package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.tools.ant.types.Resource;

class Concat$1
  implements Concat.ReaderFactory<Resource>
{
  Concat$1(Concat this$0) {}
  
  public Reader getReader(Resource o)
    throws IOException
  {
    InputStream is = o.getInputStream();
    return new BufferedReader(Concat.access$1300(this$0) == null ? 
      new InputStreamReader(is) : 
      new InputStreamReader(is, Concat.access$1300(this$0)));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Concat.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */