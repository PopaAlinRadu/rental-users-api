package ro.nila.ra.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ro.nila.ra.model.Account;
import ro.nila.ra.model.Role;
import ro.nila.ra.model.enums.RoleName;
import ro.nila.ra.model.view.Views;
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

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

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
        String username = signUpRequest.getUsername();
        if (accountService.existsByUsername(username)) {
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!", HttpStatus.BAD_REQUEST.getReasonPhrase()),
                    HttpStatus.BAD_REQUEST);
        }
        String email = signUpRequest.getEmail();
        if (accountService.existsByEmail(email)) {
            return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!", HttpStatus.BAD_REQUEST.getReasonPhrase()),
                    HttpStatus.BAD_REQUEST);
        }
        //  Creating user's account
        Account account = new Account(signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        //  Setting role for account
        Role accountRole;
        if (signUpRequest.getRoles() == null || signUpRequest.getRoles().iterator().next().getRoleName().equals(RoleName.ROLE_USER)) {
            accountRole = roleService.findByRoleName(RoleName.ROLE_USER);
        } else {
            accountRole = roleService.findByRoleName(RoleName.ROLE_ADMIN);
        }
        account.setRoles(Collections.singleton(accountRole));
        Account result = accountService.save(account);
        //  Create Location
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
    @JsonView(Views.WithRole.class)
    public ResponseEntity deleteUserById(@Valid @PathVariable Long id) {
        Account account = accountService.deleteById(id);
        if (account == null) {
            return new ResponseEntity<>(
                    new ApiResponse(false, HttpStatus.BAD_REQUEST.getReasonPhrase(), null),
                    HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity
                    .ok(account);

        }
    }

    /**
     * Method that will get a Account based on its id
     *
     * @param id identifier of the Account
     * @return Success - 200 Ok and the Account
     *         Fail - 404 Not Found
     */
    @GetMapping("/{id}")
    @JsonView(Views.WithRole.class)
    public ResponseEntity getAccount(@Valid @PathVariable Long id){
        if (!accountService.findById(id).isPresent()){
            return new ResponseEntity<>(
                    new ApiResponse(false, HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.getReasonPhrase()),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity
                .ok(accountService.findById(id));
    }

    /**
     * Method that will get all Accounts from DB
     *
     * @return A List with the Accounts
     */
    @GetMapping()
    @JsonView(Views.WithRole.class)
    public ResponseEntity getUsers() {
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
