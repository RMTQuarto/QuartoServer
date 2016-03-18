public class Figura {
	
	private char boja; //C - crna, B - bela
	private char oblik; //K - kockast, V - valjkast
	private char supljina; //S - supljina, P - puna
	private char visina; //V - visok, N - nizak
	
	public Figura(char boja, char oblik, char supljina, char visina) {
		this.boja = boja;
		this.oblik = oblik;
		this.supljina = supljina;
		this.visina = visina; 
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Figura figura = (Figura) obj;
		if(boja == figura.boja || oblik == figura.oblik 
				|| supljina == figura.supljina || visina == figura.visina)
			return true;
		return false;
	}
	
	
}