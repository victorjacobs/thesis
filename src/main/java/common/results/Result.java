package common.results;

import java.io.IOException;

/**
 * Interface that defines a result that can be written to disk through {@link #write(String)}.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class Result<E> {
    private final String name;

    protected Result(String name) {
        this.name = name;
    }

    /**
     * A name identifying the result. This is used for identifying the result on disk (either via file name or
     * directory name etc.)
     *
     * @return Name of the result
     */
    public String getName() {
        return name;
    }

    /**
     * Write the represented Result to given directory. This might be a single csv file or a directory.
     *
     * @param directory Directory to output data to
     * @throws IOException Thrown when IO fails
     */
    public abstract void write(String directory) throws IOException;

    /**
     * Returns a pretty printed string representation of the result.
     *
     * @return String representing the result
     */
    public abstract String prettyPrint();
}
