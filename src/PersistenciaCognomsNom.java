
//a cada execucio es van acumulant els errors anteriors
//bin => registre errors => serializable
//xml => si existeix el fitxer, carrego. Si no, no els he guardat mai, agafar estructures i guardar-les
//csv => concerts 
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;


public class PersistenciaCognomsNom {
	private SimpleDateFormat sdf; 
	private TreeSet<Esdeveniment> esdeveniments;  // Sense repetits. Ordenat per dia, localitat i descripci√≥
	private HashSet<Concert> concerts;	/* Sense repetits. Un concert √©s repetit si actua el mateix grup  */
	private LinkedList<Comerciant> comerciants;
	private final static String XMLPATH = "cognomsNom.xml"; // Esdeveniments
	private final static String CSVPATH = "cognomsNom.csv"; // dia, localitat, descripcio, grup, preu, hora, patro. restauracio, patro. no restauracio
	private final static String BINPATH = "cognomsNom.bin"; // Registre acumulat. llegir a l'inici


	public void init(){
		sdf = new SimpleDateFormat("yyyy/MM/dd",new Locale("CA","ES"));

		boolean desar = false;

		// Tractament registre errors. Carrega inicial
		System.out.println("::Carregar registre inicials");
		try {
			carregarRegistre();
		} catch (ExcepcioCognomNom e2) {
			System.out.println(e2.getLocalizedMessage());
		}

		System.out.println("::Carregar dades esdeveniments");

		try {
			desar = llegirDadesEsdeveniments();
		} catch (ExcepcioCognomNom e2) {
			System.out.println(e2.getLocalizedMessage());
		}

		if (desar) {
			System.out.println("::Desar dades XML");
			try {
				desarDadesXML();
			} catch (ExcepcioCognomNom e) {
				System.out.println(e.getLocalizedMessage());
			}
		}


		System.out.println("::Llistat esdeveniments");
		// Mostrar esdeveniments
		llistatEsdeveniments(esdeveniments==null?null:esdeveniments.iterator());


		System.out.println("::Carregar dades concerts CSV");
		try {
			desar = llegirDadesConcerts();
		} catch (ExcepcioCognomNom e1) {
			System.out.println(e1.getLocalizedMessage());
		} 
		if (desar) {
			System.out.println("::Desar dades CSV");
			try {
				desarDadesCSV();
			} catch (ExcepcioCognomNom e) {
				System.out.println(e.getLocalizedMessage());
			}
		}

		System.out.println("::Llistat concerts");
		// Mostrar concerts 
		llistatEsdeveniments(concerts==null?null:concerts.iterator());

		/**************************************************************************************/

		//FORZANDO ERROR
		try {
			Concert c1 = new Concert("Festival novembre", sdf.parse("2014/11/23"), "Girona", "The Hillbilly Moon Explosion" , -1, 22);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

		System.out.println("::Registre d'errors");
		// Mostrar registre errors 
		System.out.println(ExcepcioCognomNom.informeErrors());

		System.out.println("::Desar registre");
		// Tractament registre errors. Desar en fitxer serialitzat



		try {
			desarRegistre();
		} catch (ExcepcioCognomNom e) {
			System.out.println(e.getLocalizedMessage());
		}



	}


	public static void main(String[] args) throws Exception {
		new PersistenciaCognomsNom().init();
	}

	/********************************************************************************************************************/
	/********************************************************************************************************************/
	/****************************************** Llistat  ************************************************************/
	/********************************************************************************************************************/
	/********************************************************************************************************************/

	public void llistatEsdeveniments(Iterator<?> it) {


		while (it.hasNext()){
			Esdeveniment aux = (Esdeveniment) it.next(); //Em creo un objecte esdeveniment aux que em serveix tant per concerts com per fires etc
			System.out.println(aux.infoEsdeveniment()); //aprofitem el metode infoEsdeveniment
		}
	}


	/********************************************************************************************************************/
	/********************************************************************************************************************/
	/****************************************** Metodes I/O  ************************************************************/
	/********************************************************************************************************************/
	/********************************************************************************************************************/


	/**
	 * Si el fitxer no existeix no ha de generar error 
	 * 
	 * @throws ExcepcioCognomNom 8003
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void carregarRegistre() throws ExcepcioCognomNom {

		File fitxer = new File(BINPATH);
		if(fitxer.exists()){
			try {
				ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(BINPATH));
				//accedeixo al registre de la classe ExcepcioCognomNom
				ExcepcioCognomNom.setRegistre((TreeMap<String, ContextExcepcio>) fileIn.readObject());
				fileIn.close();
			} catch (ClassNotFoundException | IOException e) {
				throw new ExcepcioCognomNom("PersistenciaCognomsNom", 8003);
			}
		}

	}


	public void desarRegistre() throws ExcepcioCognomNom{
		ObjectOutputStream fileOut;
		try {
			fileOut = new ObjectOutputStream(new FileOutputStream(BINPATH));
			fileOut.writeObject(ExcepcioCognomNom.getRegistre()); //li passo el registre sencer
			fileOut.flush(); 
			fileOut.close();
		} catch (IOException e) {
			throw new ExcepcioCognomNom("PersistenciaCognomsNom", 8006);
		}
	}

	/**
	 * 
	 * @return no existeix el fitxer
	 * 
	 * @throws ExcepcioCognomNom 8001
	 * @throws FileNotFoundException 
	 */
	public boolean llegirDadesEsdeveniments() throws ExcepcioCognomNom{

		File fitxerxml = new File(XMLPATH);
		if(fitxerxml.exists()){
			XMLDecoder d;
			try {
				d = new XMLDecoder(new BufferedInputStream(new FileInputStream(fitxerxml)));
				esdeveniments = (TreeSet<Esdeveniment>) d.readObject(); //li passo el treeset sencer
				d.close();
			} catch (FileNotFoundException e) {
				throw new ExcepcioCognomNom("PersistenciaCognomsNom", 8001);
			}

		}else{ //si no existeix el fitxer es carreguen les dades desde el metode init
			initDadesEsdeveniments();
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @throws ExcepcioCognomNom 8004
	 * @throws FileNotFoundException 
	 */
	public void desarDadesXML() throws ExcepcioCognomNom {
		File fitxerxml = new File(XMLPATH);
		XMLEncoder e;
		try {
			e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fitxerxml)));
			e.writeObject(esdeveniments);
			e.close();
		} catch (FileNotFoundException e1) {
			throw new ExcepcioCognomNom("PersistenciaCognomsNom", 8004);
		}


	}

