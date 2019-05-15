package ro.nila.ra.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.nila.ra.dto.PersonDTO;
import ro.nila.ra.dto.SignInRequest;
import ro.nila.ra.dto.SignUpRequest;

import javax.validation.Valid;

@RequestMapping("/users")
public interface UsersApi {

    /**
     * Method that will verify credentials and if valid will return a JwtToken
     *
     * @param signInRequest object that will contain username/email and password
     * @return  Success - 202 Accepted and Account.id, accessToken, tokenType
     *          Fail - 409 Conflict if email or username is already taken
     */
    @PostMapping("/signIn")
    ResponseEntity signIn(@Valid @RequestBody SignInRequest signInRequest);

    /**
     * Method that will register a new User if the username/email are not already used
     *
     * @param signUpRequest object that will contain Account details
     * @return  Success - 201 Created and Account
     *          Fail - 409 Conflict if email or username is already taken
     */
    @PostMapping("/signUp")
    ResponseEntity signUp(@Valid @RequestBody SignUpRequest signUpRequest);

    /**
     * Method that will get a Account based on its id
     *
     * @param id identifier of the Account
     * @return Success - 200 Ok and the Account
     *         Fail - 404 Not Found
     */
    @GetMapping("/{id}")
    ResponseEntity getAccount(@Valid @PathVariable Long id);

    /**
     * Method that will get all Accounts from DB
     *
     * @return Success - 200 Ok and List with the Accounts
     *         Fail - 404 Not Found
     */
    @GetMapping()
    ResponseEntity getUsers();

    /**
     * Method that will get a Account based on its Username / Email
     *
     * @param usernameOrEmail identifier of the Account
     * @return Success - 200 Ok and the Account
     *         Fail - 404 Not Found
     */
    @GetMapping("/me")
    ResponseEntity getAccountByUsernameOrEmail(@Valid @RequestHeader String usernameOrEmail);

    /**
     * Method that will delete an Account based on its id
     *
     * @param id identifier of the Account
     * @return  Success - 200 Ok and Deleted Account
     *          Fail - 404 Not Found
     */
    @DeleteMapping("/{id}")
    ResponseEntity deleteUserById(@Valid @PathVariable Long id);

    /**
     * - Method that will SAVE / UPDATE a Person and associate it with an Account.
     * - SAVE: New Person will be automatically linked with the logged in Account
     * - UPDATE: If the Account will already have a Person associated with it
     *
     * @param personDTO - Person details that an Account will contain
     * @return Success - 200 Ok and Person details with associated Account id
     *         Fail - 400 Bad Request
     */
    @PostMapping("/person")
    ResponseEntity saveOrUpdatePerson(@Valid @RequestBody PersonDTO personDTO);
}
