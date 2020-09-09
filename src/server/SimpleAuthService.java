package server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService{

    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData (String login, String password, String nickname) {
            this.login = login;
            this.password= password;
            this.nickname = nickname;
        }
    }

    List<UserData> users;

    public SimpleAuthService() {
        users = new ArrayList<>();
        for (int i = 0; i <= 10 ; i++) {
            users.add(new UserData("login" + i, "pass" + i,
                    "nickname" + i));
        }

        users.add(new UserData("sanan", "sanan", "mafeeoznik"));
        users.add(new UserData("elish", "elish", "KELT"));
        users.add(new UserData("alex", "alex", "Булочка"));
        users.add(new UserData("lera", "lera", "Зайка"));
        users.add(new UserData("madina", "madina", "Meymun"));
        users.add(new UserData("melek", "melek", "Tosmelek"));

    }


    @Override
    public String getNickNameByLoginAndPassword(String login, String password) {
        for (UserData user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.nickname;
            }
        }
        return null;
    }
}
