package org.zkoss.poi.ss.formula.functions;

import java.text.NumberFormat;
import java.text.ParseException;

public class ComplexFormat extends org.apache.commons.math.complex.ComplexFormat
{
  public ComplexFormat()
  {
  }

  public ComplexFormat(NumberFormat realFormat, NumberFormat imaginaryFormat)
  {
    super(realFormat, imaginaryFormat);
  }

  public ComplexFormat(NumberFormat format)
  {
    super(format);
  }

  public ComplexFormat(String imaginaryCharacter, NumberFormat realFormat, NumberFormat imaginaryFormat)
  {
    super(imaginaryCharacter, realFormat, imaginaryFormat);
  }

  public ComplexFormat(String imaginaryCharacter, NumberFormat format)
  {
    super(imaginaryCharacter, format);
  }

  public ComplexFormat(String imaginaryCharacter)
  {
    super(imaginaryCharacter);
  }

  public Complex parse(String source, String suffix) throws ParseException
  {
    org.apache.commons.math.complex.Complex c = super.parse(source);
    return new Complex(c.getReal(), c.getImaginary(), suffix);
  }
}