	/**
	 * 
	 * @throws Exception 
	 */
	public boolean llegirDadesConcerts() throws ExcepcioCognomNom {

		concerts = new HashSet<Concert>();	/* Sense repetits. Un concert √©s repetit si actua el mateix grup  */

		File csv = new File(CSVPATH);
		if(csv.exists()){
			try {
				CSVObjectReader<Concert> reader = new CSVObjectReader<Concert>(CSVPATH, Concert.class);
				Concert concert; 
				while((concert=reader.read(Concert.class))!=null)  { 
					concerts.add(concert); //cada concert que llegeix del csv, afegeixo a la estructura				
				}
			} catch (Exception e) {
				throw new ExcepcioCognomNom("PersistenciaCognomsNom", 8002);
			}

		}else{
			initDadesConcerts(); //carrega dades desde init   no hay try-catch porque ya se captura todo dentro!
			return true; //quan s'executi el metode i retorni true executara el metode desarDadesCSV
		}

		return false;	
	}

	/**
	 * 
	 * @throws ExcepcioCognomNom 8005
	 */
	public void desarDadesCSV() throws ExcepcioCognomNom {


		try {
			CSVObjectWriter<Concert> writerC = new CSVObjectWriter<Concert>(CSVPATH, true, ";");
			Iterator<Concert> iter = concerts.iterator();
			while (iter.hasNext()) {
				Concert c = (Concert) iter.next();

				writerC.write(c);
			}
			writerC.close();
		} catch (Exception e) {
			throw new ExcepcioCognomNom("PersistenciaCognomsNom", 8005);
		}



	}

	/********************************************************************************************************************/
	/********************************************************************************************************************/
	/****************************************** Metodes carrega de dades ************************************************/
	/********************************************************************************************************************/
	/********************************************************************************************************************/

