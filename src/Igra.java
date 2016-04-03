import java.io.IOException;
import java.util.Random;

public class Igra implements Runnable{
	Igrac igrac1;
	Igrac igrac2;
	Tabla tabla;
	Figure figure;
	boolean igrac1PocinjeIgru;
	
	public Igra(Igrac igrac1, Igrac igrac2){
		inicijalizuj(igrac1,igrac2);
		Random r=new Random();
		igrac1PocinjeIgru=(r.nextFloat()<0.5)?true:false;
		podesiRedosled(igrac1PocinjeIgru);
	}
	public Igra(Igrac igrac1, Igrac igrac2,boolean igrac1PocinjeIgru){
		inicijalizuj(igrac1,igrac2);
		this.igrac1PocinjeIgru=igrac1PocinjeIgru;
		podesiRedosled(igrac1PocinjeIgru);
	}
	void inicijalizuj(Igrac igrac1,Igrac igrac2){
		this.igrac1=igrac1;
		this.igrac2=igrac2;
		tabla=new Tabla();
		figure=new Figure();
		posaljiStanje();
	}
	public void pocni(){
		Thread t=new Thread(this);
		t.setDaemon(true);
		t.start();	
	}
	
	
	public void run() {
			if(igrac1.naPotezu){
				omoguciProtivnikuDaIgra(igrac1, igrac2);
			}else{
				omoguciProtivnikuDaIgra(igrac2, igrac1);
			}
		while(true){
			if(tabla.partijaJeZavrsena() != 0){
				if(tabla.partijaJeZavrsena() == 1) {
					String pobednik=(igrac1.naPotezu)?igrac2+" je pobedio":igrac1+" je pobedio";
					posaljiObojiciPoruku(pobednik);
				}
				if(tabla.partijaJeZavrsena() == 2) {
					posaljiObojiciPoruku("Nereseno");
				}
				posaljiObojiciPoruku("Nova igra?");
				//trebaju mi dve niti ovde, treba probuditi igrace
				try {
					String odg1=igrac1.ulazniTok.readLine();
					String odg2=igrac2.ulazniTok.readLine();
					if(odg1.equals(odg2) && odg1.equals("DA")){
						MainServer.napraviPonovnuIgru(this);
						return;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				zavrsiIgru();
				return;
			}
			if(igrac1.naPotezu){ 
				staviFiguruNaTablu(igrac1);
				omoguciProtivnikuDaIgra(igrac1, igrac2);
			}
			else{
				staviFiguruNaTablu(igrac2);
				omoguciProtivnikuDaIgra(igrac2, igrac1);
			}
		}
		// sale izgenerisi svima equals metode ali da vraca true samo ako je to taj isti objekat, druga poredjenja za figure napravi kao neku drugu metodu
	}
	void omoguciProtivnikuDaIgra(Igrac naPotezu,Igrac ceka){
		izaberiFiguruZaProtivnika(naPotezu,ceka);
		podesiRedosled(ceka.naPotezu);
		posaljiStanje();
	}
	public void posaljiObojiciPoruku(Object o){
		igrac1.izlazniTok.println(o);
		igrac2.izlazniTok.println(o);
	}
	public void posaljiStanje(){
		posaljiObojiciPoruku(tabla);
		posaljiObojiciPoruku(figure);
	}
	void podesiRedosled(boolean naPotezu){
		igrac1.naPotezu=naPotezu;
		igrac2.naPotezu=!naPotezu;
		
	}
	void staviFiguruNaTablu(Igrac naPotezu){
		try {
			String[] potez=naPotezu.ulazniTok.readLine().split(";");
			String figura=potez[0];
			String pozicija=potez[1];
			tabla.postaviFiguruNaPolje(figura.charAt(0),figura.charAt(1) ,figura.charAt(2),figura.charAt(3), pozicija.charAt(0), pozicija.charAt(1));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	void izaberiFiguruZaProtivnika(Igrac naPotezu,Igrac ceka){
		try {
			String figura=naPotezu.ulazniTok.readLine();
			figure.izbaciFiguru(figure.nadjiFiguru(figura));
			ceka.izlazniTok.println(figura);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void zavrsiIgru(){
		igrac1.zavrsiIgru();
		igrac2.zavrsiIgru();
		MainServer.ukiniIgru(this);
	}
}
