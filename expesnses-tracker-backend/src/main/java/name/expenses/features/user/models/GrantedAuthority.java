package name.expenses.features.user.models;

import java.io.Serializable;

public interface GrantedAuthority extends Serializable {
    String getAuthority();
}