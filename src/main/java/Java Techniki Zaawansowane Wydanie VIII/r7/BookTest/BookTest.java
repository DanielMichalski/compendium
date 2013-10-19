import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.print.*;
import javax.print.attribute.*;
import javax.swing.*;

/**
 * Program demonstruj¹cy tworzenie wielostronicowego wydruku.
 * Drukuje tekst transparentu, powiêkszaj¹c go, tak by wype³nia³
 * wysokoœæ ca³ej strony. Program implementuje tak¿e ogóln¹ klasê
 * okna dialogowego podgl¹du wydruku.
 * @version 1.12 2007-08-16
 * @author Cay Horstmann
 */
public class BookTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new BookTestFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka zawieraj¹ca pole tekstowe umo¿liwiaj¹ce wprowadzenie
 * tekstu transparentu oraz przyciski wydruku, formatu strony i podgl¹du wydruku.
 */
class BookTestFrame extends JFrame
{
   public BookTestFrame()
   {
      setTitle("BookTest");

      text = new JTextField();
      add(text, BorderLayout.NORTH);

      attributes = new HashPrintRequestAttributeSet();

      JPanel buttonPanel = new JPanel();

      JButton printButton = new JButton("Print");
      buttonPanel.add(printButton);
      printButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               try
               {
                  PrinterJob job = PrinterJob.getPrinterJob();
                  job.setPageable(makeBook());
                  if (job.printDialog(attributes))
                  {
                     job.print(attributes);
                  }
               }
               catch (PrinterException e)
               {
                  JOptionPane.showMessageDialog(BookTestFrame.this, e);
               }
            }
         });

      JButton pageSetupButton = new JButton("Page setup");
      buttonPanel.add(pageSetupButton);
      pageSetupButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               PrinterJob job = PrinterJob.getPrinterJob();
               pageFormat = job.pageDialog(attributes);
            }
         });

      JButton printPreviewButton = new JButton("Print preview");
      buttonPanel.add(printPreviewButton);
      printPreviewButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               PrintPreviewDialog dialog = new PrintPreviewDialog(makeBook());
               dialog.setVisible(true);
            }
         });

      add(buttonPanel, BorderLayout.SOUTH);
      pack();
   }

   /**
    * Tworzy obiekt klasy Book zawieraj¹cy stronê ok³adki
    * i kolejne strony transparentu.
    */
   public Book makeBook()
   {
      if (pageFormat == null)
      {
         PrinterJob job = PrinterJob.getPrinterJob();
         pageFormat = job.defaultPage();
      }
      Book book = new Book();
      String message = text.getText();
      Banner banner = new Banner(message);
      int pageCount = banner.getPageCount((Graphics2D) getGraphics(), pageFormat);
      book.append(new CoverPage(message + " (" + pageCount + " pages)"), pageFormat);
      book.append(banner, pageFormat, pageCount);
      return book;
   }

   private JTextField text;
   private PageFormat pageFormat;
   private PrintRequestAttributeSet attributes;
}

/**
 * Klasa reprezentuj¹ca transparent drukowany na wielu stronach.
 */
class Banner implements Printable
{
   /**
    * Tworzy obiekt reprezentuj¹cy transparent.
    * @param m tekst transparentu
    */
   public Banner(String m)
   {
      message = m;
   }

   /**
    * Zwraca liczbê stron danego rozdzia³u.
    * @param g2 kontekst graficzny
    * @param pf format strony
    * @return liczba stron
    */
   public int getPageCount(Graphics2D g2, PageFormat pf)
   {
      if (message.equals("")) return 0;
      FontRenderContext context = g2.getFontRenderContext();
      Font f = new Font("Serif", Font.PLAIN, 72);
      Rectangle2D bounds = f.getStringBounds(message, context);
      scale = pf.getImageableHeight() / bounds.getHeight();
      double width = scale * bounds.getWidth();
      int pages = (int) Math.ceil(width / pf.getImageableWidth());
      return pages;
   }

   public int print(Graphics g, PageFormat pf, int page) throws PrinterException
   {
      Graphics2D g2 = (Graphics2D) g;
      if (page > getPageCount(g2, pf)) return Printable.NO_SUCH_PAGE;
      g2.translate(pf.getImageableX(), pf.getImageableY());

      drawPage(g2, pf, page);
      return Printable.PAGE_EXISTS;
   }

   public void drawPage(Graphics2D g2, PageFormat pf, int page)
   {
      if (message.equals("")) return;
      page--; // uwzglêdnia ok³adkê

      drawCropMarks(g2, pf);
      g2.clip(new Rectangle2D.Double(0, 0, pf.getImageableWidth(), pf.getImageableHeight()));
      g2.translate(-page * pf.getImageableWidth(), 0);
      g2.scale(scale, scale);
      FontRenderContext context = g2.getFontRenderContext();
      Font f = new Font("Serif", Font.PLAIN, 72);
      TextLayout layout = new TextLayout(message, f, context);
      AffineTransform transform = AffineTransform.getTranslateInstance(0, layout.getAscent());
      Shape outline = layout.getOutline(transform);
      g2.draw(outline);
   }

