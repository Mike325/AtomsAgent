package model;

/*
 *
 *
 */
public class Post 
{
    String intranet;
    String user_name ;
    
    String photo;
    int post;
    int idcompletedchallenges;
    
    String challenge_text;
    String challenge_name;
    String short_description;
    String long_description;
    
    String category_name;

   /*
    *
    */
    public String getLongDescription() { return this.long_description; }
    
   /*
    *
    */
    public String getShortDescription() { return this.short_description; }
    
   /*
    *
    */
    public String getChallengeText() { return this.challenge_text; }
    
   /*
    *
    */
    public String getChallengeName() { return this.challenge_name; }
    
   /*
    *
    */
    public String getCategoryName() { return this.category_name; }
    
   /*
    *
    */
    public String getIntranet() { return this.intranet; }

   /*
    *
    */
    public String getPhoto() { return this.photo; }
    
    /*
    *
    */
    public String getUserName() { return this.user_name ; }

   /*
    *
    */
    public int getPost() { return this.post; }

   /*
    *
    */
    public int getChallengeID() { return this.idcompletedchallenges; }
   
   /*
    *
    */
    public void setLongDescription(String long_description) 
    { 
        this.long_description = long_description; 
    }
   
   /*
    *
    */
    public void setShortDescription(String short_description) 
    { 
        this.short_description = short_description; 
    }
    
   /*
    *
    */
    public void setChallengeName(String challenge_name) 
    { 
        this.challenge_name = challenge_name; 
    }
    
   /*
    *
    */
    public void setCategoryName(String category_name) 
    { 
        this.category_name = category_name; 
    }
    
   /*
    *
    */
    public void setChallengeText(String challenge_text) 
    { 
        this.challenge_text = challenge_text; 
    }
    
   /*
    *
    */
    public void setIntranet( String intranet ) 
    { 
        this.intranet = intranet;
    }

   /*
    *
    */
    public void setPhoto( String photo ) 
    { 
        this.photo = photo;
    }

   /*
    *
    */
    public void setPost( int post ) 
    { 
        this.post = post;
    }

   /*
    *
    */
    public void setChallengeID( int idcompletedchallenges ) 
    { 
        this.idcompletedchallenges = idcompletedchallenges;
    }
    
   /*
    *
    */
    public void setUserName( String user_name ) 
    { 
        this.user_name  = user_name;
    }
}
