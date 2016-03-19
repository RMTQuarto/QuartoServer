public class Igra implements Runnable{
	Igrac igrac1;
	Igrac igrac2;
	Tabla tabla;
	Figure figure;
	public Igra(Igrac igrac1, Igrac igrac2){
		this.igrac1=igrac1;
		this.igrac2=igrac2;
		tabla=new Tabla();
		figure=new Figure();
		posaljiStanje();
	}
	public void run() {
		// TODO Auto-generated method stub
		
	}
	public void posaljiStanje(){
		igrac1.izlazniTok.println(tabla);
		igrac2.izlazniTok.println(tabla);
		igrac1.izlazniTok.println(figure);
		igrac2.izlazniTok.println(figure);
	}
	
}
