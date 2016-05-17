import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Igra implements Runnable {
	public static final String NERESENO = "NERESENO";
	public static final String POBEDA = " JE POBEDIO";
	public static final String NOVA_IGRA = "NOVA IGRA?";
	public static final String IGRAS = "IGRAS";
	public static final String NEMA_IGRE = "NEMA IGRE";
	public static final String POCELA_IGRA = "POCELA IGRA";
	
	
	
	private Igrac igrac1;
	private Igrac igrac2;
	private Tabla tabla;
	private Figure figure;
	private boolean igrac1PocinjeIgru;
	private Thread t;
	private volatile boolean aktivnaNit;

	
	
	public Igra(Igrac igrac1, Igrac igrac2) {
		inicijalizuj(igrac1, igrac2);
		igrac1.getIzlazniTok().println(MainServer.PORUKE_POZIVANJA_NA_IGRU + Igrac.PRIHVACENA_IGRA);
		Random r = new Random();
		igrac1PocinjeIgru = (r.nextFloat() < 0.5) ? true : false;
		podesiRedosled(igrac1PocinjeIgru);
	}

	public Igra(Igrac igrac1, Igrac igrac2, boolean igrac1PocinjeIgru) {
		inicijalizuj(igrac1, igrac2);
		this.igrac1PocinjeIgru = igrac1PocinjeIgru;
		podesiRedosled(igrac1PocinjeIgru);
	}

	private void inicijalizuj(Igrac igrac1, Igrac igrac2) {
		this.igrac1 = igrac1;
		this.igrac2 = igrac2;
		igrac1.setHocePonovo(null);
		igrac2.setHocePonovo(null);;
		tabla = new Tabla();
		figure = new Figure();
		aktivnaNit = true;
	}

	public Igrac getIgrac1() {
		return igrac1;
	}

	public Igrac getIgrac2() {
		return igrac2;
	}

	public boolean isIgrac1PocinjeIgru() {
		return igrac1PocinjeIgru;
	}

	public void pocni() {
		t = new Thread(this, igrac1.getIme() + igrac2.getIme());
		t.setDaemon(true);
		t.start();

	}

	public synchronized void run() {
//		// debug na serveru
//		if (igrac1PocinjeIgru) {
//			System.out.println(igrac1);
//		} else
//			System.out.println(igrac2);

		try {
			// cekanje zbog preklapanja poruka
			wait(200);
			posaljiObojiciPoruku(POCELA_IGRA);
			wait(200);
			if (igrac1.isNaPotezu()) {
				igrac1.getIzlazniTok().println(MainServer.PORUKE_IGRE + IGRAS);
				omoguciProtivnikuDaIgra(igrac1, igrac2);
			} else {
				igrac2.getIzlazniTok().println(MainServer.PORUKE_IGRE + IGRAS);
				omoguciProtivnikuDaIgra(igrac2, igrac1);
			}

			while (true) {

				if (aktivnaNit) {
					if (igrac1.isNaPotezu()) {

						staviFiguruNaTablu(igrac1, igrac2);
						proveriPobedu(igrac1, igrac2);
						omoguciProtivnikuDaIgra(igrac1, igrac2);
					} else {

						staviFiguruNaTablu(igrac2, igrac1);
						proveriPobedu(igrac2, igrac1);
						omoguciProtivnikuDaIgra(igrac2, igrac1);
					}
				}

			}
		}catch(ArrayIndexOutOfBoundsException e){
			zavrsiIgru();
			igrac1.zatvoriVeze();
			igrac2.zatvoriVeze();
			MainServer.izbaciIgraca(igrac1);
			MainServer.izbaciIgraca(igrac2);
		} 
		catch(NullPointerException e){
			zavrsiIgru();
			igrac1.zatvoriVeze();
			igrac2.zatvoriVeze();
			MainServer.izbaciIgraca(igrac1);
			MainServer.izbaciIgraca(igrac2);
		}catch (IOException e) {
			igrac1.zavrsiIgru();
			igrac2.zavrsiIgru();
			igrac1.zatvoriVeze();
			igrac2.zatvoriVeze();
			MainServer.izbaciIgraca(igrac1);
			MainServer.izbaciIgraca(igrac2);
		} catch (KrajIgreException e) {
			return;
		} catch (InterruptedException e) {
			return;
		}

	}

	private void proveriPobedu(Igrac naPotezu, Igrac ceka) throws KrajIgreException, InterruptedException {
		if (tabla.partijaJeZavrsena() != 0) {
			if (tabla.partijaJeZavrsena() == 1) {
				String pobednik = (naPotezu.isNaPotezu()) ? naPotezu.toString() : ceka.toString();

				posaljiObojiciPoruku(pobednik + POBEDA);
			}
			if (tabla.partijaJeZavrsena() == 2) {
				posaljiObojiciPoruku(NERESENO);
			}
			// cekanje zbog preklapanja poruka
			wait(200);

			posaljiObojiciPoruku(NOVA_IGRA);

			igrac1.cekajOdgovor();
			igrac2.cekajOdgovor();

//			synchronized(this){
//				wait();
//			}
//			synchronized(this){
//				wait();
//			}
			while(igrac1.getHocePonovo()==null || igrac2.getHocePonovo()==null){
				
			}

			
			if (igrac1.getHocePonovo().booleanValue() && igrac2.getHocePonovo().booleanValue()) {
				MainServer.napraviPonovnuIgru(this);
				throw new KrajIgreException("napravili novu igru");
			}
			posaljiObojiciPoruku(NEMA_IGRE);
			zavrsiIgru();
			throw new KrajIgreException("ne prave igru");
		}
	}

	private void omoguciProtivnikuDaIgra(Igrac naPotezu, Igrac ceka) throws IOException {
		izaberiFiguruZaProtivnika(naPotezu, ceka);
		podesiRedosled();

	}

	private void posaljiObojiciPoruku(Object o) {
		igrac1.getIzlazniTok().println(MainServer.PORUKE_IGRE + o);
		igrac2.getIzlazniTok().println(MainServer.PORUKE_IGRE + o);
	}

	private void podesiRedosled(boolean naPotezu) {
		igrac1.setNaPotezu(naPotezu);
		igrac2.setNaPotezu(!naPotezu);
	}

	private void podesiRedosled() {
		podesiRedosled(!igrac1.isNaPotezu());
	}

	private void staviFiguruNaTablu(Igrac naPotezu, Igrac ceka) throws IOException {
		String potezS = naPotezu.getUlazniTok().readLine();
		String[] potez = potezS.split(";");
		String figura = potez[0];
		String pozicija = potez[1];
		tabla.postaviFiguruNaPolje(figura.charAt(0), figura.charAt(1), figura.charAt(2), figura.charAt(3),
				pozicija.charAt(0), pozicija.charAt(1));
		ceka.getIzlazniTok().println(MainServer.PORUKE_IGRE + potezS);
	}

	private void izaberiFiguruZaProtivnika(Igrac naPotezu, Igrac ceka) throws IOException {

		String figura = naPotezu.getUlazniTok().readLine();
		figure.izbaciFiguru(figure.nadjiFiguru(figura));
		ceka.getIzlazniTok().println(MainServer.PORUKE_IGRE + figura);

	}

	private void zavrsiIgru() {
		igrac1.zavrsiIgru();
		igrac2.zavrsiIgru();
		MainServer.ukiniIgru(this);
	}

	private class KrajIgreException extends RuntimeException {

		public KrajIgreException(String poruka) {
			super(poruka);
		}
	}
}
