import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Igrac implements Runnable {
	Socket soket;
	BufferedReader ulazniTok;
	PrintStream izlazniTok;
	String ime;
	Thread t;

	public Igrac(Socket soket) {

		try {
			this.soket = soket;
			ulazniTok = new BufferedReader(new InputStreamReader(soket.getInputStream()));
			izlazniTok = new PrintStream(soket.getOutputStream());
			ime = ulazniTok.readLine();
			MainServer.posaljiListuSlobodnihIgraca();
			t = new Thread(this);
			t.setDaemon(true);
			t.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		try {
			// ima dosta gresaka, neki slucajevi nisu pokriveni
			while (true) {
				String[] podaci = ulazniTok.readLine().split(";");
				String protivnik = podaci[0];
				if (podaci[1].equals("1")) {
					MainServer.posaljiPozivnicu(ime, protivnik);
					
				} else {
					if (podaci[2].equals("D")) {
						
						MainServer.napraviIgru(ime, protivnik);
						igraj();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return ime;
	}

	void igraj() {

	}
}
