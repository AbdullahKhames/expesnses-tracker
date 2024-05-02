package name.expenses.utils.property_loader;

import jakarta.inject.Singleton;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Context;

import java.io.File;

@Singleton
public class ProjectConfigLoaderImpl implements ProjectConfigLoader {
	@Context
	private ServletContext servletContext;
	
	public String getProjectName() {
		String project = System.getenv("PROJECT");
		if (project == null || project.isBlank()) {
			return "expenses";
		}
		return project;
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
	@Override
	public StringBuilder getResourcesPath() {
		String webInfPath = servletContext.getRealPath("/WEB-INF");
		String expensesConfigPath = webInfPath + File.separator + "classes";
        return new StringBuilder(expensesConfigPath);
	}


	public String getUserHome() {
		return System.getenv("EXPENSES_APIS_CONFIG");
	}
	public String getProjectResourcesHome() {
		return System.getenv("EXPENSES_RESOURCES");
	}
	@Override
	public StringBuilder getConfigHome() {
		String projectResources = getProjectResourcesHome();
		if (projectResources == null || projectResources.isBlank()) {
			return getResourcesPath();
		}
		return new StringBuilder(projectResources);
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