	/**
	 *  @throws ExcepcioCognomNom 
	 */
	private void initDadesComerciants() {
		comerciants = new LinkedList<Comerciant>();

		// Comerciants

		try{
			comerciants.addAll(Arrays.asList(
					new Comerciant("Artesania Happy Karma", false),
					new Comerciant("Bijuteria Indian Treasures", false),
					new Comerciant("Carnicas Gonzalez", true),
					new Comerciant("Bar Manolo", true),
					new Comerciant("Xurreria La porra", true),
					new Comerciant("Moda Demode", false),
					new Comerciant("Bolsos y complementos Que Me lo quitan de las manos", false),
					new Comerciant("Esports Marathon", false)));

		}catch (ExcepcioCognomNom e){
			System.out.println(e.getLocalizedMessage());
		}
	}

	private void initDadesConcerts() { //como esta todo capturado no necesito un throws aqui
		concerts = new HashSet<Concert>();

		// Concerts


		Concert c1=null;
		try { 
			Date fecha;
			try {
				fecha = sdf.parse("2014/11-23");
			} catch (Exception e) {
				throw new ExcepcioCognomNom("PersistenciaCognomsNom", 7001); //si hago aqui el throw , se ira al try de encima y ahi se captura en el Exception porque es general
			}
			c1 = new Concert("Festival novembre", fecha, "Girona", "The Hillbilly Moon Explosion" , -50.00, 22);
			concerts.add(c1);
		} catch (Exception e1) {
			System.out.println(e1.getLocalizedMessage());

		} 

		Concert c2 = null;
		try {
			c2 = new Concert("Gira promocio", sdf.parse("2014/12/02"), "Mataro", "Mad Marge & The Stonecutters" , 25.00, 23);
			concerts.add(c2);
		} catch (ExcepcioCognomNom e1) {
			System.out.println(e1.getLocalizedMessage());

		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
		}

		Concert c3 = null;
		try {
			c3 = new Concert("Gira Boogie Man", sdf.parse("2015/01/15"), "Barcelona", "The Hellfreaks" , 32.50, 23);
			concerts.add(c3);
		} catch (ExcepcioCognomNom e1) {
			System.out.println(e1.getLocalizedMessage());

		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
		}
		Concert c4 = null;
		try {
			c4 = new Concert("Cover. Johnny remember me", sdf.parse("2014/11/02"), "Mataro", "The meteors" , 15.00, 22);
			concerts.add(c4);
		} catch (ExcepcioCognomNom e1) {
			System.out.println(e1.getLocalizedMessage());

		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
		}
		Concert c5 = null;
		try {
			c5 = new Concert("Festival PHB", sdf.parse("2015/01/10"), "Barcelona", "The Retarded Rats" , 18.00, 23);
			concerts.add(c5);
		} catch (ExcepcioCognomNom e1) {
			System.out.println(e1.getLocalizedMessage());

		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
		}
		Concert c6 = null;
		try {
			c6 = new Concert("Bombariado", sdf.parse("2014/12/02"), "Sant Feliu", "Rhythm Sophie" , 27.50, 21);
			concerts.add(c6);
		} catch (ExcepcioCognomNom e1) {
			System.out.println(e1.getLocalizedMessage());

		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
		}
		Concert c7 = null;
		try {
			c7 = new Concert("Promocio", sdf.parse("2014/12/02"), "Sant Feliu de Guixols", "Mad Marge & The Stonecutters" , 27.50, 21);
			concerts.add(c7);
		} catch (ExcepcioCognomNom e1) {
			System.out.println(e1.getLocalizedMessage());

		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
		}
		Concert c8 = null;
		try {
			c8 = new Concert("Festival Hivern", sdf.parse("2015/02/01"), "Lleida", "The Hellfreaks" , 28.50, 22);
			concerts.add(c8);
		} catch (ExcepcioCognomNom e1) {
			System.out.println(e1.getLocalizedMessage());

		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
		}

		Concert c9 = null;
		try {
			c9 = new Concert("Bombariado", sdf.parse("2014/12/02"), "Sant Feliu", "Rhythm Sophie" , -1, 21);
			concerts.add(c9);
		} catch (ExcepcioCognomNom e) {
			System.out.println(e.getLocalizedMessage());
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
		}




		if (comerciants == null) initDadesComerciants();

		c4.afegirPatrocinadorAlFinal(comerciants.get(2));
		c4.afegirPatrocinadorIniciLlista(comerciants.get(3));
		c4.afegirPatrocinadorAlFinal(comerciants.get(0));
		c4.afegirPatrocinadorAlFinal(comerciants.get(1));
		c4.afegirPatrocinadorAlFinal(comerciants.get(5));
		c4.afegirPatrocinadorIniciLlista(comerciants.get(7));
		c4.afegirPatrocinadorAlFinal(comerciants.get(6));
		c6.afegirPatrocinadorAlFinal(comerciants.get(6));


	}

