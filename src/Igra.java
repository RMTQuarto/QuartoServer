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
	public Igra(Igrac igrac1, Igrac igrac2,boolean igrac1PocinjeIgru){
		inicijalizuj(igrac1,igrac2);
		this.igrac1PocinjeIgru=igrac1PocinjeIgru;
		podesiRedosled(igrac1PocinjeIgru);
	}
	public void run() {
			if(igrac1.naPotezu){
				izaberiFiguruZaProtivnika(igrac1,igrac2);
				podesiRedosled(igrac2.naPotezu);
				posaljiStanje();
			}else{
				izaberiFiguruZaProtivnika(igrac2,igrac1);
				podesiRedosled(igrac1.naPotezu);
				posaljiStanje();
			}
		while(true){
			if(tabla.proveraPobede()){
				zavrsiIgru();
				return;
			}
			if(igrac1.naPotezu){ 
				staviFiguruNaTablu(igrac1);
				izaberiFiguruZaProtivnika(igrac1,igrac2);
				podesiRedosled(igrac2.naPotezu);
				posaljiStanje();
			}
			else{
				staviFiguruNaTablu(igrac2);
				izaberiFiguruZaProtivnika(igrac1, igrac2);
				podesiRedosled(igrac1.naPotezu);
				posaljiStanje();
			}
		}
		
	}
	public void posaljiStanje(){
		igrac1.izlazniTok.println(tabla);
		igrac2.izlazniTok.println(tabla);
		igrac1.izlazniTok.println(figure);
		igrac2.izlazniTok.println(figure);
	}
	void podesiRedosled(boolean igraPrvi){
		if(igraPrvi){
			igrac1.naPotezu=true;
			igrac2.naPotezu=false;
		}else{
			igrac1.naPotezu=false;
			igrac2.naPotezu=true;
		}
	}
	void staviFiguruNaTablu(Igrac naPotezu){
		try {
			String figura=naPotezu.ulazniTok.readLine();
			String pozicija=naPotezu.ulazniTok.readLine();
			tabla.postaviFiguruNaPolje(figura.charAt(0),figura.charAt(1) ,figura.charAt(2),figura.charAt(3), pozicija.charAt(0), pozicija.charAt(1));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	//treba izmeniti poruke od klijenta i od servera
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
		
	}
}
