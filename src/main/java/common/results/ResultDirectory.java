package common.results;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Bundles several Results in one.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ResultDirectory<E> extends Result<E> {
    private List<Result<E>> results;

    public ResultDirectory(String name) {
        super(name);
        this.results = newLinkedList();
    }

    public void addResult(Result<E> res) {
        results.add(res);
    }

    @Override
    public void write(String directory) throws IOException {
        String outDir = directory + "/" + getName();

        File dir = new File(outDir);
        if (!dir.exists()) dir.mkdir();

        for (Result<E> res : results) {
            res.write(outDir);
        }
    }

    @Override
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();

        for (Result<E> res : results) {
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
