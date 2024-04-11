package name.expenses.error.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
@EqualsAndHashCode(callSuper = true)
@Data
public class GeneralFailureException extends Exception {

	public enum Category {

		Business("BUS"),

		System("SYS"),

		Validation("VAL");

		private String type;

		Category(String type) {
			this.type = type;
		}

		public String value() {
			return type;
		}

	}
	public static final String GENERAL_ERROR = Category.System.value() + getPrefix() + "00";

	public static final String GENERAL_REMOVE_FAILURE = "SYS00002";

	public static final String GENERAL_REMOVE_FAILURE_WITH_VARS = "SYS00001";

	public static final String HASHING_ERROR = "SYS00004";


	protected static String getPrefix() {
		return "00";
	}

	private String errorCode;

	protected Map<String, String> varMap;

	public GeneralFailureException() {
		super();
	}

	public GeneralFailureException(String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public GeneralFailureException(String errorCode, Map<String, String> varMap) {
		super(errorCode);
		this.errorCode = errorCode;
		this.varMap = varMap;
	}

	public GeneralFailureException(String errorCode, Throwable initCase) {
		super(errorCode, initCase);
		this.errorCode = errorCode;
		if (initCase instanceof GeneralFailureException) {
			this.varMap = ((GeneralFailureException) initCase).getVarMap();
		}
	}

}