	private void initDadesEsdeveniments() throws ExcepcioCognomNom {

		// Fires
		LinkedList<Fira> fires = new LinkedList<Fira>();

		// Esdeveniments
		esdeveniments = new TreeSet<Esdeveniment>();

		// Comerciants si escau
		if (comerciants == null) initDadesComerciants();

		try {
			fires.addAll(Arrays.asList(
					new Fira("La fira de la puri≠sssima", sdf.parse("2014/12/06") , "Santa Coloma de Gramanet", 3), 
					new Fira("IV Fira de la puri≠sssima", sdf.parse("2014/12/06") , "Sant Boi", 2), 
					new Fira("Fira puri≠sssima", sdf.parse("2014/12/06") , "Esplugues", 3), 
					new Fira("Fira de la constitucio", sdf.parse("2014/12/07") , "Igualada", 1), 
					new Fira("Fira abans de Nadal", sdf.parse("2014/12/07") , "Hospitalet", 3), 
					new Fira("Fira abans de Nadal 2", sdf.parse("2014/12/07") , "Hospitalet", 2)));

			esdeveniments.addAll(fires);

			esdeveniments.addAll(Arrays.asList(
					new Esdeveniment("Cavalcada dels Reis", sdf.parse("2015/01/05") , "Tarragona"), 
					new Esdeveniment("Cavalcada dels Reis", sdf.parse("2015/01/05") , "Santa Coloma de Gramanet"), 
					new Esdeveniment("Nit de reis", sdf.parse("2015/01/05") , "Hospitalet"), 
					new Esdeveniment("Concert de Nadal", sdf.parse("2014/12/24") , "Tarragona"), 
					new Esdeveniment("Concert de Nadal de les escoles", sdf.parse("2014/12/21") , "Santa Coloma de Gramanet"), 
					new Esdeveniment("Sant Esteve popular", sdf.parse("2014/12/26") , "Hospitalet")));


			Fira[] firesArray = fires.toArray(new Fira[fires.size()]);

			// Peticions inscripcio. No ha de generar Excepcio 
			firesArray[0].novaPeticioInscripcio(comerciants.get(0));
			firesArray[0].novaPeticioInscripcio(comerciants.get(1));
			firesArray[0].novaPeticioInscripcio(comerciants.get(2));
			firesArray[1].novaPeticioInscripcio(comerciants.get(3));
			firesArray[1].novaPeticioInscripcio(comerciants.get(4));
			firesArray[2].novaPeticioInscripcio(comerciants.get(5));
			firesArray[2].novaPeticioInscripcio(comerciants.get(0));
			firesArray[3].novaPeticioInscripcio(comerciants.get(1));
			firesArray[4].novaPeticioInscripcio(comerciants.get(2));
			firesArray[4].novaPeticioInscripcio(comerciants.get(2));
			firesArray[4].novaPeticioInscripcio(comerciants.get(3));
			firesArray[4].novaPeticioInscripcio(comerciants.get(4));
			firesArray[4].novaPeticioInscripcio(comerciants.get(0));
			firesArray[4].novaPeticioInscripcio(comerciants.get(4));

			firesArray[0].confirmarPeticioInscripcio(); 
			firesArray[0].confirmarPeticioInscripcio(); 
			firesArray[1].confirmarPeticioInscripcio(); 
			firesArray[2].confirmarPeticioInscripcio();  
			firesArray[2].confirmarPeticioInscripcio();  
			firesArray[2].confirmarPeticioInscripcio(); 
			firesArray[3].confirmarPeticioInscripcio();
			firesArray[3].anularDarreraConfirmada();  
		} catch (ParseException e) {
			throw new ExcepcioCognomNom("PersistenciaCognomsNom", 7001);
		} catch (Exception e) {
			throw new ExcepcioCognomNom("PersistenciaCognomsNom", 9999);
		}
	}
}
