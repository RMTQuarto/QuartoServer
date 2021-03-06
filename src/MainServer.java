import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class MainServer {
	public static final int BROJ_PORTA=9999;
	public static final String PORUKE_KONEKTOVANJA="K;";
	public static final String PORUKE_IGRE="I;";
	public static final String PORUKE_POZIVANJA_NA_IGRU="P;";
	public static final String PUN_SERVER="SERVER PUN";
	public static final String LISTA="L;";
	
	public static Igra[] igre = new Igra[5];
	public static LinkedList<Igrac> igraci=new LinkedList<Igrac>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			ServerSocket soketZaOsluskivanje = new ServerSocket(MainServer.BROJ_PORTA);
			while (true) {
				Socket soket = soketZaOsluskivanje.accept();
				new Igrac(soket);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void posaljiListuSlobodnihIgraca(){
		for (Igrac igrac : igraci) {
			igrac.getIzlazniTok().println(listaSlobodnihIgraca());
		}
		
	}
	private static String listaSlobodnihIgraca(){
		String lista=LISTA;
		for(Igrac igrac : igraci) {
			lista+=igrac+";";
		}
		return lista;
	}
	public static void posaljiPozivnicu(String pozivac, String protivnik){
		nadjiIgraca(protivnik).getIzlazniTok().println(PORUKE_POZIVANJA_NA_IGRU+pozivac);
	}
	
	public static void napraviIgru(String igrac1, String igrac2){
		Igrac igr1=nadjiIgraca(igrac1);
		Igrac igr2=nadjiIgraca(igrac2);
		for (int i = 0; i < igre.length; i++) {
			if(igre[i]==null){				
				igre[i]=new Igra(igr1,igr2);
				uvediIgraceUIgru(igre[i],igr1,igr2);
				posaljiListuSlobodnihIgraca();
				igre[i].pocni();
				break;
			}
			if(i==igre.length-1){
				igr1.getIzlazniTok().println(PORUKE_POZIVANJA_NA_IGRU+PUN_SERVER);
				igr2.getIzlazniTok().println(PORUKE_POZIVANJA_NA_IGRU+PUN_SERVER);
			}
		}
	}
	public static void napraviPonovnuIgru(Igra igra){
		for (int i = 0; i < igre.length; i++) {
			if(igre[i]!=null && igre[i].equals(igra)){
				igre[i]=new Igra(igra.getIgrac1(),igra.getIgrac2(),!igra.isIgrac1PocinjeIgru());
				igra.getIgrac1().igraj();
				igra.getIgrac2().igraj();
				igre[i].pocni();
				break;
			}
		}
	}
	private static void uvediIgraceUIgru(Igra igra,Igrac igr1,Igrac igr2){		
		igr1.igraj();
		igr2.igraj();
		igr1.setIgra(igra);	
		igr2.setIgra(igra);
		igraci.remove(igr1);
		igraci.remove(igr2);	
	}
	public static Igrac nadjiIgraca(String ime){
		for (Igrac igrac : igraci) {
			if(igrac.getIme().equals(ime)){
				return igrac;
			}
		}
		return null;
	}
	public static void ukiniIgru(Igra igra){
		igraci.add(igra.getIgrac1());
		igraci.add(igra.getIgrac2());
		for (int i = 0; i < igre.length; i++) {
			if(igre[i]!=null && igre[i].equals(igra)){
				igre[i]=null;
			}
		}
	}
	public static void izbaciIgraca(Igrac igrac){
		igraci.remove(igrac);
		posaljiListuSlobodnihIgraca();
	}
}
