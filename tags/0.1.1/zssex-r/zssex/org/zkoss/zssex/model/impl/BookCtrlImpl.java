package org.zkoss.zssex.model.impl;

import org.zkoss.poi.ss.SpreadsheetVersion;
import org.zkoss.zss.engine.RefBook;
import org.zkoss.zss.model.Book;
import org.zkoss.zssex.engine.impl.RefBookImpl;

public class BookCtrlImpl extends org.zkoss.zss.model.impl.BookCtrlImpl
{
  public RefBook newRefBook(Book paramBook)
  {
    return new RefBookImpl(paramBook.getBookName(), paramBook.getSpreadsheetVersion().getLastRowIndex(), paramBook.getSpreadsheetVersion().getLastColumnIndex());
  }
}