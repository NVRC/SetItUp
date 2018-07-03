package threeblindmice.setitup.events;

public class QueryEvent {

    private String query;

    public QueryEvent(String qry){
        this.query = qry;
    }

    public String getQuery() {
        return query;
    }
}
