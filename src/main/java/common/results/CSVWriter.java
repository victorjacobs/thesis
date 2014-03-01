package common.results;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newLinkedHashMap;

/**
 * Class to write CSV files.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class CSVWriter<E> {

	private Map<String, List<E>> data;
	private String name;

	/**
	 * Creates CSVWriter with given name. This name is used to identify it. When doing {@link CSVWriter#write(String)}
	 * , the data will be written at directory/name.csv.
	 *
	 * @param n Name of the writer
	 */
	public CSVWriter(String n) {
		data = newLinkedHashMap();
		name = n;
	}

	/**
	 * Add data to certain column with given name.
	 *
	 * @param header Column name
	 * @param d Data to add to column
	 */
	public void addToColumn(String header, E d) {
		if (!data.containsKey(header))
			data.put(header, new LinkedList<E>());

		data.get(header).add(d);
	}

	/**
	 * Write the represented CSV file to given directory. The data can be found at directory/name.csv.
	 *
	 * @param directory Directory to output data to
	 * @throws IOException Thrown when IO fails
	 */
	public void write(String directory) throws IOException {
		Writer w = new PrintWriter(directory + "/" + getName() + ".csv");
		w.write(toString());
		w.close();
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// Write headers
		for (String head : data.keySet()) {
			sb = sb.append(head).append(",");
		}

		sb = sb.deleteCharAt(sb.length() - 1).append('\n');

		// Figure out rows
		int nbRows = -1;
		for (String k : data.keySet())
			nbRows = (data.get(k).size() > nbRows) ? data.get(k).size() : nbRows;

		// Write data
		for (int i = 0; i < nbRows; i++) {
			for (String head : data.keySet()) {
				if (i < nbRows)
					sb.append(data.get(head).get(i));

				sb = sb.append(',');
			}

			sb = sb.deleteCharAt(sb.length() - 1).append('\n');
		}

		return sb.toString();
	}
}
