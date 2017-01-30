package threads;

public class SampleThread implements Runnable {

	@Override
	public void run() {
		int c = 0;
		while(true) {
			try {
				System.out.println(c);
				c++;
				Thread.sleep(1000);				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
}
