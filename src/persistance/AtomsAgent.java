package persistance;

import java.util.ArrayList;

import controller.Publisher;
import model.Post;
import model.Connector;
import persistance.DebugMode;

/**
 *
 * @author Miguel Ochoa
 */
public class AtomsAgent 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
    	DebugMode.setDebugState(true);
    	Connector.setSchema(0);
    	int time = 30;
    	
        Publisher ctrl = new Publisher();
        
        double run = 0;
        int challenges_per_day = 0;

        while(true)
        {
        	try 
            {
        		
        		run += (run < Double.MAX_VALUE && DebugMode.getDebugState() == true) ? 1 : 0;
        		
        		DebugMode.printDebugMessage("\n----> iteracion:	" + run);
	            
	            ArrayList<Post> posts = ctrl.GetChallenges();
	            
	            if( !posts.isEmpty() && posts != null )
	            {   
	            	int challenges = ctrl.PostChallenges(posts);
	            	challenges_per_day += (challenges != -1)? challenges : 0 ;
	            }
	            
	            DebugMode.printDebugMessage("----> challenges completados:	" + challenges_per_day);
	            DebugMode.printDebugMessage("----> Espeando " + time + " seg.");
	        
				Thread.sleep(time * 1000);
			} 
            catch (InterruptedException e) 
        	{
				e.printStackTrace();
			}
        }
    }    
}
