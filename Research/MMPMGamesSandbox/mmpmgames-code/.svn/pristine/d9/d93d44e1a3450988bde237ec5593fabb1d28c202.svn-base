package towers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Action {
	public String m_name,m_actor;
	public String m_unit_id;
	HashMap<String,String> m_parameters = new HashMap<String,String>();
	
	public Action(String name,String actor,String unit_id) {
		m_name = name;
		m_actor = actor;
		m_unit_id = unit_id;
	}
	
	public void addParameter(String n,String v) {
		m_parameters.put(n,v);
	}
	
	public String getParameter(String n) {
		return m_parameters.get(n);
	}

	public String toString() {
		String str = m_name + "(" + m_actor + ", " + m_unit_id;
		for(String p:m_parameters.keySet()) {
			str += ", " + p + " = " + m_parameters.get(p); 
		}
		return str + ")";
	}
	
	void saveToXML(FileWriter fp,int spaces) throws IOException {
		int i;		

		for(i=0;i<spaces;i++) fp.write(" "); 
		fp.write("<action name=\"" + m_name + "\" actor=\"" + m_actor + "\" unit-id=\"" + m_unit_id + "\">\n");

		for(String name:m_parameters.keySet()) {
			for(i=0;i<spaces+2;i++) fp.write(" ");
			fp.write("<" + name + ">" + m_parameters.get(name) + "</" + name + ">\n");
		} // while 
		for(i=0;i<spaces;i++) fp.write(" ");
		fp.write("</action>\n");
	}
}
