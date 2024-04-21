package name.expenses.models;

import java.io.Serializable;

public interface GrantedAuthority extends Serializable {
    String getAuthority();
}