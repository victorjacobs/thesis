import org.jppf.utils.JPPFConfiguration;

import java.io.*;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class RemoteConfig implements JPPFConfiguration.ConfigurationSource {

	@Override
	public InputStream getPropertyStream() throws IOException {
		return new FileInputStream("files/config/jppf.properties");
	}

}
