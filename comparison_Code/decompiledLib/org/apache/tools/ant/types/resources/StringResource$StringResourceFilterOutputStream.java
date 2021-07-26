package org.apache.tools.ant.types.resources;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import org.apache.tools.ant.Project;

class StringResource$StringResourceFilterOutputStream
  extends FilterOutputStream
{
  private final ByteArrayOutputStream baos;
  
  public StringResource$StringResourceFilterOutputStream(StringResource paramStringResource)
  {
    super(new ByteArrayOutputStream());
    baos = ((ByteArrayOutputStream)out);
  }
  
  public void close()
    throws IOException
  {
    super.close();
    
    String result = StringResource.access$000(this$0) == null ? baos.toString() : baos.toString(StringResource.access$000(this$0));
    
    setValueFromOutputStream(result);
  }
  
  private void setValueFromOutputStream(String output)
  {
    String value;
    String value;
    if (this$0.getProject() != null) {
      value = this$0.getProject().replaceProperties(output);
    } else {
      value = output;
    }
    this$0.setValue(value);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.StringResource.StringResourceFilterOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */