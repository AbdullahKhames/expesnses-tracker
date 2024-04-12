package name.expenses.utils.property_loader;

import jakarta.inject.Singleton;

@Singleton
public class ProjectConfigLoaderImpl implements ProjectConfigLoader {
	
	public String getProjectName() {
            return System.getenv("PROJECT");
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