package ChemHelper;

public class InvalidInputException extends Throwable 
{
	public static final int INVALID_ELEMENT = 0, NOT_NUMBER = 1;
	
	public InvalidInputException(int type)
	{
		super(getMessage(type));
	}
	
	private static String getMessage(int type)
	{
		if(type == INVALID_ELEMENT) return "Invalid element entered";
		else if(type == NOT_NUMBER) return "No number where there should have been";
		return "There was a problem with your input";
	}
}