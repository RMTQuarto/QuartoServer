public class Tabla {
	
	private static int brojPoljaNaJednojOsi = 4;
	private Figura[][] tabla;
	
	public Tabla() {
		tabla = new Figura[brojPoljaNaJednojOsi][brojPoljaNaJednojOsi];
	}
	
	public void postaviFiguruNaPolje(char boja, char oblik, char supljina, char visina, int x, int y) {
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
	
	//vraca true ako je partija zavrsena, false ako nije
	public boolean proveraPobede() {
		if(redniBrojPoteza() >= 4) {
			if(redniBrojPoteza() <= 16)
			return proveraCetiriFigure();
		}
		return false;
	}
	
	public boolean proveraCetiriFigure() {
		//glavna dijagonala
		if(tabla[0][0]!=null && tabla[1][1]!=null && tabla[2][2]!=null && tabla[3][3]!=null)
			if(tabla[0][0].equals(tabla[1][1]) && tabla[0][0].equals(tabla[2][2]) && tabla[0][0].equals(tabla[3][3]) 
					&& tabla[1][1].equals(tabla[2][2]) && tabla[1][1].equals(tabla[3][3]) && tabla[2][2].equals(tabla[3][3])) 
				return true;
		//sporedna dijagonala
		if(tabla[0][3]!=null && tabla[1][2]!=null && tabla[2][1]!=null && tabla[3][0]!=null)
			if(tabla[0][3].equals(tabla[1][2]) && tabla[0][3].equals(tabla[2][1]) && tabla[0][3].equals(tabla[3][0]) 
					&& tabla[1][2].equals(tabla[2][1]) && tabla[1][2].equals(tabla[3][0]) && tabla[2][1].equals(tabla[3][0])) 
				return true;
		//redovi
		for (int i = 0; i < brojPoljaNaJednojOsi; i++) {
			if(tabla[i][0]!=null && tabla[i][1]!=null && tabla[i][2]!=null && tabla[i][3]!=null)
				if(tabla[i][0].equals(tabla[i][1]) && tabla[i][0].equals(tabla[i][2]) && tabla[i][0].equals(tabla[i][3]) 
						&& tabla[i][1].equals(tabla[i][2]) && tabla[i][1].equals(tabla[i][3]) && tabla[i][2].equals(tabla[i][3]))
					return true;
		}
		//kolone
		for (int j = 0; j < brojPoljaNaJednojOsi; j++) {
			if(tabla[0][j]!=null && tabla[1][j]!=null && tabla[2][j]!=null && tabla[3][j]!=null)
				if(tabla[0][j].equals(tabla[1][j]) && tabla[0][j].equals(tabla[2][j]) && tabla[0][j].equals(tabla[3][j]) 
						&& tabla[1][j].equals(tabla[2][j]) && tabla[1][j].equals(tabla[3][j]) && tabla[2][j].equals(tabla[3][j]))
					return true;
		}
		return false;
	}
}
