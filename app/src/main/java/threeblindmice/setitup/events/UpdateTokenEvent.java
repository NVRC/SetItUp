package threeblindmice.setitup.events;

public class UpdateTokenEvent {
    private String token;
    private String email;
    public UpdateTokenEvent(){
    }
    public void addToken(String newToken){
        this.token = newToken;
    }

    public void addEmail(String name){
        this.email = name;
    }
    public String getEmail(){
        return email;
    }

    public String getToken(){
        return token;
    }
}
