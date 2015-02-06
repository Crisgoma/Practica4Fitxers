

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVObjectWriter<T extends CSVable<T>> {  // extends serveix per implements també. Obliga T implementi CSVable 
	// <T> Tipus genèric vull garantir que els tipus generics hereden de CSVable
	private static final String SEPARATOR_COMMA = ",";
	private static final String SEPARATOR_PUNT_COMMA = ";";
	private PrintWriter pw;
	private FileWriter fw;
	private boolean header;
	private String separator;
	
	public CSVObjectWriter(String fitxer, boolean header, String separator) throws Exception {
		this.header = header;
		this.separator = separator;
		
		//Genera excepció si el separador no és correcte
		if (!SEPARATOR_COMMA.equals(this.separator) && 
			!SEPARATOR_PUNT_COMMA.equals(this.separator) ) 
			throw new Exception ("Separador incorrecte, només \",\" o \";\"");
		
		this.fw = new FileWriter(fitxer, false);
        this.pw = new PrintWriter(fw, true);

	}
	//T = desconegut
	public void write(T t) throws Exception {
		if (pw == null) throw new Exception ("No es pot accedir al fitxer"); 
		if (this.header == true) {
			// Escriure capçalera
			pw.println(t.csvHeader(this.separator));
			this.header = false;
		}
		pw.println(t.toCsv(this.separator)); //ejecuta metodo aunq no sepa lo que es t, porque le obligamos a implementar la interface
	}

	public void close() throws IOException {
		fw.close();
	}
}
