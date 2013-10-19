import java.io.*;
import java.util.*;

/**
 * @version 1.11 2004-05-11
 * @author Cay Horstmann
 */
public class RandomFileTest
{  
   public static void main(String[] args)
   {
      Employee[] staff = new Employee[3];

      staff[0] = new Employee("Carl Cracker", 75000, 1987, 12, 15);
      staff[1] = new Employee("Harry Hacker", 50000, 1989, 10, 1);
      staff[2] = new Employee("Tony Tester", 40000, 1990, 3, 15);

      try
      {  
         // zapisuje rekordy wszystkich pracowników w pliku employee.dat
         DataOutputStream out = new DataOutputStream(new FileOutputStream("employee.dat"));
         for (Employee e : staff)
            e.writeData(out);
         out.close();
      
         // wczytuje wszystkie rekordy do nowej tablicy
         RandomAccessFile in = new RandomAccessFile("employee.dat", "r");   
         // oblicza rozmiar tablicy
         int n = (int)(in.length() / Employee.RECORD_SIZE);
         Employee[] newStaff = new Employee[n];

         // wczytuje rekordy pracowników w odwrotnej kolejnoœci
         for (int i = n - 1; i >= 0; i--)
         {  
            newStaff[i] = new Employee();
            in.seek(i * Employee.RECORD_SIZE);
            newStaff[i].readData(in);
         }
         in.close();
         
         // wyœwietla wczytane rekordy
         for (Employee e : newStaff) 
            System.out.println(e);
      }
      catch (IOException e)
      {  
         e.printStackTrace(); 
      }
   }
}

class Employee
{
   public Employee() {}

   public Employee(String n, double s, int year, int month, int day)
   {  
      name = n;
      salary = s;
      GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);
      hireDay = calendar.getTime();
   }

   public String getName()
   {
      return name;
   }

   public double getSalary()
   {
      return salary;
   }

   public Date getHireDay()
   {  
      return hireDay;
   }

   /**
      Podnosi wynagrodzenie pracownika.
      @byPercent podwy¿ka procentowo
   */
   public void raiseSalary(double byPercent)
   {  
      double raise = salary * byPercent / 100;
      salary += raise;
   }

   public String toString()
   {  
      return getClass().getName()
         + "[name=" + name
         + ",salary=" + salary
         + ",hireDay=" + hireDay
         + "]";
   }

   /**
      Zapisuje dane pracownika
      @param out obiekt DataOutput
   */
   public void writeData(DataOutput out) throws IOException
   {
      DataIO.writeFixedString(name, NAME_SIZE, out);
      out.writeDouble(salary);

      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(hireDay);
      out.writeInt(calendar.get(Calendar.YEAR));
      out.writeInt(calendar.get(Calendar.MONTH) + 1);
      out.writeInt(calendar.get(Calendar.DAY_OF_MONTH));
   }

   /**
      Wczytuje dane pracownika
      @param in obiekt DataOutput
   */
   public void readData(DataInput in) throws IOException
   {
      name = DataIO.readFixedString(NAME_SIZE, in);
      salary = in.readDouble();
      int y = in.readInt();
      int m = in.readInt();
      int d = in.readInt();
      GregorianCalendar calendar = new GregorianCalendar(y, m - 1, d);
      hireDay = calendar.getTime();
   }

   public static final int NAME_SIZE = 40;
   public static final int RECORD_SIZE = 2 * NAME_SIZE + 8 + 4 + 4 + 4;

   private String name;
   private double salary;
   private Date hireDay;
}

class DataIO
{
   public static String readFixedString(int size, DataInput in) 
      throws IOException
   {  
      StringBuilder b = new StringBuilder(size);
      int i = 0;
      boolean more = true;
      while (more && i < size)
      {  
         char ch = in.readChar();
         i++;
         if (ch == 0) more = false;
         else b.append(ch);
      }
      in.skipBytes(2 * (size - i));
      return b.toString();
   }

   public static void writeFixedString(String s, int size, DataOutput out) 
      throws IOException
   {
      for (int i = 0; i < size; i++)
      {  
         char ch = 0;
         if (i < s.length()) ch = s.charAt(i);
         out.writeChar(ch);
      }
   }
}
