package org.zkoss.zssex.util;

public class Base64Coder
{
  private static final String systemLineSeparator = System.getProperty("line.separator");
  private static char[] map1 = new char[64];
  private static byte[] map2;

  public static String encodeString(String paramString)
  {
    return new String(encode(paramString.getBytes()));
  }

  public static String encodeLines(byte[] paramArrayOfByte)
  {
    return encodeLines(paramArrayOfByte, 0, paramArrayOfByte.length, 76, systemLineSeparator);
  }

  public static String encodeLines(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    int i = paramInt3 * 3 / 4;
    if (i <= 0)
      throw new IllegalArgumentException();
    int j = (paramInt2 + i - 1) / i;
    int k = (paramInt2 + 2) / 3 * 4 + j * paramString.length();
    StringBuilder localStringBuilder = new StringBuilder(k);
    int l = 0;
    while (l < paramInt2)
    {
      int i1 = Math.min(paramInt2 - l, i);
      localStringBuilder.append(encode(paramArrayOfByte, paramInt1 + l, i1));
      localStringBuilder.append(paramString);
      l += i1;
    }
    return localStringBuilder.toString();
  }

  public static char[] encode(byte[] paramArrayOfByte)
  {
    return encode(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public static char[] encode(byte[] paramArrayOfByte, int paramInt)
  {
    return encode(paramArrayOfByte, 0, paramInt);
  }

  public static char[] encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = (paramInt2 * 4 + 2) / 3;
    int j = (paramInt2 + 2) / 3 * 4;
    char[] arrayOfChar = new char[j];
    int k = paramInt1;
    int l = paramInt1 + paramInt2;
    for (int i1 = 0; k < l; ++i1)
    {
      int i2 = paramArrayOfByte[(k++)] & 0xFF;
      int i3 = (k < l) ? paramArrayOfByte[(k++)] & 0xFF : 0;
      int i4 = (k < l) ? paramArrayOfByte[(k++)] & 0xFF : 0;
      int i5 = i2 >>> 2;
      int i6 = (i2 & 0x3) << 4 | i3 >>> 4;
      int i7 = (i3 & 0xF) << 2 | i4 >>> 6;
      int i8 = i4 & 0x3F;
      arrayOfChar[(i1++)] = map1[i5];
      arrayOfChar[(i1++)] = map1[i6];
      arrayOfChar[i1] = ((i1 < i) ? map1[i7] : '=');
      arrayOfChar[(++i1)] = ((i1 < i) ? map1[i8] : '=');
    }
    return arrayOfChar;
  }

  public static String decodeString(String paramString)
  {
    return new String(decode(paramString));
  }

  public static byte[] decodeLines(String paramString)
  {
    char[] arrayOfChar = new char[paramString.length()];
    int i = 0;
    for (int j = 0; j < paramString.length(); ++j)
    {
      int k = paramString.charAt(j);
      if ((k != 32) && (k != 13) && (k != 10) && (k != 9))
        arrayOfChar[(i++)] = (char)k;
    }
    return decode(arrayOfChar, 0, i);
  }

  public static byte[] decode(String paramString)
  {
    return decode(paramString.toCharArray());
  }

  public static byte[] decode(char[] paramArrayOfChar)
  {
    return decode(paramArrayOfChar, 0, paramArrayOfChar.length);
  }

  public static byte[] decode(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramInt2 % 4 != 0)
      throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
    while ((paramInt2 > 0) && (paramArrayOfChar[(paramInt1 + paramInt2 - 1)] == '='))
      --paramInt2;
    int i = paramInt2 * 3 / 4;
    byte[] arrayOfByte = new byte[i];
    int j = paramInt1;
    int k = paramInt1 + paramInt2;
    int l = 0;
    while (j < k)
    {
      int i1 = paramArrayOfChar[(j++)];
      int i2 = paramArrayOfChar[(j++)];
      int i3 = (j < k) ? paramArrayOfChar[(j++)] : 65;
      int i4 = (j < k) ? paramArrayOfChar[(j++)] : 65;
      if ((i1 > 127) || (i2 > 127) || (i3 > 127) || (i4 > 127))
        throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
      int i5 = map2[i1];
      int i6 = map2[i2];
      int i7 = map2[i3];
      int i8 = map2[i4];
      if ((i5 < 0) || (i6 < 0) || (i7 < 0) || (i8 < 0))
        throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
      int i9 = i5 << 2 | i6 >>> 4;
      int i10 = (i6 & 0xF) << 4 | i7 >>> 2;
      int i11 = (i7 & 0x3) << 6 | i8;
      arrayOfByte[(l++)] = (byte)i9;
      if (l < i)
        arrayOfByte[(l++)] = (byte)i10;
      if (l < i)
        arrayOfByte[(l++)] = (byte)i11;
    }
    return arrayOfByte;
  }

  static
  {
    int i = 0, j;
    for (j = 65; j <= 90; j = (char)(j + 1))
      map1[(i++)] = (char)j;
    for (j = 97; j <= 122; j = (char)(j + 1))
      map1[(i++)] = (char)j;
    for (j = 48; j <= 57; j = (char)(j + 1))
      map1[(i++)] = (char)j;
    map1[(i++)] = '+';
    map1[(i++)] = '/';
    map2 = new byte[128];
    for (i = 0; i < map2.length; ++i)
      map2[i] = -1;
    for (i = 0; i < 64; ++i)
      map2[map1[i]] = (byte)i;
  }
}