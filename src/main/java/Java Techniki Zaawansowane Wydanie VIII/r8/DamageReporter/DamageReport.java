import java.awt.*;
import java.awt.geom.*;
import java.beans.*;
import java.util.*;

/**
 * Klasa reprezentuj¹ca raport o uszkodzeniach samochodu,
 * przechowywany i odtwarzany przy u¿yciu mechanizmu trwa³oœci.
 * @version 1.21 2004-08-30
 * @author Cay Horstmann
 */
public class DamageReport
{
   public enum CarType
   {
      SEDAN, WAGON, SUV
   }

   // w³aœciwoœæ zapisywana automatycznie
   public void setRentalRecord(String newValue)
   {
      rentalRecord = newValue;
   }

   public String getRentalRecord()
   {
      return rentalRecord;
   }

   // w³aœciwoœæ zapisywana automatycznie
   public void setCarType(CarType newValue)
   {
      carType = newValue;
   }

   public CarType getCarType()
   {
      return carType;
   }

   // w³aœciwoœæ oznaczona jako transient
   public void setRemoveMode(boolean newValue)
   {
      removeMode = newValue;
   }

   public boolean getRemoveMode()
   {
      return removeMode;
   }

   public void click(Point2D p)
   {
      if (removeMode)
      {
         for (Point2D center : points)
         {
            Ellipse2D circle = new Ellipse2D.Double(center.getX() - MARK_SIZE, center.getY()
                  - MARK_SIZE, 2 * MARK_SIZE, 2 * MARK_SIZE);
            if (circle.contains(p))
            {
               points.remove(center);
               return;
            }
         }
      }
      else points.add(p);
   }

   public void drawDamage(Graphics2D g2)
   {
      g2.setPaint(Color.RED);
      for (Point2D center : points)
      {
         Ellipse2D circle = new Ellipse2D.Double(center.getX() - MARK_SIZE, center.getY()
               - MARK_SIZE, 2 * MARK_SIZE, 2 * MARK_SIZE);
         g2.draw(circle);
      }
   }

   public void configureEncoder(XMLEncoder encoder)
   {
      // operacja konieczna w celu przechowywania obiektów Point2D.Double 
      encoder.setPersistenceDelegate(Point2D.Double.class, new DefaultPersistenceDelegate(
            new String[] { "x", "y" }));

      // operacja konieczna, poniewa¿ lista punktów nie jest
      // (i nie powinna byæ) dostêpna jako w³aœciwoœæ
      encoder.setPersistenceDelegate(DamageReport.class, new DefaultPersistenceDelegate()
         {
            protected void initialize(Class<?> type, Object oldInstance, Object newInstance,
                  Encoder out)
            {
               super.initialize(type, oldInstance, newInstance, out);
               DamageReport r = (DamageReport) oldInstance;

               for (Point2D p : r.points)
                  out.writeStatement(new Statement(oldInstance, "click", new Object[] { p }));
            }
         });

   }

   // operacja konieczna ze wzglêdu 
   // na zapewnienie tymczasowoœci (transient) w³aœciwoœci removeMode
   static
   {
      try
      {
         BeanInfo info = Introspector.getBeanInfo(DamageReport.class);
         for (PropertyDescriptor desc : info.getPropertyDescriptors())
            if (desc.getName().equals("removeMode")) desc.setValue("transient", Boolean.TRUE);
      }
      catch (IntrospectionException e)
      {
         e.printStackTrace();
      }
   }

   private String rentalRecord;
   private CarType carType;
   private boolean removeMode;
   private ArrayList<Point2D> points = new ArrayList<Point2D>();

   private static final int MARK_SIZE = 5;
}

