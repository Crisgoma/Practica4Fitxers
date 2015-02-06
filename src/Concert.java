

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

public class Concert extends Esdeveniment implements CSVable<Concert> {
	/* Sense repetits. Per grup  */
	private String grup;
	private double preu;
	private int hora;
	public static final String[] HEADER_FIELDS = { "dia", "localitat", "descripcio", "grup", "preu", "hora", "patro. restauracio", "patro. no restauracio"};
	/******  Indicar tipus d'estructures *****************/

	private LinkedList<Comerciant> patrocinadors; /* Llista normal */

	/*****************************************************/

	public Concert(String descripcio, Date dia, String localitat, String grup, double preu, int hora) throws ExcepcioCognomNom {
		super(descripcio, dia, localitat);
		if (preu < 0) throw new ExcepcioCognomNom("Concert", 2001);  		
		this.grup = grup;
		this.preu = preu;
		this.hora = hora;
		this.patrocinadors = new LinkedList<Comerciant>();
	}

	
	
	public Concert() {
		super();
	}



	public String getGrup() {
		return grup;
	}

	public void setGrup(String grup) {
		this.grup = grup;
	}

	public double getPreu() {
		return preu;
	}

	public void setPreu(double preu) throws ExcepcioCognomNom {
		if (preu < 0) throw new ExcepcioCognomNom("Concert", 2001);  
		this.preu = preu;
	}

	public int getHora() {
		return hora;
	}

	public void setHora(int hora) {
		this.hora = hora;
	}

	public void afegirPatrocinadorAlFinal(Comerciant c) {
		patrocinadors.add(c);
	}

	public void afegirPatrocinadorIniciLlista(Comerciant c) {
		patrocinadors.addFirst(c);
	}

	public void treurePatrocinador(Comerciant c) throws ExcepcioCognomNom {
		if (patrocinadors.isEmpty()) throw new ExcepcioCognomNom("Concert", 2002);
		if (!patrocinadors.contains(c)) throw new ExcepcioCognomNom("Concert", 2003);
		patrocinadors.remove(c);
	}

	public void treurePatrocinador(int posicio) throws ExcepcioCognomNom {
		if (patrocinadors.isEmpty()) throw new ExcepcioCognomNom("Concert", 2002);

		try {
			patrocinadors.remove(posicio);
		} catch (IndexOutOfBoundsException e) {
			throw new ExcepcioCognomNom("Concert", 2003);
		}
	}

	public String patrocinadorsUltimAlPrimer() {
		String llista = "";
		Iterator<Comerciant> descend = patrocinadors.descendingIterator();

		while (descend.hasNext()) {
			llista += descend.next().getNom() + "\n";
		}
		return llista;
	}

	public void ordenaPatrocinadorsPerNom() {
		Collections.sort(patrocinadors, new ComerciantComparator());
	}

	public int hashCode() {
		return grup.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;

		Concert other = (Concert) obj;
		if (obj == null || 
				getClass() != obj.getClass() ||
				grup == null || 
				!grup.equals(other.grup)) return false;

		return true;
	}

	@Override
	public String toCsv(String separator) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String csv="";
		String patroRest="", patroNoRest="";
		csv += sdf.format(this.dia) + separator + this.localitat + separator + this.descripcio
				+ separator + this.grup + separator + this.preu + separator + this.hora
				+ separator;

		Iterator iter = this.patrocinadors.iterator();
		while (iter.hasNext()) {
			Comerciant auxiliar = (Comerciant) iter.next();
			if(auxiliar.isRestauracio()){
				patroRest += auxiliar.getNom() +",";
			}else{
				patroNoRest += auxiliar.getNom()+",";
			}
		}
		
		if(patroRest==""){
			patroRest="--";
		}
		
		if(patroNoRest==""){
			patroNoRest="--";
		}
		csv += patroRest + separator + patroNoRest;



		return csv;
	}

	@Override
	public String csvHeader(String separator) {
		return StringUtils.join(HEADER_FIELDS, separator); 
	}

	@Override
	public Concert fromCsv(String registre, String separator) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String[] fields = StringUtils.split(registre, separator);
		
		Concert concert = null;
		
		try {
			if (fields.length != Concert.HEADER_FIELDS.length) throw new Exception("Format incorrecte 1");

			Date dia = sdf.parse(fields[0]);
			String loc = fields[1];
			String descripcio = fields[2];
			String grup = fields[3];
			double preu = Double.parseDouble(fields[4]);
			int hora = Integer.parseInt(fields[5]);
			concert = new Concert(descripcio, dia, loc, grup, preu, hora);
			String [] patrorest = StringUtils.split(fields[6], ",");
			
			for(int i = 0; i<patrorest.length; i++){
				Comerciant com = new Comerciant(patrorest[i], true );
				concert.afegirPatrocinadorAlFinal(com);
				
				/*Iterator<Comerciant> it = patrocinadors.iterator();
				while (it.hasNext()){
					System.out.println("Hay esto" + it.next().getNom() + "\n");
				}*/
			}
			
			String [] patronorest = StringUtils.split(fields[7], ",");
			
			for(int j = 0; j<patronorest.length; j++){
				Comerciant com2 = new Comerciant(patronorest[j], false);
				concert.afegirPatrocinadorAlFinal(com2);
			}
	
		} catch (NumberFormatException e) {
			 throw new Exception("Format incorrecte 3");
		}

		return concert;
		
		
	}

	@Override
	public boolean checkCsvHeader(String header, String separator) {
		if (header == null) return false;
		return header.equals(this.csvHeader(separator));		
	}

	@Override
	public String getCsvSeparator(String registre) {
		if (StringUtils.split(registre, ";").length == Concert.HEADER_FIELDS.length) return ";";
		
		if (StringUtils.split(registre, ",").length == Concert.HEADER_FIELDS.length) return ",";
			
		return "";
	}

	@Override
	public String toString() {
		return "Concert [grup=" + grup + ", preu=" + preu + ", hora=" + hora
				+ ", patrocinadors=" + patrocinadors + "]";
	}
	
	

}


class ComerciantComparator implements Comparator<Comerciant> {
	public int compare(Comerciant c1, Comerciant c2) {
		return c1.getNom().compareTo(c2.getNom());
	}
}