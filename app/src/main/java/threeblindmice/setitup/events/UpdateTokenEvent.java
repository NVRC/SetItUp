package threeblindmice.setitup.events;

public class UpdateTokenEvent {
    private String token;

    public UpdateTokenEvent(String newToken){
        this.token = newToken;
    }

    public String getToken(){
        return token;
    }
}
