

public interface CSVable<T> {
	public String toCsv(String separator);
	
	public String csvHeader(String separator);

	public T fromCsv(String registre, String separator) throws Exception;
	
	public boolean checkCsvHeader(String header, String separator); 
	
	public String getCsvSeparator(String registre);
}
