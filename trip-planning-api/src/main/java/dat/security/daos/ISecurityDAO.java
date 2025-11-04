package dat.security.daos;

import dat.exceptions.ApiException;
import dat.security.entities.User;
import dat.security.exceptions.ValidationException;
import dk.bugelhartmann.UserDTO;

public interface ISecurityDAO {
    UserDTO getVerifiedUser(String username, String password) throws ValidationException;
    User createUser(String username, String password) throws ApiException;
    User addRole(UserDTO user, String newRole);
}
