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
	boolean poslaoPoziv;
	public final static String POZIVAC = "1";
	public final static String PRIHVACENA_IGRA = "D";
	public final static String ZAUZETO_IME="IME ZAUZETO";
	public static final String DISKONEKTOVANJE="KRAJ";
	public static final String POVUCI_POZIV="POVUCI POZIV";
	public static final String IGRAJ_PONOVO="IGRAJ PONOVO";
	boolean naPotezu;
	boolean hocePonovo;
	Igra igra;

	public Igrac(Socket soket) {

		try {
			this.soket = soket;
			ulazniTok = new BufferedReader(new InputStreamReader(soket.getInputStream()));
			izlazniTok = new PrintStream(soket.getOutputStream());
			ime = ulazniTok.readLine();
			if (MainServer.igraci.contains(this)) {
				izlazniTok.println(MainServer.PORUKE_KONEKTOVANJA+ZAUZETO_IME);
				return;
			} else {
				MainServer.igraci.add(this);
			}
			MainServer.posaljiListuSlobodnihIgraca();
			poslaoPoziv = false;
			hocePonovo = false;
			pocni();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	void pocni(){
		t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}
	public void run() {
		try {
			while (true) {
				String podaciS=ulazniTok.readLine();
				if(podaciS==null)continue;
				String[] podaci = podaciS.split(";");
				if (poslaoPoziv) {
					if (hoceDaPovucePoziv(podaci)) {
						poslaoPoziv = false;
					}
					continue;
				}
				if (hoceDaSeDiskonektuje(podaci)) {
					zatvoriVeze();
					MainServer.igraci.remove(this);
					break;
				}
				if (hocePonovoDaIgra(podaci)) {
					pokusajPonovnuIgru();
				}
				String protivnik = podaci[0];
				String tipIgraca = podaci[1];
				String prihvacenaIgra = podaci[2];
				if (tipIgraca.equals(Igrac.POZIVAC)) {
					MainServer.posaljiPozivnicu(ime, protivnik);
					poslaoPoziv = true;
				} else {
					if (prihvacenaIgra.equals(Igrac.PRIHVACENA_IGRA)) {
						MainServer.napraviIgru(ime, protivnik);
					}
				}
			}
		} catch (IOException e) {
			MainServer.igraci.remove(this);		
		}
	}

	@Override
	public String toString() {
		return ime;
	}

	void zavrsiIgru() {
		igra = null;
		notify();
		poslaoPoziv = false;
	}

	synchronized void igraj() {
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean hoceDaSeDiskonektuje(String[] niz) {
		return niz[0].equals(DISKONEKTOVANJE);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Igrac))
			return false;
		Igrac i = (Igrac) o;
		return ime.equals(i.ime);
	}

	boolean hoceDaPovucePoziv(String[] niz) {
		return niz[0].equals(POVUCI_POZIV);
	}

	boolean hocePonovoDaIgra(String[] niz) {
		return niz[0].equals(IGRAJ_PONOVO);
	}

	void zatvoriVeze() {
		try {
			ulazniTok.close();
			izlazniTok.close();
			soket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void cekajOdgovor() {
		notify();
	}

	void pokusajPonovnuIgru() {
		hocePonovo = true;
		igra.notify();
		igraj();
	}
}
