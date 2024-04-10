package name.expenses.features.user.service;


import jakarta.ejb.Local;
import jakarta.ejb.Remote;

@Local
public interface UserService {
    String getUserInfo();
}