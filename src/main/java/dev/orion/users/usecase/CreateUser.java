public class CreateUser implements UseCase{
    private static final int SIZE_PASSWORD = 8;

    /** User repository. */
    private Repository repository = new UserRepository();

    /**
     * Creates a user in the service (UC: Create).
     *
     * @param name     : The name of the user
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     * @return A Uni<User> object
     */
    @Override
    public Uni<User> createUser(final String name, final String email,
            final String password) {
        Uni<User> user = null;
        if (name.isBlank() || !EmailValidator.getInstance().isValid(email)
            || password.isBlank()) {
            throw new IllegalArgumentException("Blank arguments or invalid e-mail");
        } else {
            if (password.length() < SIZE_PASSWORD) {
                throw new IllegalArgumentException(
                        "Password less than eight characters");
            } else {
                user = repository.createUser(name, email,
                        DigestUtils.sha256Hex(password));
            }
        }
        return user;
    }
}