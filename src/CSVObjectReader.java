

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVObjectReader <T extends CSVable<T>> {
	private BufferedReader br;
	private FileReader fr;
	//private boolean header;
	private String separator;
	private String primerFila;
	
	public CSVObjectReader(String fitxer, Class<T> classe) throws Exception { //separador ";" o ","
		super(); //Class<T> classe = indicador de classe. no es un objecte! 
		File file = new File(fitxer);        

		try {
			this.fr = new FileReader (file);
			this.br = new BufferedReader(fr); // Inicialitza buffer amb l’entrada 
		} catch (FileNotFoundException e) {
			throw new Exception ("No existeix el fitxer");
		}	
		
		this.primerFila = "";
		this.separator = "";

		String primerFila = this.br.readLine(); //Lectura String
		
		T t = classe.newInstance(); //instancio la clase que le entra
		
		this.separator = t.getCsvSeparator(primerFila);
		
		if ("".equals(this.separator)) throw new Exception("El separador no és correcte");
		
		if (t.checkCsvHeader(primerFila, separator)) {
			primerFila = "";
		}
	}
	
	public T read(Class<T> classe) throws Exception {  //detecta si té capçalera i el separador automàticament
		// Genera excepció si el format no és correcte
		T t = classe.newInstance();
		
		String registre;
		if (!"".equals(this.primerFila) ) {
			registre = this.primerFila;
			this.primerFila = "";
		} else {
			registre = this.br.readLine(); //Lectura String
		}
		
		if (registre == null) return null;

		return t.fromCsv(registre, separator);
	}

	public void close() throws IOException {
		fr.close();
	}
}
