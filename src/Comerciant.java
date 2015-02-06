import java.io.Serializable;



public class Comerciant implements Serializable{
	private String nom;
	private boolean restauracio;
	
	public Comerciant(String nom, boolean restauracio) throws ExcepcioCognomNom {
		super(); 
		
		if (nom == null || "".equals(nom.trim())) throw new ExcepcioCognomNom("Comerciant", 1001);  
		
		this.nom = nom;
		this.restauracio = restauracio;
	}

	public Comerciant() {
		super();
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) throws ExcepcioCognomNom {
		if (nom == null || "".equals(nom.trim())) throw new ExcepcioCognomNom("Comerciant", 1001);
		this.nom = nom;
	}

	public boolean isRestauracio() {
		return restauracio;
	}

	public void setRestauracio(boolean restauracio) {
		this.restauracio = restauracio;
	}

	@Override
	public String toString() {
		return "Comerciant [nom=" + nom + ", restauracio=" + restauracio + "]";
	}

	
	
}
