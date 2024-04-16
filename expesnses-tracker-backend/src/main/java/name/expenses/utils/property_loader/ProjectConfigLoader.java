package name.expenses.utils.property_loader;

public interface
ProjectConfigLoader {

	public String getProjectName();

	String getConfigProjectName();

	String getProjectHomeDir();

    public String getUserHome();
	public String getActiveMQBrokerURL();
	public String getActiveMQBrokerReceiveTimeout();
	public String getActiveMQBrokerReceiveTimeToLive();
	public String getDuplicateRequestWindowTimeout();
	public String getDuplicateRequestPreventerStatus();
}