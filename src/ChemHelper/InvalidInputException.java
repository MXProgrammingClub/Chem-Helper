/*
 * An exception with some prewritten error messages.
 * 
 * Author: Julia McClellan
 * Version: 2/23/2016
 */

package ChemHelper;

public class InvalidInputException extends Throwable 
{
	public static final int INVALID_ELEMENT = 0, NOT_NUMBER = 1, NOT_BALANCED = 2;
	
	public InvalidInputException(int type)
	{
		super(getMessage(type));
	}
	
	private static String getMessage(int type)
	{
		if(type == INVALID_ELEMENT) return "Invalid element entered";
		else if(type == NOT_NUMBER) return "No number where there should have been";
		else if(type == NOT_BALANCED) return "This equation could not be balanced.";
		return "There was a problem with your input";
	}
}