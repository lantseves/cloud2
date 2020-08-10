package main.java.response;

import java.io.Serializable;

public class AuthorizationResponse implements Serializable {
    private String login ;
    private boolean result ;

    public AuthorizationResponse() {
    }

    public AuthorizationResponse(String login, boolean result) {
        this.login = login;
        this.result = result;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "AuthorizationResponse{" +
                "login='" + login + '\'' +
                ", result=" + result +
                '}';
    }
}
