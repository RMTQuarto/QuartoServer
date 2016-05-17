import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Igrac implements Runnable {
	public final static String POZIVAC = "1";
	public final static String PRIHVACENA_IGRA = "D";
	public final static String ZAUZETO_IME = "IME ZAUZETO";
	public static final String DISKONEKTOVANJE = "KRAJ";
	public static final String POVUCI_POZIV = "POVUCI POZIV";
	public static final String IGRAJ_PONOVO = "IGRAJ PONOVO";
	public static final String POZIVNICA_USPESNA="OK";
	public static final String NECE_PONOVNU_IGRU="N";
	
	
	private Socket soket;
	private BufferedReader ulazniTok;
	private PrintStream izlazniTok;
	private String ime;
	private Thread t;
	private boolean poslaoPoziv;
	private boolean naPotezu;
	private Igra igra;
	private volatile boolean aktivnaNit;
	private volatile Boolean hocePonovo;
	
	
	public Boolean getHocePonovo() {
		return hocePonovo;
	}



	public void setHocePonovo(Boolean hocePonovo) {
		this.hocePonovo = hocePonovo;
	}



	public Igra getIgra() {
		return igra;
	}

	

	public boolean isNaPotezu() {
		return naPotezu;
	}



	public void setNaPotezu(boolean naPotezu) {
		this.naPotezu = naPotezu;
	}



	public BufferedReader getUlazniTok() {
		return ulazniTok;
	}



	public void setIgra(Igra igra) {
		this.igra = igra;
	}



	public String getIme() {
		return ime;
	}



	public PrintStream getIzlazniTok() {
		return izlazniTok;
	}

	
	

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
			MainServer.izbaciIgraca(this);
		}

	}

	private void pocni() {
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
						odbiPonovnuIgru();
						continue;
					}
					if(podaciS.equals(IGRAJ_PONOVO)){
						igrajPonovo();
						continue;
					}
					if (podaciS.equals(DISKONEKTOVANJE)) {
						zatvoriVeze();
						MainServer.izbaciIgraca(this);
						return;
					}
					if (poslaoPoziv) {
						if (podaciS.equals(POVUCI_POZIV)) {
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
	public void zavrsiIgru() {
		igra = null;
		hocePonovo=null;
		poslaoPoziv = false;
		aktivnaNit=true;	
	}

	public void igraj() {
		aktivnaNit = false;
	}

	public void cekajOdgovor(){
		aktivnaNit=true;
	}
	private void odbiPonovnuIgru(){
		aktivnaNit=false;
		hocePonovo=new Boolean(false);
//		synchronized(igra){
//			igra.notify();
//		}
	}
	private void igrajPonovo(){
		aktivnaNit=false;
		hocePonovo=new Boolean(true);
//		synchronized(igra){
//			igra.notify();
//		}
	}
	public void zatvoriVeze() {
		try {
			ulazniTok.close();
			izlazniTok.close();
			soket.close();
		} catch (IOException e) {
			MainServer.izbaciIgraca(this);
		}

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
