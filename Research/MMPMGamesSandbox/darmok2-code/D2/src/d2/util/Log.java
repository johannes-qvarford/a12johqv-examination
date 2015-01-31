/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.util;

/**
 * Class used for writing D2 logs. By default, it ignores the logs
 * (writing it into the vacuum), but may be configure to write to
 * a file.
 * 
 * @author Marco Antonio
 */
public class Log {

	static java.io.PrintStream m_file = null;
	
	public static void print(String msg) {
		if (m_file != null)
			m_file.print(msg);
	}
	
	public static void println(String msg) {
		if (m_file != null)
			m_file.println(msg);
	}
	
	public static void setLogFileName(String name) throws java.io.IOException {

		java.io.PrintStream newPrintStream;
		try {
		
			java.io.File f = new java.io.File(name);
			f.createNewFile();
			
			newPrintStream = new java.io.PrintStream(f);
		} catch (java.io.IOException ex) {
			m_file = null;
			throw ex;
		}
		setPrintStream(newPrintStream); 
	}
	
	public static void useStandardOutput() {
		setPrintStream(System.out);
	}
	
	protected static void setPrintStream(java.io.PrintStream newStream) {
		if (m_file == newStream)
			return;
		
		if ((m_file != null) && (m_file != System.out))
			m_file.close();
		
		m_file = newStream;
	}
}
