package ro.nila.ra.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ro.nila.ra.exceptions.AppException;
import ro.nila.ra.model.Account;
import ro.nila.ra.model.Role;
import ro.nila.ra.model.enums.RoleName;
import ro.nila.ra.payload.ApiResponse;
import ro.nila.ra.payload.JwtAuthenticationResponse;
import ro.nila.ra.payload.SignInRequest;
import ro.nila.ra.payload.SignUpRequest;
import ro.nila.ra.security.JwtTokenProvider;
import ro.nila.ra.service.AccountService;
import ro.nila.ra.service.RoleService;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/users")
public class UsersController {

    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationManager authenticationManager;
    private RoleService roleService;
    private AccountService accountService;

    public UsersController(
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager,
            RoleService roleService,
            AccountService accountService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.roleService = roleService;
        this.accountService = accountService;
    }

    /**
     * Method that will verify credentials and if valid will return a JwtToken
     *
     * @param signInRequest object that will contain username/email and password
     * @return JwtAuthenticationResponse object that will contain JwtToken details
     */
    @PostMapping("/signIn")
    public ResponseEntity signIn(@Valid @RequestBody SignInRequest signInRequest) {

        return ResponseEntity.ok(authenticate(signInRequest));
    }

    /**
     * Method that will register a new User if the username/email are not already used
     * - Fail will return {"success" : "false"} if username or email are already into DB
     * - Success will return {"success" : "true"} if registration was successfully
     *
     * @param signUpRequest object that will contain Account details
     * @return ApiResponse object with a message containing the success or failure of the request
     */
    @PostMapping("/signUp")
    public ResponseEntity signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (accountService.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!", null),
                    HttpStatus.BAD_REQUEST);
        }
        if (accountService.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!", null),
                    HttpStatus.BAD_REQUEST);
        }
        // Creating user's account
        Account account = new Account(signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Role accountRole = roleService.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));
        account.setRoles(Collections.singleton(accountRole));
        Account result = accountService.save(account);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{id}")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, "User registered successfully", location.toString()));
    }

    /**
     * Method that will delete an Account based on its id
     *
     * @param id identifier of the Account
     * @return Success - 200 Ok and Deleted Account
     *         Fail - 404 Not Found
     */
    @DeleteMapping("/{id}")
    @JsonView(Account.WithoutPasswordView.class)
    public ResponseEntity deleteUserById(@Valid @PathVariable Long id) {
        Account account = accountService.deleteById(id);
        if (account == null) {
            return new ResponseEntity<>(new ApiResponse(false, HttpStatus.BAD_REQUEST.getReasonPhrase(), null), HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity
                    .ok(account);

        }
    }

    /** Method that will get all Accounts from DB
     *
     * @return A List with the Accounts
     */
    @GetMapping()
    @JsonView(Account.WithoutPasswordView.class)
    public ResponseEntity getUsers(){
        return ResponseEntity.ok(accountService.findAll());
    }

    /**
     * @param signInRequest object that will contain UsernameOrEmail and Password
     * @return AuthenticationResponse object containing the JwtToken
     */
    private JwtAuthenticationResponse authenticate(SignInRequest signInRequest) {
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                signInRequest.getUsernameOrEmail(),
                                signInRequest.getPassword()
                        )
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return new JwtAuthenticationResponse(jwt);
    }

}
