package common.results;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Bundles several {@link common.results.Result}s in one.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ResultDirectory extends Result {
    private List<Result> results;

    /**
     * Constructs a new ResultDirectory, given a directory name
     *
     * @param directoryName Name of the directory in which the contained results will be written
     */
    public ResultDirectory(String directoryName) {
        super(directoryName);
        this.results = newLinkedList();
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    /**
     * Add a result to this directory.
     *
     * @param res Result to be added
     */
    public void addResult(Result res) {
        if (res != null)
            results.add(res);
    }

    @Override
    @SuppressWarnings("all")
    public void write(String directory) throws IOException {
        String outDir = (directory == "") ? getName() : directory + "/" + getName();

        File dir = new File(outDir);
        if (!dir.exists()) dir.mkdirs();

        for (Result res : results) {
            res.write(outDir);
        }
    }

    @Override
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();

        for (Result res : results) {
            if (res.prettyPrint() == null)
                continue;
            sb.append(getName());
            sb.append('/');
            sb.append(res.prettyPrint());
            sb.append('\n');
        }

        return sb.toString();
    }
}
