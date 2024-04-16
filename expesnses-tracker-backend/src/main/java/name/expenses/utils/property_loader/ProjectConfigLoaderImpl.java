package name.expenses.utils.property_loader;

import jakarta.inject.Singleton;

import java.io.File;

@Singleton
public class ProjectConfigLoaderImpl implements ProjectConfigLoader {
	
	public String getProjectName() {
            return System.getenv("PROJECT");
	}
	@Override
	public String getConfigProjectName() {
            return "expenses-apis-config" + File.separator + "expenses";
	}
	@Override
	public String getProjectHomeDir() {
		String currentDir = System.getProperty("user.dir");

		// Navigate upwards to the project root
		File projectRoot = new File(currentDir).getParentFile().getParentFile().getParentFile();

		// Set projectHome to the project root path
		return projectRoot.getAbsolutePath();
	}

	public String getUserHome() {
		return System.getenv("EXPENSES_APIS_CONFIG");
	}

	public String getActiveMQBrokerURL() {
		return System.getenv("ACTIVEMQ_BROKER_URL");
	}

	public String getActiveMQBrokerReceiveTimeout()
	{
		//return System.getenv("ACTIVEMQ_BROKER_RECEIVE_TIMEOUT");
		return "120000";
	}

	public String getActiveMQBrokerReceiveTimeToLive() {
		return "35500";
	}

	public String getDuplicateRequestWindowTimeout() {
		
		return System.getenv("DUPLICATE_REQUEST_WINDOW_TIMEOUT");
	}

	public String getDuplicateRequestPreventerStatus() {
		
		return System.getenv("DUPLICATE_REQUEST_PREVENTER_ENABLED");
	}

	public ProjectConfigLoaderImpl() {
	}
}