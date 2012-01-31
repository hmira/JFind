package cz.cuni.mff.java.du2011x2;


import java.io.*;
import java.lang.reflect.*;
//import java.sql.Time;
import java.util.Calendar;
import java.util.regex.*;

//import sun.misc.Regexp;

//import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;
//import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector.Matcher;

/** DU 1. 
  * 
  * @author Peter Hmira
  */
public class JFind {
	static class MyFinder
	{
		private static String[] Conditions;
		
		public static void SetConditions(String[] conditions) {
			Conditions = conditions;
		}
		
		public static void GetConditions() {
			for (String s : Conditions) {
				System.out.print(s + " ");
			}
		}
		
		private static Boolean MatchConditions(File f) throws NoSuchMethodException, InvocationTargetException
		{
			if (Conditions == null)
				return true;
			for (int i = 0; i < Conditions.length; i++) {
				String methodName = Conditions[i].substring(1);
				String argumentName = "";
				if ( i < Conditions.length -1 && Conditions[i + 1].charAt(0) != '-')
				{
					argumentName = Conditions[i + 1];
					i++;
				}
				try {
					Method m = MyFinder.class.getMethod("Match_" + methodName, File.class, String.class);
					if (!((Boolean)m.invoke(MyFinder.class, f, argumentName)))
					{
						return false;
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		
		public static Boolean Match_readable(File f, String s)	{		return f.canRead();		}
		public static Boolean Match_writable(File f, String s)	{		return f.canWrite();	}
		public static Boolean Match_executable(File f, String s) {		return f.canExecute();	}		
		public static Boolean Match_name(File f, String s) {
			s = s.replace(".", "\\.");
			s = s.replace("?", ".");
			s = s.replace("*", ".*");
			Matcher m = (Pattern.compile(s)).matcher(f.getName());
			return m.matches();
		}
		public static Boolean Match_iname(File f, String s) {
			s = s.replace(".", "\\.");
			s = s.replace("?", ".");
			s = s.replace("*", ".*");
			Matcher m = (Pattern.compile(s, Pattern.CASE_INSENSITIVE)).matcher(f.getName());
			return m.matches();
		}
		public static Boolean Match_regex(File f, String s) {
			Matcher m = (Pattern.compile(s)).matcher(f.getName());
			return m.matches();
		}
		public static Boolean Match_size(File f, String s) throws NumberFormatException{
			if (s == "") return false;
			int coeff = 1;
			if (s.charAt(s.length() - 1) == 'k')
			{
				coeff = 1024;
				s = s.substring(0, s.length() - 2);
			}
			else if (s.charAt(s.length() - 1) == 'M')
			{
				coeff = 1024 * 1024;
				s = s.substring(0, s.length() - 2);
			}
			int size = Integer.parseInt(s) * coeff;
			return f.length() == size;
		}
		public static Boolean Match_ssize(File f, String s) throws NumberFormatException{
			if (s == "") return false;
			int coeff = 1;
			if (s.charAt(s.length() - 1) == 'k')
			{
				coeff = 1024;
				s = s.substring(0, s.length() - 1);
			}
			else if (s.charAt(s.length() - 1) == 'M')
			{
				coeff = 1024 * 1024;
				s = s.substring(0, s.length() - 1);
			}
			int size = Integer.parseInt(s) * coeff;
			return f.length() <= size;
		}
		public static Boolean Match_bsize(File f, String s) {
			if (s == "") return false;
			int coeff = 1;
			if (s.charAt(s.length() - 1) == 'k')
			{
				coeff = 1024;
				s = s.substring(0, s.length() - 2);
			}
			else if (s.charAt(s.length() - 1) == 'M')
			{
				coeff = 1024 * 1024;
				s = s.substring(0, s.length() - 2);
			}
			int size = Integer.parseInt(s) * coeff;
			return f.length() >= size;
		}
		public static Boolean Match_cnewer(File f, String s) throws FileNotFoundException{
			File ref = new File(s);
			if (!ref.exists())
			{
				FileNotFoundException e = new FileNotFoundException();
				throw e;
			}
				return f.lastModified() > ref.lastModified();
		}
		public static Boolean Match_colder(File f, String s) throws FileNotFoundException {
			File ref = new File(s);
			if (!ref.exists())
			{
				FileNotFoundException e = new FileNotFoundException();
				throw e;
			}
				return f.lastModified() < ref.lastModified();
		}
		public static Boolean Match_cmin(File f, String s) throws NumberFormatException{
			if (s=="") return false;
			long Minutes = Integer.parseInt(s) * 60000;
			long Current = Calendar.getInstance().getTimeInMillis();
			return (Current - f.lastModified() < Minutes);
		}
		public static void RecursiveFind(String s)
		{
			
			if (s.charAt(s.length() - 1) != '/')
				s+= "/";
			File dir = new File(s);
			
			try {
				if (MatchConditions(dir))
				{
					System.out.println(dir.getAbsolutePath());
				}
			} catch (NoSuchMethodException e) {
				System.out.printf("wrong parameters\n");
				return;
			} catch (InvocationTargetException e) {
				System.out.printf("wrong parameters\n");
				return;
			}
			
			if (dir.isDirectory())
			{
				String[] fileNames = dir.list();
				for (String name : fileNames) {
					RecursiveFind(s + name);
				}
			}
		}
		
		
	}

  public static void main(String[] argv) {
    
	  String[] Conditions= new String[argv.length - 1];
	  for (int i = 1; i < argv.length; i++) {
		  Conditions[i - 1] = argv[i];
	  }
	  MyFinder.SetConditions(Conditions);
	  MyFinder.RecursiveFind(argv[0]);

	  //MyFinder.GetConditions();
	  
  }
}

