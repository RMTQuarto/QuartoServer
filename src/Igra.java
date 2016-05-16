import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Igra implements Runnable {
	Igrac igrac1;
	Igrac igrac2;
	Tabla tabla;
	Figure figure;
	boolean igrac1PocinjeIgru;
	Thread t;
	Igra igra;
	public static final String NERESENO="NERESENO";
	public static final String POBEDA=" JE POBEDIO";
	public static final String NOVA_IGRA="NOVA IGRA?";
	public static final String IGRAS="IGRAS";
	public static final String NEMA_IGRE="NEMA IGRE";
	volatile boolean aktivnaNit;

	public Igra(Igrac igrac1, Igrac igrac2) {
		inicijalizuj(igrac1, igrac2);
		igrac1.izlazniTok.println(MainServer.PORUKE_POZIVANJA_NA_IGRU+Igrac.PRIHVACENA_IGRA);
		Random r = new Random();
		igrac1PocinjeIgru = (r.nextFloat() < 0.5) ? true : false;	
		podesiRedosled(igrac1PocinjeIgru);
	}

	public Igra(Igrac igrac1, Igrac igrac2, boolean igrac1PocinjeIgru) {
		inicijalizuj(igrac1, igrac2);
		this.igrac1PocinjeIgru = igrac1PocinjeIgru;
		podesiRedosled(igrac1PocinjeIgru);
	}

	void inicijalizuj(Igrac igrac1, Igrac igrac2) {
		this.igrac1 = igrac1;	
		this.igrac2 = igrac2;
		igrac1.hocePonovo=null;
		igrac2.hocePonovo=null;
		tabla = new Tabla();
		figure = new Figure();
		aktivnaNit=true;
		igra=this;
	}

	public void pocni() {	
		t = new Thread(this,igrac1.ime+igrac2.ime);
		t.setDaemon(true);
		t.start();	
		
	}

	public synchronized void run() {
		// debug na serveru
		if(igrac1PocinjeIgru){
			System.out.println(igrac1);
		}else System.out.println(igrac2);
		
		
		try{
			//cekanje zbog nepreklapanja poruka
			wait(1000);
		if (igrac1.naPotezu) {
			igrac1.izlazniTok.println(MainServer.PORUKE_IGRE+IGRAS);
			omoguciProtivnikuDaIgra(igrac1, igrac2);
		} else {
			igrac2.izlazniTok.println(MainServer.PORUKE_IGRE+IGRAS);
			omoguciProtivnikuDaIgra(igrac2, igrac1);
		}
		
		while (true) {
			
			if(aktivnaNit){
			if (igrac1.naPotezu) {
			
				staviFiguruNaTablu(igrac1,igrac2);
				proveriPobedu(igrac1,igrac2);
				omoguciProtivnikuDaIgra(igrac1, igrac2);
			} else {
			
				staviFiguruNaTablu(igrac2,igrac1);
				proveriPobedu(igrac2,igrac1);
				omoguciProtivnikuDaIgra(igrac2, igrac1);
			}
		}
			
		}
	} catch (IOException e) {
		igrac1.zavrsiIgru();
		igrac2.zavrsiIgru();
		igrac1.zatvoriVeze();
		igrac2.zatvoriVeze();
		MainServer.izbaciIgraca(igrac1);
		MainServer.izbaciIgraca(igrac2);
	} catch (KrajIgreException e){
		return;
	} catch(InterruptedException e){
		return;
	}
		
	}
	private void proveriPobedu(Igrac naPotezu, Igrac ceka) throws KrajIgreException, InterruptedException{
		if (tabla.partijaJeZavrsena() != 0) {
			if (tabla.partijaJeZavrsena() == 1) {
				String pobednik = (naPotezu.naPotezu) ?naPotezu.toString():ceka.toString();
				
				posaljiObojiciPoruku(pobednik+POBEDA);
			}
			if (tabla.partijaJeZavrsena() == 2) {
				posaljiObojiciPoruku(NERESENO);
			}
			// cekanje zbog nepreklapanja poruka
				wait(1000);
			
			posaljiObojiciPoruku(NOVA_IGRA);
			
			
			igrac1.cekajOdgovor();
			igrac2.cekajOdgovor();
			
			Timer tajmer=new Timer();
			tajmer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					if(igrac1.hocePonovo==null)
						igrac1.hocePonovo=false;
					if(igrac2.hocePonovo==null)
						igrac2.hocePonovo=false;
					synchronized(igra){
						notify();
					}
					synchronized(igra){
						notify();
					}
				}
			}
				
			, 8000);
				
				wait();
				wait(); 
			while(igrac1.hocePonovo==null || igrac2.hocePonovo==null){
				
			}
			tajmer=null;
			if (igrac1.hocePonovo.booleanValue() && igrac2.hocePonovo.booleanValue()) {
				MainServer.napraviPonovnuIgru(this);
				throw new KrajIgreException("napravili novu igru");
			}
			System.out.println("OOOO");
			posaljiObojiciPoruku(NEMA_IGRE);
			zavrsiIgru();
			throw new KrajIgreException("ne prave igru");
		}
	}
	void omoguciProtivnikuDaIgra(Igrac naPotezu, Igrac ceka) throws IOException{
		izaberiFiguruZaProtivnika(naPotezu, ceka);
		podesiRedosled();
	
	}

	public void posaljiObojiciPoruku(Object o) {
		igrac1.izlazniTok.println(MainServer.PORUKE_IGRE+o);
		igrac2.izlazniTok.println(MainServer.PORUKE_IGRE+o);
	}

	void podesiRedosled(boolean naPotezu) {
		igrac1.naPotezu = naPotezu;
		igrac2.naPotezu = !naPotezu;
	}
	void podesiRedosled() {
		igrac1.naPotezu = !igrac1.naPotezu;
		igrac2.naPotezu = !igrac2.naPotezu;
	}

	void staviFiguruNaTablu(Igrac naPotezu,Igrac ceka) throws IOException {		
			String potezS=naPotezu.ulazniTok.readLine();
			if(potezS==null){
				MainServer.izbaciIgraca(naPotezu);
				return;
				//posalji poruku da se prekine igra
			}			
			String[] potez = potezS.split(";");
			String figura = potez[0];
			String pozicija = potez[1];
			tabla.postaviFiguruNaPolje(figura.charAt(0), figura.charAt(1), figura.charAt(2), figura.charAt(3),
					pozicija.charAt(0), pozicija.charAt(1));
			ceka.izlazniTok.println(MainServer.PORUKE_IGRE+potezS);
	}

	void izaberiFiguruZaProtivnika(Igrac naPotezu, Igrac ceka) throws IOException{
		
			String 	figura = naPotezu.ulazniTok.readLine();
			if(figura==null){
				MainServer.izbaciIgraca(naPotezu);
				return;
				//posalji poruku da se prekine igra
			}
			figure.izbaciFiguru(figure.nadjiFiguru(figura));
			ceka.izlazniTok.println(MainServer.PORUKE_IGRE+figura);
		
	}

	void zavrsiIgru() {
		igrac1.zavrsiIgru();
		igrac2.zavrsiIgru();
		MainServer.ukiniIgru(this);
	}
	
	class KrajIgreException extends RuntimeException{
		
		public KrajIgreException(String poruka){
			super(poruka);
		}
	}
}
