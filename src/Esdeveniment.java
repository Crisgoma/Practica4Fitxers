

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class Esdeveniment implements Comparable<Esdeveniment>, Serializable {
	// Sense repetits. Ordenat per dia, localitat i descripció
	protected String descripcio;
	protected Date dia;
	protected String localitat;
	
	public Esdeveniment(String descripcio, Date dia, String localitat) {
		super();
		this.descripcio = descripcio;
		this.dia = dia;
		this.localitat = localitat;
	}
	
	
	public Esdeveniment() {
		super();
	}


	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public Date getDia() {
		return dia;
	}

	public void setDia(Date dia) {
		this.dia = dia;
	}

	public String getLocalitat() {
		return localitat;
	}

	public void setLocalitat(String localitat) {
		this.localitat = localitat;
	}

	public int compareTo(Esdeveniment e) {
		// Sense repetits. Ordenat per dia, localitat i descripció
		if (this.dia.compareTo(e.dia) != 0) return  this.dia.compareTo(e.dia);
		if (this.localitat.compareTo(e.localitat) != 0) return  this.localitat.compareTo(e.localitat);
		return this.descripcio.compareTo(e.descripcio);
	}
	
	public String infoEsdeveniment() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S",new Locale("CA","ES"));
		
		return StringUtils.center(sdf.format(this.dia), 10) + "\t" + 
		StringUtils.rightPad(StringUtils.abbreviate(this.localitat, 15), 15)  
		+ "\t" + this.descripcio + "\n";	
	}	
}
