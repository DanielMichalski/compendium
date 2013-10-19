import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

/**
 * Program demonstruj¹cy wykorzystanie ramek wewnêtrznych.
 * @version 1.11 2007-08-01
 * @author Cay Horstmann
 */
public class InternalFrameTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new DesktopFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka pulpitu zawieraj¹ca panele wyœwietlaj¹ce zawartoœæ plików HTML.
 */
class DesktopFrame extends JFrame
{
   public DesktopFrame()
   {
      setTitle("InternalFrameTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      desktop = new JDesktopPane();
      add(desktop, BorderLayout.CENTER);

      // tworzy menu

      JMenuBar menuBar = new JMenuBar();
      setJMenuBar(menuBar);
      JMenu fileMenu = new JMenu("File");
      menuBar.add(fileMenu);
      JMenuItem openItem = new JMenuItem("New");
      openItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               createInternalFrame(new JLabel(new ImageIcon(planets[counter] + ".gif")),
                     planets[counter]);
               counter = (counter + 1) % planets.length;
            }
         });
      fileMenu.add(openItem);
      JMenuItem exitItem = new JMenuItem("Exit");
      exitItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               System.exit(0);
            }
         });
      fileMenu.add(exitItem);
      JMenu windowMenu = new JMenu("Window");
      menuBar.add(windowMenu);
      JMenuItem nextItem = new JMenuItem("Next");
      nextItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               selectNextWindow();
            }
         });
      windowMenu.add(nextItem);
      JMenuItem cascadeItem = new JMenuItem("Cascade");
      cascadeItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               cascadeWindows();
            }
         });
      windowMenu.add(cascadeItem);
      JMenuItem tileItem = new JMenuItem("Tile");
      tileItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               tileWindows();
            }
         });
      windowMenu.add(tileItem);
      final JCheckBoxMenuItem dragOutlineItem = new JCheckBoxMenuItem("Drag Outline");
      dragOutlineItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               desktop.setDragMode(dragOutlineItem.isSelected() ? JDesktopPane.OUTLINE_DRAG_MODE
                     : JDesktopPane.LIVE_DRAG_MODE);
            }
         });
      windowMenu.add(dragOutlineItem);
   }

   /**
    * Tworzy wewnêtrzn¹ ramkê pulpitu.
    * @param c komponent wewn¹trz ramki wewnêtrznej
    * @param t tytu³ ramki wewnêtrznej.
    */
   public void createInternalFrame(Component c, String t)
   {
      final JInternalFrame iframe = new JInternalFrame(t, 
         true,   // zmiana rozmiarów
         true,  // mo¿liwoœæ zamkniêcia
         true,  // maksymalizacja
         true); // zwiniêcie do ikony

      iframe.add(c, BorderLayout.CENTER);
      desktop.add(iframe);

      iframe.setFrameIcon(new ImageIcon("document.gif"));

      // dodaje obiekt nas³uchuj¹cy, aby potwierdziæ zamkniêcie ramki
      iframe.addVetoableChangeListener(new VetoableChangeListener()
         {
            public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException
            {
               String name = event.getPropertyName();
               Object value = event.getNewValue();

               // sprawdza tylko próby zamkniêcia ramki
               if (name.equals("closed") && value.equals(true))
               {
                  // prosi u¿ytkownika o potwierdzenie zamkniêcia ramki
                  int result = JOptionPane.showInternalConfirmDialog(iframe, "OK to close?",
                        "Select an Option", JOptionPane.YES_NO_OPTION);

                  // jeœli u¿ytkownik siê nie zgodzi, to zg³asza weto
                  if (result != JOptionPane.YES_OPTION) throw new PropertyVetoException(
                        "User canceled close", event);
               }
            }
         });

      // ustala pozycjê ramki
      int width = desktop.getWidth() / 2;
      int height = desktop.getHeight() / 2;
      iframe.reshape(nextFrameX, nextFrameY, width, height);

      iframe.show();

      // wybór ramki - mo¿e zostaæ zawetowany
      try
      {
         iframe.setSelected(true);
      }
      catch (PropertyVetoException e)
      {
      }

      frameDistance = iframe.getHeight() - iframe.getContentPane().getHeight();

      // oblicza odleg³oœæ pomiêdzy ramkami

      nextFrameX += frameDistance;
      nextFrameY += frameDistance;
      if (nextFrameX + width > desktop.getWidth()) nextFrameX = 0;
      if (nextFrameY + height > desktop.getHeight()) nextFrameY = 0;
   }

   /**
    * Rozmieszcza kaskadowo te ramki pulpitu, które nie s¹ zwiniête do ikony.
    */
   public void cascadeWindows()
   {
      int x = 0;
      int y = 0;
      int width = desktop.getWidth() / 2;
      int height = desktop.getHeight() / 2;

      for (JInternalFrame frame : desktop.getAllFrames())
      {
         if (!frame.isIcon())
         {
            try
            {
               // próbuje przeprowadziæ ramki do stanu poœredniego
               // co  mo¿e zostaæ zawetowane

               frame.setMaximum(false);
               frame.reshape(x, y, width, height);

               x += frameDistance;
               y += frameDistance;
               // wrap around at the desktop edge
               if (x + width > desktop.getWidth()) x = 0;
               if (y + height > desktop.getHeight()) y = 0;
            }
            catch (PropertyVetoException e)
            {
            }
         }
      }
   }

   /**
    * Rozmieszcza s¹siaduj¹co te ramki pulpitu, które nie s¹ zwiniete do ikony.
    */
   public void tileWindows()
   {
      // zlicza ramki, które nie s¹ zwiniête do ikony
      int frameCount = 0;
      for (JInternalFrame frame : desktop.getAllFrames())
         if (!frame.isIcon()) frameCount++;
      if (frameCount == 0) return;

      int rows = (int) Math.sqrt(frameCount);
      int cols = frameCount / rows;
      int extra = frameCount % rows;
      // liczba kolumn z dodatkowym wierszem

      int width = desktop.getWidth() / cols;
      int height = desktop.getHeight() / rows;
      int r = 0;
      int c = 0;
      for (JInternalFrame frame : desktop.getAllFrames())
      {
         if (!frame.isIcon())
         {
            try
            {
               frame.setMaximum(false);
               frame.reshape(c * width, r * height, width, height);
               r++;
               if (r == rows)
               {
                  r = 0;
                  c++;
                  if (c == cols - extra)
                  {
                     // dodatkowy wiersz
                     rows++;
                     height = desktop.getHeight() / rows;
                  }
               }
            }
            catch (PropertyVetoException e)
            {
            }
         }
      }
   }

   /**
    * Wybiera ramkê.
    */
   public void selectNextWindow()
   {
      JInternalFrame[] frames = desktop.getAllFrames();
      for (int i = 0; i < frames.length; i++)
      {
         if (frames[i].isSelected())
         {
            // znajduje ramkê, która nie jest zwiniêta do ikony
            // i mo¿e zostaæ wybrana
            int next = (i + 1) % frames.length;
            while (next != i)
            {
               if (!frames[next].isIcon())
               {
                  try
                  {
                     // pozosta³e ramki s¹ zwiniête do ikon lub zg³osi³y weto do wyboru
                     frames[next].setSelected(true);
                     frames[next].toFront();
                     frames[i].toBack();
                     return;
                  }
                  catch (PropertyVetoException e)
                  {
                  }
               }
               next = (next + 1) % frames.length;
            }
         }
      }
   }

   private JDesktopPane desktop;
   private int nextFrameX;
   private int nextFrameY;
   private int frameDistance;
   private int counter;
   private static final String[] planets = { "Mercury", "Venus", "Earth", "Mars", "Jupiter",
         "Saturn", "Uranus", "Neptune", "Pluto", };

   private static final int DEFAULT_WIDTH = 600;
   private static final int DEFAULT_HEIGHT = 400;
}
