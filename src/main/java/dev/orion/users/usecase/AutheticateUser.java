public class AutheticateUser implements UseCase{
     /** The minimum size of the password required. */
     private static final int SIZE_PASSWORD = 8;

     /** User repository. */
     private Repository repository = new UserRepository();
 
     /**
      * Authenticates the user in the service (UC: Authenticate).
      *
      * @param email    : The email of the user
      * @param password : The password of the user
      * @return A Uni<User> object
      */
     @Override
     public Uni<User> authenticate(final String email, final String password) {
         Uni<User> user = null;
         if ((email != null) && (password != null)) {
             user = repository.authenticate(email,
                     DigestUtils.sha256Hex(password));
         } else {
             throw new IllegalArgumentException("All arguments are required");
         }
         return user;
     }
}
