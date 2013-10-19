import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Program testuj¹cy dzia³anie monitora postêpu strumienia wejœciowego.
 * @version 1.04 2007-08-01
 * @author Cay Horstmann
 */
public class ProgressMonitorInputStreamTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new TextFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka wyposa¿ona w menu umo¿liwiaj¹ce za³adowanie
 * pliku tekstowego i obszar tekstowy pokazuj¹cy jego zawartoœæ.
 * Obszar tekstowy jest tworzony i inicjowany po zakoñczeniu
 * wczytywania pliku, aby unikn¹æ migania.
 */
class TextFrame extends JFrame
{
   public TextFrame()
   {
      setTitle("ProgressMonitorInputStreamTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      textArea = new JTextArea();
      add(new JScrollPane(textArea));

      chooser = new JFileChooser();
      chooser.setCurrentDirectory(new File("."));

      JMenuBar menuBar = new JMenuBar();
      setJMenuBar(menuBar);
      JMenu fileMenu = new JMenu("File");
      menuBar.add(fileMenu);
      openItem = new JMenuItem("Open");
      openItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               try
               {
                  openFile();
               }
               catch (IOException exception)
               {
                  exception.printStackTrace();
               }
            }
         });

      fileMenu.add(openItem);
      exitItem = new JMenuItem("Exit");
      exitItem.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               System.exit(0);
            }
         });
      fileMenu.add(exitItem);
   }

   /**
    * Umo¿liwia u¿ytkownikowi wybranie pliku, którego zawartoœæ
    * zostanie pokazana w obszarze tekstowym.
    */
   public void openFile() throws IOException
   {
      int r = chooser.showOpenDialog(this);
      if (r != JFileChooser.APPROVE_OPTION) return;
      final File f = chooser.getSelectedFile();

      // tworzy strumieñ i sekwencjê filtrów odczytu
      
      FileInputStream fileIn = new FileInputStream(f);
      ProgressMonitorInputStream progressIn = new ProgressMonitorInputStream(this, "Reading "
            + f.getName(), fileIn);
      final Scanner in = new Scanner(progressIn);

      textArea.setText("");

      SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
         {
            protected Void doInBackground() throws Exception
            {
               while (in.hasNextLine())
               {
                  String line = in.nextLine();
                  textArea.append(line);
                  textArea.append("\n");
               }
               in.close();
               return null;
            }
         };
      worker.execute();
   }

   private JMenuItem openItem;
   private JMenuItem exitItem;
   private JTextArea textArea;
   private JFileChooser chooser;

   public static final int DEFAULT_WIDTH = 300;
   public static final int DEFAULT_HEIGHT = 200;
}
