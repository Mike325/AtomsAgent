package persistance;

public class DebugMode {
	
	static boolean state = false;
	
	public static boolean getDebugState() { return state; }
	
	public static void setDebugState(boolean new_state)
	{
		state = new_state;
		
		if(state == true)
		{
			System.out.println("Mensajes de debug activados\n");
		}
	}
	
	public static void printDebugMessage(String message)
	{
		if( state == true )
		{
			System.out.println(message);
		}
	}
}
