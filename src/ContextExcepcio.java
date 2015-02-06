import java.io.Serializable;



public class ContextExcepcio implements Serializable{
	private static final long serialVersionUID = 1L;
	private String classe;
	private int codi;
	private ExcepcioCognomNom excepcio;
	public ContextExcepcio(String classe, int codi, ExcepcioCognomNom excepcio) {
		super();
		this.classe = classe;
		this.codi = codi;
		this.excepcio = excepcio;
	}
	public String getClasse() {
		return classe;
	}
	public void setClasse(String classe) {
		this.classe = classe;
	}
	public int getCodi() {
		return codi;
	}
	public void setCodi(int codi) {
		this.codi = codi;
	}
	public ExcepcioCognomNom getExcepcio() {
		return excepcio;
	}
	public void setExcepcio(ExcepcioCognomNom excepcio) {
		this.excepcio = excepcio;
	}
	
}
