package org.zkoss.zssex.model.impl;

import java.util.HashMap;
import java.util.Map;
import org.zkoss.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.zkoss.poi.ss.formula.WorkbookEvaluator;
import org.zkoss.poi.ss.usermodel.FormulaEvaluator;
import org.zkoss.zss.model.Book;
import org.zkoss.zss.model.BookSeries;
import org.zkoss.zss.model.impl.BookHelper;

public class BookSeriesImpl
  implements BookSeries
{
  private Map<String, Book> _books = new HashMap(4);

  public BookSeriesImpl(Book[] books)
  {
    int len = books.length;
    String[] workbookNames = new String[len];
    WorkbookEvaluator[] evaluators = new WorkbookEvaluator[len];
    for (int j = 0; j < books.length; ++j) {
      Book book = books[j];
      String bookName = book.getBookName();
      workbookNames[j] = bookName;
      evaluators[j] = book.getFormulaEvaluator().getWorkbookEvaluator();
      this._books.put(bookName, book);
      BookHelper.setBooks(book, this);
    }
    CollaboratingWorkbooksEnvironment.setup(workbookNames, evaluators);
  }

  public Book getBook(String bookName) {
    return ((Book)this._books.get(bookName));
  }
}