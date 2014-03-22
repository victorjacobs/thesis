package common.results;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newLinkedHashMap;

/**
 * Class to write CSV files.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class CSVWriter<E> {
    private Map<String, List<E>> data;
	private final String name;
    private boolean writeHeaders;
    private String separator;
    private int nextRow = 0;     // For use when no headers are used

	/**
	 * Creates CSVWriter with given name. This name is used to identify it. When doing {@link CSVWriter#write(String)}
	 * , the data will be written at directory/name.csv.
	 *
	 * @param name Name of the writer
	 */
	public CSVWriter(String name) {
        this(name, true);
	}

    public CSVWriter(String name, boolean writeHeaders) {
        this.writeHeaders = writeHeaders;
        this.data = newLinkedHashMap();
        this.name = name;
        this.separator = ",";
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

    public void writeHeaders(boolean writeHeaders) {
        this.writeHeaders = writeHeaders;
    }

    public void separator(String separator) {
        this.separator = separator;
    }

    /**
     * Add a row to the CSV file. For now don't try to mix this with the {@link #addToColumn(String, Object)} calls.
     * TODO
     *
     * @param d
     */
    public void addRow(List<E> d) {
        // Make sure columns match
        while (data.keySet().size() < d.size())
            data.put(Integer.toString(data.keySet().size()), new LinkedList<E>());

        for (int col = 0; col < data.keySet().size(); col++) {
            data.get(Integer.toString(col)).add(nextRow, d.get(col));
        }

        nextRow++;
    }

    public void addToColumn(String header, List<E> d) {
        for (E el : d)
            addToColumn(header, el);
    }

	/**
	 * Adds column to the CSV file. Doesn't overwrite columns. If you want to overwrite a column,
	 * first explicitly remove it using {@link #deleteColumn(String)}.
	 *
	 * @param header Column name
	 * @param d Column data
	 */
	public void addColumn(String header, List<E> d) {
		checkState(!data.containsKey(header), "Column " + header + " already exists");

		data.put(header, new LinkedList<E>(d));
	}

	/**
	 * Deletes column from the CSV file.
	 *
	 * @param header Column name
	 */
	public void deleteColumn(String header) {
		checkState(data.containsKey(header), "Trying to remove non-existing column");

		data.remove(header);
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

        if (writeHeaders) {
            // Write headers
            for (String head : data.keySet()) {
                sb = sb.append(head).append(separator);
            }

            sb = sb.deleteCharAt(sb.length() - 1).append('\n');
        }

		// Figure out rows
		int nbRows = -1;
		for (String k : data.keySet())
			nbRows = (data.get(k).size() > nbRows) ? data.get(k).size() : nbRows;

		// Write data
		for (int i = 0; i < nbRows; i++) {
			for (String head : data.keySet()) {
                try {
                    sb.append(data.get(head).get(i));
                } catch (IndexOutOfBoundsException ignored) {
                    // Don't do anything
                }

				sb = sb.append(separator);
			}

			sb = sb.deleteCharAt(sb.length() - 1).append('\n');
		}

		return sb.toString();
	}
}
