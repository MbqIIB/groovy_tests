package threads;

public class MainClass {

	public static void main(String[] args) {
		SampleThread sp = new SampleThread();
		SampleThread sp2 = new SampleThread();
		
		sp.run();
		sp2.run();
		
	}

}
