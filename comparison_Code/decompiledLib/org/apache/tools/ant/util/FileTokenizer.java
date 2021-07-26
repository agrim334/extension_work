package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.ProjectComponent;

public class FileTokenizer
  extends ProjectComponent
  implements Tokenizer
{
  public String getToken(Reader in)
    throws IOException
  {
    return FileUtils.readFully(in);
  }
  
  public String getPostToken()
  {
    return "";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.FileTokenizer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */