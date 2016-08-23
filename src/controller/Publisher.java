package controller;

import model.Post;
import persistance.DebugMode;
import model.Connector;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

import java.util.ArrayList;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.io.IOException;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.codec.binary.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;


/*
 * 
 *
 */
public class Publisher 
{

    ResultSet result;
    PreparedStatement pre_statement;
    Statement statement;
    Connection connection;

    Connector connector;
    String schema;
    
    String base_url;
    String auth;
    
    public Publisher()
    {
    	this.connector = new Connector();
        this.schema    = Connector.schema;
        this.base_url  = "https://w3-connections.ibm.com";
        this.auth      = "YXRvbXNnZGxAbXgxLmlibS5jb206YXRvbXM5OWdkbA==";
    }    
    
    /*
    * 
    */
    public boolean postAPI( String api_url )
    {
        boolean success = false;

        return success;
    }

    /*
    * 
    */
    public boolean getAPI( String api_url )
    {
        boolean success = false;

        return success;
    }
     
    
   /*
    * 
    */
    public String uploadImage( String image, int challenge_id, String user_name )
    {
        String t_user_name = user_name.replace(" ", "-");
        
        String imageid 		  = "-1";
        String name    		  = "atoms-"+ t_user_name +"-" + challenge_id;
        
        String library_url 	  = base_url + "/files/basic/api/myuserlibrary/feed";
        String parameters_api = "?title="+name+"&label="+name+"&visibility=public";
       
        File file;
        HttpResponse response;
        try
        {
            String path = stringToFile( image, challenge_id, t_user_name  );
            file        = new File(path);
            
            HttpClient httpClient = HttpClientBuilder.create().build();
            String basic_auth     = "Basic " + this.auth;
            
            DebugMode.printDebugMessage("url de api:                 " + library_url + parameters_api);
            
            HttpPost post_method = new HttpPost(library_url + parameters_api);
            post_method.addHeader(HttpHeaders.CONTENT_TYPE, "image/jpg");
            post_method.addHeader(HttpHeaders.AUTHORIZATION, basic_auth);
            post_method.addHeader("Slug", path);
            
            post_method.setEntity(new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM));
            
            response = httpClient.execute(post_method);
            
            DebugMode.printDebugMessage("respuesta Imagen:   " + response.toString() + "\n");
            DebugMode.printDebugMessage("Library URL:   " + library_url + "\n");
            
            HttpGet get_method = new HttpGet(library_url);
            get_method.addHeader(HttpHeaders.CONTENT_TYPE, "application/atom+xml");
            get_method.addHeader(HttpHeaders.AUTHORIZATION, basic_auth);
            response = httpClient.execute(get_method);
            
            DebugMode.printDebugMessage("respuesta Libreria:   " + response.toString() + "\n");
            
            String xml =  EntityUtils.toString(response.getEntity());
            DebugMode.printDebugMessage(xml);
            
            if(!xml.isEmpty())
            {
            	imageid = findInXML(xml, name + ".jpg", "/feed/entry/id", 5, 3 );
            }
            
            File deleter   = new File(name + ".jpg");  
            
            if(deleter.exists())
            {
                 deleter.delete();
            } 
        }
        catch(Exception e)
        {
            e.printStackTrace();
            imageid = "-1";
        }
        
        return imageid;
    }
    
   /*
    * 
    */
    public int PostChallenges(ArrayList<Post> posts )
    {
        int success = 0;
        
        String attach_url = "/common/opensocial/basic/rest/ublog/{userId}/@all";
        String json = 	"{"+
        				"\"content\":\"{user_name} just completed the challenge: '{challenge_name}' on #{category_name} \n {message} "+
        					"Be part of #ATOMS https://ibm.biz/Bd4ufn\"{image} }";
        
        String message = " Message: '{text}'\n";
        
        String attachment = ",\"attachments\":["+
							"{"+
							"\"displayName\":\"Atoms.jpg\","+
							"\"url\":\"https://w3-connections.ibm.com/files/app/file/{id}\","+
							"\"image\":{"+
							"\"url\":\"https://w3-connections.ibm.com/files/form/anonymous/api/library/0cdd9f8c-1415-44b8-947e-7b3b656046ed/document/{id}/thumbnail\"}"+
							"}]";
        
        try
        {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String basic_auth     = "Basic " + this.auth;
            
            for(Post post : posts)
            {
            	String json_body = json;
            	String imageid = post.getPhoto();
            	
            	DebugMode.printDebugMessage("Publicando");
            	DebugMode.printDebugMessage("\tIntranet:     " + post.getIntranet());
                DebugMode.printDebugMessage("\tChallenge ID: " + post.getChallengeID());
                
                if(!imageid.contains("PHOTO"))
                {
                	imageid = uploadImage(post.getPhoto(), post.getChallengeID(), post.getUserName());
                }                
                
                DebugMode.printDebugMessage("ID de imagen:   " + imageid);
                
                json_body = (imageid.contains("PHOTO")) ? json_body.replace("{image}", "") : json_body.replace("{image}", attachment );
                                
                if(!imageid.equals("-1"))
                {
                	String userid    = getUserID(post.getIntranet(), post.getUserName());
                    DebugMode.printDebugMessage("ID de usuario:   " + userid);
                    
                    if(!userid.equals("-1"))
                    { 
                    	
                    	if(post.getChallengeText().isEmpty() || (post.getChallengeText().length() == 1 && post.getChallengeText().contains(" ")) )
                    	{
                    		json_body = json_body.replace("{message}", "");
                    	}
                    	else
                    	{
                    		json_body = json_body.replace("{message}", message) ;
                    		
                    		if(!post.getChallengeText().isEmpty() && post.getChallengeText().contains("\""))
                        	{
                        		post.setChallengeText(post.getChallengeText().replace("\"", "'"));
                        	}
                    		
                    		json_body = json_body.replace("{text}", post.getChallengeText());
                    	}
                    	
                    	String api_url   = base_url + attach_url.replace("{userId}", userid);
                        
                    	json_body = json_body.replace("{user_name}", post.getUserName());
                    	json_body = json_body.replace("{category_name}", post.getCategoryName());
                    	json_body = json_body.replace("{challenge_name}", post.getChallengeName());
                    	                    	
                    	if(!imageid.contains("PHOTO"))
                    	{
                    		json_body = json_body.replace("{id}", imageid);
                    	}
                    	
                        DebugMode.printDebugMessage("url:   " + api_url);
                        DebugMode.printDebugMessage("json:   " + json_body);
                        
                        StringEntity requestEntity = new StringEntity(json_body, ContentType.APPLICATION_JSON);
                        
                        HttpPost post_method = new HttpPost(api_url);
                        post_method.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                        post_method.addHeader(HttpHeaders.AUTHORIZATION, basic_auth);
                        post_method.setEntity(requestEntity);

                        HttpResponse response = httpClient.execute(post_method);
                        
                        DebugMode.printDebugMessage("respuesta Post:   " + response.toString() + "\n");
                        
                        int status = response.getStatusLine().getStatusCode();
                        DebugMode.printDebugMessage(status + "");
                        
                        if(status == 200)
                        {
                        	changeStatus(post);
                        	success++;
                        }	
                    }
                    else
                    {
                    	DebugMode.printDebugMessage("Error: userid not found.\n Data provided: \n");
                    	DebugMode.printDebugMessage("	----> User: 	" + post.getUserName() );
                    	DebugMode.printDebugMessage("	----> Intranet: " + post.getIntranet() );
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }        
        
        return success;
    }

	/*
	 *
	 */
	public String findInXML(String xml, String find, String path, int search_offset, int target_offset)
	{
		String label="-1";
		int i=-1;
		String result_id="-1";
		  
		try
		{
			VTDGen vg = new VTDGen();
			byte[] bytes = xml.getBytes("UTF-8");
			vg.setDoc(bytes);
			vg.parse(true);
			VTDNav vn = vg.getNav();
			AutoPilot ap =  new AutoPilot(vn);
			ap.selectXPath(path);
			while((i = ap.evalXPath())!=-1)
			{
				label=vn.toString(i+search_offset);
				if(label.equals(find))
				{
					result_id=vn.toString(i+target_offset);
					//DebugMode.printDebugMessage("Si es "+label);
					break;
				}
				else
				{
					//DebugMode.printDebugMessage("No es "+label);
					result_id="-1";
					label="-1";
				}		
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			result_id="-1";
		}
		return result_id;    
	}

    
   /*
    * 
    */
    public String getUserID( String intranet , String name)
    {
        String profile_url = base_url + "/profiles/atom/search.do?name={name}";
        String userid = "";
        
        String[] names = name.split(" ");
        String partial_name = "";
                
        try
        {
            for(String n: names)
            {   
                partial_name += (partial_name.isEmpty()) ? n : "+" + n ; 
                
                HttpClient httpClient = HttpClientBuilder.create().build();
                String basic_auth     = "Basic " + this.auth;
                String api_url        = profile_url.replace("{name}", partial_name);

                HttpGet get_method = new HttpGet(api_url);
                get_method.addHeader(HttpHeaders.CONTENT_TYPE, "application/atom+xml; charset=UTF-8");
                get_method.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0");
                get_method.addHeader(HttpHeaders.AUTHORIZATION, basic_auth); 

                HttpResponse response = httpClient.execute(get_method);
                String xml =  EntityUtils.toString(response.getEntity());
                
                DebugMode.printDebugMessage("Response UserID: " + response.toString());
                DebugMode.printDebugMessage(xml);
                
                if(!xml.isEmpty())
                {
                    userid = findInXML(xml, intranet, "/feed/entry/contributor/email", 1, -1);
                }
                
                if(!userid.equals("-1")) break;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }   
        
        return userid;
    }
    
   
    /*
    * 
    */
    public boolean changeStatus(Post post)
    {
        boolean status      = false;
        String t_challenges = schema + ".completedchallenges";

        try
        {
        	DebugMode.printDebugMessage("Actualizando Status");
        	
            connection    = connector.CreateConnection();

        	DebugMode.printDebugMessage("Reto " + post.getChallengeID());
        	
            pre_statement = connection.prepareStatement("update "+t_challenges+ 
                                                        " set post=-1"+
                                                        " where idcompletedchallenges=" + post.getChallengeID());
            pre_statement.executeUpdate();
            
            
            DebugMode.printDebugMessage("Retos Actualizados");
            
            status = true;
        }
        catch( Exception e )
        {
            e.printStackTrace();
            status = false;
        }
        finally
        {
            connector.CloseConnection(connection);
        }

        return status;
    }

   
    /*
    * 
    */
    public String clobToString(Clob image)
    {
        String s_image      = "";
        String str_line     = "";
        StringBuffer buffer = new StringBuffer();

        try
        {
            BufferedReader bufferRead = new BufferedReader(image.getCharacterStream());

            while( (str_line = bufferRead.readLine()) != null)
            {    
                buffer.append(str_line);
            }

            s_image = buffer.toString(); 
        }
        catch( IOException | SQLException io_sql_e )
        {
            io_sql_e.printStackTrace();
            s_image = "";
        }

        return s_image;
    }

    
   /*
    * 
    */
    public String stringToFile( String image, int challenge_id, String name ) throws IOException
    {
        byte[] decoded = Base64.decodeBase64(image);
        
        name = name.replace(" ", "-");
        
        String path    = "atoms-"+ name +"-" + challenge_id + ".jpg";
        File deleter   = new File(path);  
        
        if(deleter.exists())
        {
             deleter.delete();
        }
        
        FileOutputStream imageOutFile = new FileOutputStream(path);
 
        imageOutFile.write(decoded);
        imageOutFile.close();
        
        return path;
    }
    
    
   /*
    * 
    */
    public ArrayList<Post> GetChallenges()
    {
        Post post             			= null; 
        ArrayList<Post> posts 			= new ArrayList<Post>();
        String t_completed_challenges   = schema + ".completedchallenges";
        String t_challenges   			= schema + ".challenges";
        String t_users        			= schema + ".users";
        String t_category     			= schema + ".categories";

        try
        {
        	DebugMode.printDebugMessage("buscando...");
            connection    = connector.CreateConnection();
            pre_statement = connection.prepareStatement("select intranetid, displayname, attachtext, idcompletedchallenges, post, imageurl, shortdescription, longdescription, " + 
            											t_category + ".name as categoryname, " + t_challenges + ".name as challengename "+
                                                        " from "+t_completed_challenges+ 
                                                        " join "+t_users+
                                                        " on "+t_completed_challenges+".iduser="+t_users+".iduser"+
                                                        " join "+t_challenges+
                                                        " on "+t_completed_challenges+".idchallenges="+t_challenges+".idchallenges"+
                                                        " join "+t_category+
                                                        " on "+t_category+".idcategory="+t_challenges+".idcategory"+
                                                        " where post=1");
            result = pre_statement.executeQuery();
            
            while (result.next()) 
            {
                post = new Post();
                post.setIntranet(result.getString("intranetid"));
                post.setUserName(result.getString("displayname"));

                post.setCategoryName(result.getString("categoryname"));
                
                post.setChallengeName(result.getString("challengename"));
                post.setChallengeText(result.getString("attachtext"));
                post.setShortDescription(result.getString("shortdescription"));
                post.setLongDescription(result.getString("longdescription"));
                post.setChallengeID(result.getInt("idcompletedchallenges"));
                post.setPost(result.getInt("post"));
                post.setPhoto(clobToString(result.getClob("imageurl")));
                
                DebugMode.printDebugMessage("Intranet: " + post.getIntranet());
                DebugMode.printDebugMessage("|\tName: " + post.getUserName());
                DebugMode.printDebugMessage("|\tChallenge ID: " + post.getChallengeID());

                posts.add(post);
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            posts = null;
        }
        finally
        {
            connector.CloseConnection(connection);
        }
        
        DebugMode.printDebugMessage("\n");
        return posts;
    }
}
