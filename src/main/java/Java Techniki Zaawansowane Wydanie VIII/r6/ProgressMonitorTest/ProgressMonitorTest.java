import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Program testuj�cy dzia�anie okna dialogowego monitora post�pu.
 * @version 1.04 2007-08-01
 * @author Cay Horstmann
 */
public class ProgressMonitorTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new ProgressMonitorFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka zawieraj�ca przycisk uruchamiaj�cy
 * symulacj� czasoch�onnej operacji oraz pole tekstowe.
 */
class ProgressMonitorFrame extends JFrame
{
   public ProgressMonitorFrame()
   {
      setTitle("ProgressMonitorTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      // pole tekstowe, w kt�rym prezentowane jest dzia�anie w�tku
      textArea = new JTextArea();

      // tworzy panel przycisk�w
      JPanel panel = new JPanel();
      startButton = new JButton("Start");
      panel.add(startButton);

      add(new JScrollPane(textArea), BorderLayout.CENTER);
      add(panel, BorderLayout.SOUTH);

      // tworzy obiekt nas�uchuj�cy przycisku

      startButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               startButton.setEnabled(false);
               final int MAX = 1000;

               // uruchamia symulacj�
               activity = new SimulatedActivity(MAX);
               activity.execute();
               
               // uruchomia okno dialogowe monitora
               progressDialog = new ProgressMonitor(ProgressMonitorFrame.this,
                     "Waiting for Simulated Activity", null, 0, MAX);
               cancelMonitor.start();              
            }
         });

      // konfiguruje akcj� licznika czasu

      cancelMonitor = new Timer(500, new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {               
               if (progressDialog.isCanceled())
               {                  
                  activity.cancel(true);
                  startButton.setEnabled(true);                  
               }
               else if (activity.isDone())
               {
                  progressDialog.close();
                  startButton.setEnabled(true);                  
               }
               else
               {
                  progressDialog.setProgress(activity.getProgress());                  
               }
            }
         });
   }

   private Timer cancelMonitor;
   private JButton startButton;
   private ProgressMonitor progressDialog;
   private JTextArea textArea;
   private SimulatedActivity activity;

   public static final int DEFAULT_WIDTH = 300;
   public static final int DEFAULT_HEIGHT = 200;

   class SimulatedActivity extends SwingWorker<Void, Integer>
   {
      /**
       * Tworzy w�tek symulowanej operacji. Zwi�ksza on warto�� licznika
       * do momentu osi�gni�cia warto�ci docelowej.
       * @param t warto�� docelowa licznika.
       */
      public SimulatedActivity(int t)
      {
         current = 0;
         target = t;
      }

      protected Void doInBackground() throws Exception
      {
         try
         {
            while (current < target)
            {
               Thread.sleep(100);
               current++;
               textArea.append(current + "\n");               
               setProgress(current);
            }
         }
         catch (InterruptedException e)
         {
         }
         return null;
      }
      
      private int current;
      private int target;
   }      
}
