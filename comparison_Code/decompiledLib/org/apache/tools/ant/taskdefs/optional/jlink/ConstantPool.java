package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.DataInput;
import java.io.IOException;

class ConstantPool
{
  static final byte UTF8 = 1;
  static final byte UNUSED = 2;
  static final byte INTEGER = 3;
  static final byte FLOAT = 4;
  static final byte LONG = 5;
  static final byte DOUBLE = 6;
  static final byte CLASS = 7;
  static final byte STRING = 8;
  static final byte FIELDREF = 9;
  static final byte METHODREF = 10;
  static final byte INTERFACEMETHODREF = 11;
  static final byte NAMEANDTYPE = 12;
  byte[] types;
  Object[] values;
  
  ConstantPool(DataInput data)
    throws IOException
  {
    int count = data.readUnsignedShort();
    types = new byte[count];
    values = new Object[count];
    for (int i = 1; i < count; i++)
    {
      byte type = data.readByte();
      types[i] = type;
      switch (type)
      {
      case 1: 
        values[i] = data.readUTF();
        break;
      case 2: 
        break;
      case 3: 
        values[i] = Integer.valueOf(data.readInt());
        break;
      case 4: 
        values[i] = Float.valueOf(data.readFloat());
        break;
      case 5: 
        values[i] = Long.valueOf(data.readLong());
        i++;
        break;
      case 6: 
        values[i] = Double.valueOf(data.readDouble());
        i++;
        break;
      case 7: 
      case 8: 
        values[i] = Integer.valueOf(data.readUnsignedShort());
        break;
      case 9: 
      case 10: 
      case 11: 
      case 12: 
        values[i] = Integer.valueOf(data.readInt());
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.jlink.ConstantPool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */