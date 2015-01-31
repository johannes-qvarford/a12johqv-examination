package bc.helpers;

public class VirtualController {
	public String m_id;
	public String m_name;

	public boolean m_joystick[]={false,false,false,false};
	public boolean m_button[]={false,false};
	public boolean m_pause;
	public boolean m_quit;

	public boolean m_old_joystick[]={false,false,false,false};
	public boolean m_old_button[]={false,false};
	public boolean m_old_pause;
	public boolean m_old_quit;

	public void reset()
	{
		m_old_joystick[0] = m_joystick[0] = false;
		m_old_joystick[1] = m_joystick[1] = false;
		m_old_joystick[2] = m_joystick[2] = false;
		m_old_joystick[3] = m_joystick[3] = false;

		m_old_button[0] = m_button[0] = false;
		m_old_button[1] = m_button[1] = false;

		m_old_pause = m_pause = false;
		m_old_quit = m_quit = false;
	} 

	public void cycle()
	{
		m_old_joystick[0] = m_joystick[0];
		m_old_joystick[1] = m_joystick[1];
		m_old_joystick[2] = m_joystick[2];
		m_old_joystick[3] = m_joystick[3];

		m_old_button[0] = m_button[0];
		m_old_button[1] = m_button[1];

		m_old_pause = m_pause;
		m_old_quit = m_quit;
	}

}