   /**
    * Rysuje znaki wyznaczaj¹ce obszar drukowania
    * w odleg³oœci 1/2" od naro¿ników strony.
    * @param g2 kontekst graficzny
    * @param pf format strony
    */
   public void drawCropMarks(Graphics2D g2, PageFormat pf)
   {
      final double C = 36; // crop mark length = 1/2 inch
      double w = pf.getImageableWidth();
      double h = pf.getImageableHeight();
      g2.draw(new Line2D.Double(0, 0, 0, C));
      g2.draw(new Line2D.Double(0, 0, C, 0));
      g2.draw(new Line2D.Double(w, 0, w, C));
      g2.draw(new Line2D.Double(w, 0, w - C, 0));
      g2.draw(new Line2D.Double(0, h, 0, h - C));
      g2.draw(new Line2D.Double(0, h, C, h));
      g2.draw(new Line2D.Double(w, h, w, h - C));
      g2.draw(new Line2D.Double(w, h, w - C, h));
   }

   private String message;
   private double scale;
}

/**
 * Klasa drukuj¹ca stronê ok³adki zawieraj¹c¹ tytu³.
 */
class CoverPage implements Printable
{
   /**
    * Tworzy stronê ok³adki.
    * @param t tytu³
    */
   public CoverPage(String t)
   {
      title = t;
   }

   public int print(Graphics g, PageFormat pf, int page) throws PrinterException
   {
      if (page >= 1) return Printable.NO_SUCH_PAGE;
      Graphics2D g2 = (Graphics2D) g;
      g2.setPaint(Color.black);
      g2.translate(pf.getImageableX(), pf.getImageableY());
      FontRenderContext context = g2.getFontRenderContext();
      Font f = g2.getFont();
      TextLayout layout = new TextLayout(title, f, context);
      float ascent = layout.getAscent();
      g2.drawString(title, 0, ascent);
      return Printable.PAGE_EXISTS;
   }

   private String title;
}

/**
 * Klasa implementuj¹ca uniwersalne okno dialogowe podgl¹du wydruku.
 */
class PrintPreviewDialog extends JDialog
{
   /**
    * Tworzy okno dialogowe podgl¹du wydruku.
    * @param p obiekt typu Printable
    * @param pf format strony	
    * @param pages liczba stron obiektu p
    */
   public PrintPreviewDialog(Printable p, PageFormat pf, int pages)
   {
      Book book = new Book();
      book.append(p, pf, pages);
      layoutUI(book);
   }

   /**
    * Tworzy okno podgl¹du wydruku.
    * @param b obiekt klasy Book
    */
   public PrintPreviewDialog(Book b)
   {
      layoutUI(b);
   }

   /**
    * Rozmieszcza komponenty okna dialogowego.
    * @param book obiekt klasy Book, którego podgl¹d bêdzie prezentowany
    */
   public void layoutUI(Book book)
   {
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      canvas = new PrintPreviewCanvas(book);
      add(canvas, BorderLayout.CENTER);

      JPanel buttonPanel = new JPanel();

      JButton nextButton = new JButton("Next");
      buttonPanel.add(nextButton);
      nextButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               canvas.flipPage(1);
            }
         });

      JButton previousButton = new JButton("Previous");
      buttonPanel.add(previousButton);
      previousButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               canvas.flipPage(-1);
            }
         });

      JButton closeButton = new JButton("Close");
      buttonPanel.add(closeButton);
      closeButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               setVisible(false);
            }
         });

      add(buttonPanel, BorderLayout.SOUTH);
   }

   private PrintPreviewCanvas canvas;

   private static final int DEFAULT_WIDTH = 300;
   private static final int DEFAULT_HEIGHT = 300;
}

/**
 * Komponent prezentacji podgl¹du wydruku.
 */
class PrintPreviewCanvas extends JComponent
{
   /**
    * Tworzy obiekt prezentacji podgl¹du wydruku.
    * @param b obiekt klasy Book, którego podgl¹d bêdzie prezentowany
    */
   public PrintPreviewCanvas(Book b)
   {
      book = b;
      currentPage = 0;
   }

   public void paintComponent(Graphics g)
   {
      Graphics2D g2 = (Graphics2D) g;
      PageFormat pageFormat = book.getPageFormat(currentPage);

      double xoff; // przesuniêcie wzd³u¿ osi x pocz¹tku strony w oknie
      double yoff; // przesuniêcie wzd³u¿ osi y pocz¹tku strony w oknie
      double scale; // wspó³czynnik przeskalowania strony w oknie
      double px = pageFormat.getWidth();
      double py = pageFormat.getHeight();
      double sx = getWidth() - 1;
      double sy = getHeight() - 1;
      if (px / py < sx / sy) // centruje w poziomie
      {
         scale = sy / py;
         xoff = 0.5 * (sx - scale * px);
         yoff = 0;
      }
      else
      // centruje w pionie
      {
         scale = sx / px;
         xoff = 0;
         yoff = 0.5 * (sy - scale * py);
      }
      g2.translate((float) xoff, (float) yoff);
      g2.scale((float) scale, (float) scale);

      // rysuje obraz strony (ignoruj¹c marginesy)
      Rectangle2D page = new Rectangle2D.Double(0, 0, px, py);
      g2.setPaint(Color.white);
      g2.fill(page);
      g2.setPaint(Color.black);
      g2.draw(page);

      Printable printable = book.getPrintable(currentPage);
      try
      {
         printable.print(g2, pageFormat, currentPage);
      }
      catch (PrinterException e)
      {
         g2.draw(new Line2D.Double(0, 0, px, py));
         g2.draw(new Line2D.Double(px, 0, 0, py));
      }
   }

   /**
    * Przesuwa siê o zadan¹ liczbê stron.
    *  @param liczba stron. Wartoœæ ujemna oznacza kierunek wstecz.
    */
   public void flipPage(int by)
   {
      int newPage = currentPage + by;
      if (0 <= newPage && newPage < book.getNumberOfPages())
      {
         currentPage = newPage;
         repaint();
      }
   }

   private Book book;
   private int currentPage;
}
