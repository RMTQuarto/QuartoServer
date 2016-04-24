public class Figura {
	
	private char boja; //C - crna, B - bela
	private char oblik; //K - kockast, V - valjkast
	private char supljina; //S - supljina, P - puna
	private char visina; //D - dug, N - nizak
	
	public Figura(char boja, char oblik, char supljina, char visina) {
		this.boja = boja;
		this.oblik = oblik;
		this.supljina = supljina;
		this.visina = visina; 
	}
	
	public char getBoja() {
		return boja;
	}

	public char getOblik() {
		return oblik;
	}

	public char getSupljina() {
		return supljina;
	}

	public char getVisina() {
		return visina;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Figura other = (Figura) obj;
		if (boja != other.boja)
			return false;
		if (oblik != other.oblik)
			return false;
		if (supljina != other.supljina)
			return false;
		if (visina != other.visina)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return boja+oblik+supljina+visina+"";
	}
	
}
