import java.io.*;
import java.util.*;

/**
 * @version 1.12 2007-06-22
 * @author Cay Horstmann
 */
public class TextFileTest
{
   public static void main(String[] args)
   {
      Employee[] staff = new Employee[3];

      staff[0] = new Employee("Carl Cracker", 75000, 1987, 12, 15);
      staff[1] = new Employee("Harry Hacker", 50000, 1989, 10, 1);
      staff[2] = new Employee("Tony Tester", 40000, 1990, 3, 15);

      try
      {
         // zapisuje wszystkie rekordy pracownik�w w pliku employee.dat
         PrintWriter out = new PrintWriter("employee.dat");
         writeData(staff, out);
         out.close();

         // wczytuje wszystkie rekordy do nowej tablicy
         Scanner in = new Scanner(new FileReader("employee.dat"));
         Employee[] newStaff = readData(in);
         in.close();

         // wy�wietla wszystkie wczytane rekordy
         for (Employee e : newStaff)
            System.out.println(e);
      }
      catch (IOException exception)
      {
         exception.printStackTrace();
      }
   }

   /**
    * Zapisuje dane wszystkich obiekt�w Employee
    * umieszczonych w tablicy
    * do obiektu PrintWriter
    * @param employees tablica obiekt�w Employee
    * @param out obiekt PrintWriter
    */
   private static void writeData(Employee[] employees, PrintWriter out) throws IOException
   {
      // zapisuje liczb� obiekt�w
      out.println(employees.length);

      for (Employee e : employees)
         e.writeData(out);
   }

   /**
    * Wczytuje tablic� obiekt�w Employee
    * @param in obiekt Scanner
    * @return tablica obiekt�w Employee
    */
   private static Employee[] readData(Scanner in)
   {
      // pobiera rozmiar tablicy
      int n = in.nextInt();
      in.nextLine(); // consume newline

      Employee[] employees = new Employee[n];
      for (int i = 0; i < n; i++)
      {
         employees[i] = new Employee();
         employees[i].readData(in);
      }
      return employees;
   }
}

class Employee
{
   public Employee()
   {
   }

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

   public void raiseSalary(double byPercent)
   {
      double raise = salary * byPercent / 100;
      salary += raise;
   }

   public String toString()
   {
      return getClass().getName() + "[name=" + name + ",salary=" + salary + ",hireDay=" + hireDay
            + "]";
   }

   /**
    * Zapisuje dane obiektu Employee 
    * do obiektu PrintWriter
    * @param out obiektu PrintWriter
    */
   public void writeData(PrintWriter out)
   {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(hireDay);
      out.println(name + "|" + salary + "|" + calendar.get(Calendar.YEAR) + "|"
            + (calendar.get(Calendar.MONTH) + 1) + "|" + calendar.get(Calendar.DAY_OF_MONTH));
   }

   /**
    * Wczytuje dane obiektu Employee 
    * @param in obiekt Scanner
    */
   public void readData(Scanner in)
   {
      String line = in.nextLine();
      String[] tokens = line.split("\\|");
      name = tokens[0];
      salary = Double.parseDouble(tokens[1]);
      int y = Integer.parseInt(tokens[2]);
      int m = Integer.parseInt(tokens[3]);
      int d = Integer.parseInt(tokens[4]);
      GregorianCalendar calendar = new GregorianCalendar(y, m - 1, d);
      hireDay = calendar.getTime();
   }

   private String name;
   private double salary;
   private Date hireDay;
}