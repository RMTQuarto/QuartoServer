import java.io.IOException;
import java.util.Random;
import java.util.TimerTask;

import javax.management.timer.Timer;

public class Igra implements Runnable {
	Igrac igrac1;
	Igrac igrac2;
	Tabla tabla;
	Figure figure;
	boolean igrac1PocinjeIgru;
	Thread t;
	public static final String NERESENO="NERESENO";
	public static final String POBEDA="JE POBEDIO";
	public static final String NOVA_IGRA="NOVA IGRA?";
	public static final String IGRAS="IGRAS";
	volatile boolean aktivnaNit;

	public Igra(Igrac igrac1, Igrac igrac2) {
		inicijalizuj(igrac1, igrac2);
		Random r = new Random();
	//	igrac1PocinjeIgru = (r.nextFloat() < 0.5) ? true : false;
		igrac1PocinjeIgru=true;
		podesiRedosled(igrac1PocinjeIgru);
		igrac1.izlazniTok.println(MainServer.PORUKE_POZIVANJA_NA_IGRU+Igrac.PRIHVACENA_IGRA);
	}

	public Igra(Igrac igrac1, Igrac igrac2, boolean igrac1PocinjeIgru) {
		inicijalizuj(igrac1, igrac2);
		this.igrac1PocinjeIgru = igrac1PocinjeIgru;
		podesiRedosled(igrac1PocinjeIgru);
		igrac1.izlazniTok.println(MainServer.PORUKE_POZIVANJA_NA_IGRU+Igrac.PRIHVACENA_IGRA);
	}

	void inicijalizuj(Igrac igrac1, Igrac igrac2) {
		
		this.igrac1 = igrac1;
		this.igrac2 = igrac2;
		tabla = new Tabla();
		figure = new Figure();
		aktivnaNit=true;
//		posaljiStanje();
	}

	public void pocni() {	
		t = new Thread(this,igrac1.ime+igrac2.ime);
		t.setDaemon(true);
		
		t.start();	
		
	}

	public synchronized void run() {
		if(igrac1PocinjeIgru){
			System.out.println(igrac1);
		}else System.out.println(igrac2);
		try {
			wait(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
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
			//	igrac1.izlazniTok.println(MainServer.PORUKE_IGRE+IGRAS);
				staviFiguruNaTablu(igrac1,igrac2);
				proveriPobedu(igrac1,igrac2);
				omoguciProtivnikuDaIgra(igrac1, igrac2);
			} else {
			//	igrac2.izlazniTok.println(MainServer.PORUKE_IGRE+IGRAS);
				staviFiguruNaTablu(igrac2,igrac1);
				proveriPobedu(igrac2,igrac1);
				omoguciProtivnikuDaIgra(igrac2, igrac1);
			}
		}
			
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		igrac1.zavrsiIgru();
		igrac2.zavrsiIgru();
		igrac1.zatvoriVeze();
		igrac2.zatvoriVeze();
		MainServer.izbaciIgraca(igrac1);
		MainServer.izbaciIgraca(igrac2);
		
	}
		
	}
	void proveriPobedu(Igrac naPotezu, Igrac ceka){
		if (tabla.partijaJeZavrsena() != 0) {
			if (tabla.partijaJeZavrsena() == 1) {
				String pobednik = (naPotezu.naPotezu) ?naPotezu +POBEDA :ceka + POBEDA;
				posaljiObojiciPoruku(pobednik);
			}
			if (tabla.partijaJeZavrsena() == 2) {
				posaljiObojiciPoruku(NERESENO);
			}
			posaljiObojiciPoruku(NOVA_IGRA);
			igrac1.cekajOdgovor();
			igrac2.cekajOdgovor();
			synchronized(this){
			try {
				wait(2500);
				wait(2500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			if (igrac1.hocePonovo && igrac2.hocePonovo) {
				MainServer.napraviPonovnuIgru(this);
				return;
			}
			zavrsiIgru();
			return;
		}
	}
	void omoguciProtivnikuDaIgra(Igrac naPotezu, Igrac ceka) throws IOException{
		izaberiFiguruZaProtivnika(naPotezu, ceka);
		podesiRedosled();
	//	posaljiStanje();
	}

	public void posaljiObojiciPoruku(Object o) {
		igrac1.izlazniTok.println(MainServer.PORUKE_IGRE+o);
		igrac2.izlazniTok.println(MainServer.PORUKE_IGRE+o);
	}

//	public void posaljiStanje() {
//		posaljiObojiciPoruku(tabla);
//		posaljiObojiciPoruku(figure);
//	}

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
}
