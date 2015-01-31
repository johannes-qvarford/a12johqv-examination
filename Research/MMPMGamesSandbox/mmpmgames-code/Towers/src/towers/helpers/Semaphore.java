package towers.helpers;

public class Semaphore {
	private int count;
	public Semaphore(int n) {
		count = n;
	}
	
	public synchronized void WAIT() {
		while(count<=0) {
			try {
				wait();
			} catch (Exception e) {
			}
		}
		count--; 
	}
	
	public synchronized void SIGNAL() {
		count++;
		notify();
	}
}
