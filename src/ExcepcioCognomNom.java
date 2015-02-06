

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class ExcepcioCognomNom extends Exception implements Serializable{  
	/** Definir el tipus d'excepció heretat */
	private static TreeMap<String, ContextExcepcio> registre;
	
	public static TreeMap<String, ContextExcepcio> getRegistre() {
		return registre;
	}

	public static void setRegistre(TreeMap<String, ContextExcepcio> registre) {
		ExcepcioCognomNom.registre = registre;
	}

	public ContextExcepcio getContextE() {
		return contextE;
	}

	public void setContextE(ContextExcepcio contextE) {
		this.contextE = contextE;
	}

	public static Map<Integer, String> getErrorscontextes() {
		return errorsContextes;
	}

	@SuppressWarnings("serial")
	private static final Map<Integer, String> errorsContextes = new HashMap<Integer, String>() {{
		put(1001, "No s'ha indicat el nom del comerciant");
		put(2001, "El preu del concert ha de ser >= 0");
		put(2002, "No es poden treure patrocinadors, encara no hi ha cap");
		put(2003, "No existeix el patrocinador que vols treure");
		put(3001, "No es pot afegir la petició, el comerciant no és correcte");
		put(3002, "Encara no hi ha cap inscripció confirmada per consultar");
		put(3003, "Encara no hi ha cap inscripció confirmada per anular");
		put(7001, "Format de data incorrecte");
		put(8001, "No s'ha pogut llegir les dades del fitxer XML");
		put(8002, "No s'ha pogut llegir les dades del fitxer CSV");
		put(8003, "No s'ha pogut llegir les dades del fitxer BIN");
		put(8004, "No s'ha pogut escriure les dades al fitxer XML");
		put(8005, "No s'ha pogut escriure les dades al fitxer CSV");
		put(8006, "No s'ha pogut escriure les dades al fitxer BIN");
		put(9999, "Error desconegut");
	}};
	
	private ContextExcepcio contextE;   
	
	
	public ExcepcioCognomNom(String classe, int codi) {
		super();
		
		if (registre == null) registre = new TreeMap<String, ContextExcepcio>(new DatesComparator());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S",new Locale("CA","ES"));
		this.contextE = new ContextExcepcio(classe, codi, this);
		
		String key = ExcepcioCognomNom.registre.size() + " " + sdf.format(new Date());
		
		ExcepcioCognomNom.registre.put("`:( $" + StringUtils.leftPad(key, 28), this.contextE); // Afegir excepció al registre
		
		System.out.println(ExcepcioCognomNom.registre.size() + "\n");
	}

	@Override
	public String getLocalizedMessage() {
		int codi = this.contextE.getCodi();
		if (!ExcepcioCognomNom.errorsContextes.containsKey(codi)) codi = 9999;
		String sms = ExcepcioCognomNom.errorsContextes.get(codi);
		
		return "[ "+StringUtils.rightPad(codi + ".-" +this.contextE.getClasse(),16)+" ] " + sms;
	}
	
	public static String informeErrors() {
		String registre = "";
		
		if (ExcepcioCognomNom.registre == null) return "";
		
		Entry<String, ContextExcepcio> aux = null;
		Set<Entry<String, ContextExcepcio>> entrades = ExcepcioCognomNom.registre.entrySet();
		
		Iterator<Entry<String, ContextExcepcio>> it = entrades.iterator();
		while (it.hasNext()) {
			aux = it.next();
			registre += StringUtils.rightPad( aux.getKey(), 15 ) + " => ";
			//registre += StringUtils.rightPad( "[" + aux.getValue().getClasse() + "] " + aux.getValue().getCodi(), 30);
			registre += aux.getValue().getExcepcio().getLocalizedMessage() + "\n";
		}
		
		return registre;
	}
}

class DatesComparator implements Serializable, Comparator<String>  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int compare(String s1, String s2) {
		return s1.compareTo(s2);
	}
}
