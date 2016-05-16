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
	public final static String ZAUZETO_IME = "IME ZAUZETO";
	public static final String DISKONEKTOVANJE = "KRAJ";
	public static final String POVUCI_POZIV = "POVUCI POZIV";
	public static final String IGRAJ_PONOVO = "IGRAJ PONOVO";
	public static final String POZIVNICA_USPESNA="OK";
	public static final String NECE_PONOVNU_IGRU="N";
	boolean naPotezu;
	Igra igra;
	volatile boolean aktivnaNit;
	volatile Boolean hocePonovo;

	public Igrac(Socket soket) {

		try {
			this.soket = soket;
			ulazniTok = new BufferedReader(new InputStreamReader(soket.getInputStream()));
			izlazniTok = new PrintStream(soket.getOutputStream());
			ime = ulazniTok.readLine();
			if (MainServer.igraci.contains(this)) {
				izlazniTok.println(MainServer.PORUKE_KONEKTOVANJA + ZAUZETO_IME);
				return;
			} else {
				MainServer.igraci.add(this);
			}
			MainServer.posaljiListuSlobodnihIgraca();
			poslaoPoziv = false;
			aktivnaNit = true;
			pocni();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void pocni() {
		t = new Thread(this, ime);
		t.setDaemon(true);
		t.start();
	}

	public void run() {

		try {
			while (true) {
				if (aktivnaNit) {
					String podaciS = ulazniTok.readLine();
					if(podaciS==null){
						MainServer.izbaciIgraca(this);
						return;
					}
					if (podaciS.equals(POZIVNICA_USPESNA))
						continue;
					if(podaciS.equals(NECE_PONOVNU_IGRU)){
						hocePonovo=new Boolean(false);
						synchronized(igra){
							notify();
						}
						continue;
					}
					if(hocePonovoDaIgra(podaciS)){
						igrajPonovo();
						continue;
					}
					if (hoceDaSeDiskonektuje(podaciS)) {
						zatvoriVeze();
						MainServer.izbaciIgraca(this);
						return;
					}
					if (poslaoPoziv) {
						if (hoceDaPovucePoziv(podaciS)) {
							poslaoPoziv = false;
						}
						continue;
					}	
					String[] podaci = podaciS.split(";");					
					String protivnik = podaci[0];
					String tipIgraca = podaci[1];
					String prihvacenaIgra = podaci[2];
					if (tipIgraca.equals(Igrac.POZIVAC)) {
						MainServer.posaljiPozivnicu(ime, protivnik);
						poslaoPoziv = true;
					} else {
						if (prihvacenaIgra.equals(Igrac.PRIHVACENA_IGRA)) {
							MainServer.napraviIgru(protivnik, ime);
						}
					}
				}
			}
		} catch (IOException e) {
			MainServer.izbaciIgraca(this);
		}
	}
	void zavrsiIgru() {
		igra = null;
		hocePonovo=null;
		poslaoPoziv = false;
		aktivnaNit=true;	
	}

	void igraj() {
		aktivnaNit = false;
	}

	boolean hoceDaSeDiskonektuje(String komanda) {
		return komanda.equals(DISKONEKTOVANJE);
	}
	boolean hocePonovoDaIgra(String odgovor) {
		return odgovor.equals(IGRAJ_PONOVO);
	}
	void cekajOdgovor(){
		aktivnaNit=true;
	}
	void igrajPonovo(){
		aktivnaNit=false;
		hocePonovo=new Boolean(true);
		synchronized(igra){
			notify();
		}
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
	boolean hoceDaPovucePoziv(String komanda) {
		return komanda.equals(POVUCI_POZIV);
	}
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Igrac))
			return false;
		Igrac i = (Igrac) o;
		return ime.equals(i.ime);
	}
	@Override
	public String toString() {
		return ime;
	}
	
	
	

	
	
}
