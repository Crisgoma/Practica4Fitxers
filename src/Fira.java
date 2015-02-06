

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Fira extends Esdeveniment implements Serializable{
	private int durada;
	
	/******  Indicar tipus de les estructures  *****************/
	
	private Queue<Comerciant> peticionsInscripcio;  /* Cua normal d'inscripcions  */
	
	private Stack<Comerciant> inscripcionsConfirmades; /* Pila de comerciants inscrits */
	
	/***********************************************************/
	
	public Fira(String descripcio, Date dia, String localitat, int durada) {
		super(descripcio, dia, localitat);
		this.durada = durada;
		this.peticionsInscripcio = new LinkedList<Comerciant>();
		this.inscripcionsConfirmades = new Stack<Comerciant>();
	}

	public Fira() {
		super();
	}

	public Queue<Comerciant> getPeticionsInscripcio() {
		return peticionsInscripcio;
	}

	public void setPeticionsInscripcio(Queue<Comerciant> peticionsInscripcio) {
		this.peticionsInscripcio = peticionsInscripcio;
	}

	public Stack<Comerciant> getInscripcionsConfirmades() {
		return inscripcionsConfirmades;
	}

	public void setInscripcionsConfirmades(Stack<Comerciant> inscripcionsConfirmades) {
		this.inscripcionsConfirmades = inscripcionsConfirmades;
	}

	public int getDurada() {
		return durada;
	}

	public void setDurada(int durada) {
		this.durada = durada;
	}
	
	public void novaPeticioInscripcio(Comerciant com) throws ExcepcioCognomNom {
		if (com == null) throw new ExcepcioCognomNom("Fira", 3001);
		peticionsInscripcio.offer(com);
	}

	public Comerciant consultarPeticioEnTramit() {
		return peticionsInscripcio.peek();
	}
	
	public Comerciant consultarUltimConfirmat() throws ExcepcioCognomNom {
		if (inscripcionsConfirmades.empty()) throw new ExcepcioCognomNom("Fira", 3002);
		return inscripcionsConfirmades.peek(); 
	}
	
	public void confirmarPeticioInscripcio() {
		Comerciant c = peticionsInscripcio.poll();
		inscripcionsConfirmades.push(c);
	}
	
	public void anularDarreraConfirmada() throws ExcepcioCognomNom {
		if (inscripcionsConfirmades.empty()) throw new ExcepcioCognomNom("Fira", 3003); 
		inscripcionsConfirmades.pop(); 
	}
}
