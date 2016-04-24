public class Tabla {
	
	private static int brojPoljaNaJednojOsi = 4;
	private Figura[][] tabla;
	
	public Tabla() {
		tabla = new Figura[brojPoljaNaJednojOsi][brojPoljaNaJednojOsi];
	}
	
	public void postaviFiguruNaPolje(char boja, char oblik, char supljina, char visina, char xC, char yC) {
		int x=xC-48;
		int y=yC-48;
		if(tabla[x][y] != null) return;
		Figura figura = new Figura(boja, oblik, supljina, visina);
		tabla[x][y] = figura;
	}
	
	public int redniBrojPoteza() {
		int redniBrojPoteza = 0;
		for (int i = 0; i < brojPoljaNaJednojOsi; i++) {
			for (int j = 0; j < brojPoljaNaJednojOsi; j++) {
				if(tabla[i][j] != null) {
					redniBrojPoteza++;
				}
			}
		}
		return redniBrojPoteza;
	}
	
	//vraca 0 ako nije zavrsena, 1 ako je zavrsena pobedom, 2 ako je nereseno
	public int partijaJeZavrsena() {
		if(redniBrojPoteza() >= 4){
			if(redniBrojPoteza() <= 16 && proveraCetiriFigure() == 1)
				return 1;
			if(redniBrojPoteza() == 16 && proveraCetiriFigure() == 0)
				return 2;
		}
		return 0;
	}
	public boolean jednakiPoBoji(Figura f1, Figura f2, Figura f3, Figura f4) {
		if(f1.getBoja() == f2.getBoja() && f2.getBoja() == f3.getBoja() && f3.getBoja() == f4.getBoja())
			return true;
		return false;
	}
	public boolean jednakiPoObliku(Figura f1, Figura f2, Figura f3, Figura f4) {
		if(f1.getOblik() == f2.getOblik() && f2.getOblik() == f3.getOblik() && f3.getOblik() == f4.getOblik())
			return true;
		return false;
	}
	public boolean jednakiPoVisini(Figura f1, Figura f2, Figura f3, Figura f4) {
		if(f1.getVisina() == f2.getVisina() && f2.getVisina() == f3.getVisina() && f3.getVisina() == f4.getVisina())
			return true;
		return false;
	}
	public boolean jednakiPoSupljini(Figura f1, Figura f2, Figura f3, Figura f4) {
		if(f1.getSupljina() == f2.getSupljina() && f2.getSupljina() == f3.getSupljina() && f3.getSupljina() == f4.getSupljina())
			return true;
		return false;
	}
	public int proveraCetiriFigure() {
		//glavna dijagonala
		boolean poBoji;
		boolean poObliku;
		boolean poVisini;
		boolean poSupljini;
		if(tabla[0][0]!=null && tabla[1][1]!=null && tabla[2][2]!=null && tabla[3][3]!=null){
			poBoji = jednakiPoBoji(tabla[0][0], tabla[1][1], tabla[2][2], tabla[3][3]);
			poVisini = jednakiPoVisini(tabla[0][0], tabla[1][1], tabla[2][2], tabla[3][3]);
			poObliku = jednakiPoObliku(tabla[0][0], tabla[1][1], tabla[2][2], tabla[3][3]);
			poSupljini = jednakiPoSupljini(tabla[0][0], tabla[1][1], tabla[2][2], tabla[3][3]);
			if(poBoji || poVisini || poObliku || poSupljini)
				return 1;
			poBoji = false; poObliku = false; poVisini = false; poSupljini = false;
		}
		//sporedna dijagonala
		if(tabla[0][3]!=null && tabla[1][2]!=null && tabla[2][1]!=null && tabla[3][0]!=null) {
			poBoji = jednakiPoBoji(tabla[0][3], tabla[1][2], tabla[2][1], tabla[3][0]);
			poVisini = jednakiPoVisini(tabla[0][3], tabla[1][2], tabla[2][1], tabla[3][0]);
			poObliku = jednakiPoObliku(tabla[0][3], tabla[1][2], tabla[2][1], tabla[3][0]);
			poSupljini = jednakiPoSupljini(tabla[0][3], tabla[1][2], tabla[2][1], tabla[3][0]);
			if(poBoji || poVisini || poObliku || poSupljini)
				return 1;
			poBoji = false; poObliku = false; poVisini = false; poSupljini = false;
		}
		//redovi
		for (int i = 0; i < brojPoljaNaJednojOsi; i++) {
			if(tabla[i][0]!=null && tabla[i][1]!=null && tabla[i][2]!=null && tabla[i][3]!=null) {
				poBoji = jednakiPoBoji(tabla[i][0], tabla[i][1], tabla[i][2], tabla[i][3]);
				poVisini = jednakiPoVisini(tabla[i][0], tabla[i][1], tabla[i][2], tabla[i][3]);
				poObliku = jednakiPoObliku(tabla[i][0], tabla[i][1], tabla[i][2], tabla[i][3]);
				poSupljini = jednakiPoSupljini(tabla[i][0], tabla[i][1], tabla[i][2], tabla[i][3]);
				if(poBoji || poVisini || poObliku || poSupljini)
					return 1;
				poBoji = false; poObliku = false; poVisini = false; poSupljini = false;
			}
		}
		//kolone
		for (int j = 0; j < brojPoljaNaJednojOsi; j++) {
			if(tabla[0][j]!=null && tabla[1][j]!=null && tabla[2][j]!=null && tabla[3][j]!=null) {
				poBoji = jednakiPoBoji(tabla[0][j], tabla[1][j], tabla[2][j], tabla[3][j]);
				poVisini = jednakiPoVisini(tabla[0][j], tabla[1][j], tabla[2][j], tabla[3][j]);
				poObliku = jednakiPoObliku(tabla[0][j], tabla[1][j], tabla[2][j], tabla[3][j]);
				poSupljini = jednakiPoSupljini(tabla[0][j], tabla[1][j], tabla[2][j], tabla[3][j]);
				if(poBoji || poVisini || poObliku || poSupljini)
					return 1;
				poBoji = false; poObliku = false; poVisini = false; poSupljini = false;
			}
		}
		return 0;
	}
	@Override
	public String toString() {
		String tekst="";
		for (int i = 0; i < brojPoljaNaJednojOsi; i++) {
			for (int j = 0; j < brojPoljaNaJednojOsi; j++) {
				if(tabla[i][j]!=null){
					tekst+=i+j+tabla[i][j].toString()+";";
				}
			}
		}
		return tekst;
	}
}
