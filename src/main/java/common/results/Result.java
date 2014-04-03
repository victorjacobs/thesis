package common.results;

import java.io.IOException;

/**
 * Interface that defines an abstraction for result data to be written to disk through {@link #write(String)}.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class Result {
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
    public final String getName() {
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
     * Use with caution
     * @throws IOException
     */
    public void write() throws IOException {
        write("");
    }

    /**
     * Returns a pretty printed string representation of the result.
     * TODO this is probably left over from some refactoring. Maybe just move to toString()
     *
     * @return String representing the result
     */
    public abstract String prettyPrint();
}
