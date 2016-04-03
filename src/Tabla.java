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
	
	public int proveraCetiriFigure() {
		//glavna dijagonala
		if(tabla[0][0]!=null && tabla[1][1]!=null && tabla[2][2]!=null && tabla[3][3]!=null)
			if(tabla[0][0].isteFigure(tabla[1][1]) && tabla[0][0].isteFigure(tabla[2][2]) && tabla[0][0].isteFigure(tabla[3][3]) 
					&& tabla[1][1].isteFigure(tabla[2][2]) && tabla[1][1].isteFigure(tabla[3][3]) && tabla[2][2].isteFigure(tabla[3][3])) 
				return 1;
		//sporedna dijagonala
		if(tabla[0][3]!=null && tabla[1][2]!=null && tabla[2][1]!=null && tabla[3][0]!=null)
			if(tabla[0][3].isteFigure(tabla[1][2]) && tabla[0][3].isteFigure(tabla[2][1]) && tabla[0][3].isteFigure(tabla[3][0]) 
					&& tabla[1][2].isteFigure(tabla[2][1]) && tabla[1][2].isteFigure(tabla[3][0]) && tabla[2][1].isteFigure(tabla[3][0])) 
				return 1;
		//redovi
		for (int i = 0; i < brojPoljaNaJednojOsi; i++) {
			if(tabla[i][0]!=null && tabla[i][1]!=null && tabla[i][2]!=null && tabla[i][3]!=null)
				if(tabla[i][0].isteFigure(tabla[i][1]) && tabla[i][0].isteFigure(tabla[i][2]) && tabla[i][0].isteFigure(tabla[i][3]) 
						&& tabla[i][1].isteFigure(tabla[i][2]) && tabla[i][1].isteFigure(tabla[i][3]) && tabla[i][2].isteFigure(tabla[i][3]))
					return 1;
		}
		//kolone
		for (int j = 0; j < brojPoljaNaJednojOsi; j++) {
			if(tabla[0][j]!=null && tabla[1][j]!=null && tabla[2][j]!=null && tabla[3][j]!=null)
				if(tabla[0][j].isteFigure(tabla[1][j]) && tabla[0][j].isteFigure(tabla[2][j]) && tabla[0][j].isteFigure(tabla[3][j]) 
						&& tabla[1][j].isteFigure(tabla[2][j]) && tabla[1][j].isteFigure(tabla[3][j]) && tabla[2][j].isteFigure(tabla[3][j]))
					return 1;
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
