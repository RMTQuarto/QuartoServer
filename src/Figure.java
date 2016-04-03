import java.util.Iterator;

public class Figure {
	private Figura figura1;
	private Figura figura2;
	private Figura figura3;
	private Figura figura4;
	private Figura figura5;
	private Figura figura6;
	private Figura figura7;
	private Figura figura8;
	private Figura figura9;
	private Figura figura10;
	private Figura figura11;
	private Figura figura12;
	private Figura figura13;
	private Figura figura14;
	private Figura figura15;
	private Figura figura16;
	
	private Figura[] figure;
	
	public Figure() {
		figura1 = new Figura('C', 'K', 'S', 'D');
		figura2 = new Figura('C', 'K', 'S', 'N');
		figura3 = new Figura('C', 'K', 'P', 'D');
		figura4 = new Figura('C', 'K', 'P', 'N');
		figura5 = new Figura('C', 'V', 'S', 'D');
		figura6 = new Figura('C', 'V', 'S', 'N');
		figura7 = new Figura('C', 'V', 'P', 'D');
		figura8 = new Figura('C', 'v', 'P', 'N');
		figura9 = new Figura('B', 'K', 'S', 'D');
		figura10 = new Figura('B', 'K', 'S', 'N');
		figura11 = new Figura('B', 'K', 'P', 'D');
		figura12 = new Figura('B', 'K', 'P', 'N');
		figura13 = new Figura('B', 'V', 'S', 'D');
		figura14 = new Figura('B', 'V', 'S', 'N');
		figura15 = new Figura('B', 'V', 'P', 'D');
		figura16 = new Figura('B', 'V', 'P', 'N');
		
		figure = new Figura[16];
		
		figure[0] = figura1;
		figure[1] = figura2;
		figure[2] = figura3;
		figure[3] = figura4;
		figure[4] = figura5;
		figure[5] = figura6;
		figure[6] = figura7;
		figure[7] = figura8;
		figure[8] = figura9;
		figure[9] = figura10;
		figure[10] = figura11;
		figure[11] = figura12;
		figure[12] = figura13;
		figure[13] = figura14;
		figure[14] = figura15;
		figure[15] = figura16;
	}
	
	public void izbaciFiguru(Figura figura) {
		for (int i = 0; i < figure.length; i++) {
			if(figure[i].equals(figura)) {
				figure[i] = null;
				break;
			}
		}
	}
	@Override
	public String toString() {
		String tekst="";
		for (Figura figura : figure) {
			if(figura!=null)
				tekst+=figura+";";
		}
		return tekst;
	}
	public Figura nadjiFiguru(String tekst){
		for (Figura figura : figure) {
			if(figura.toString().equals(tekst)) return figura;
		}
		return null;
	}
}
