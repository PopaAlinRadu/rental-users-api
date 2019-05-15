package ro.nila.ra.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ro.nila.ra.dto.*;
import ro.nila.ra.payload.ApiResponse;
import ro.nila.ra.security.JwtTokenProvider;
import ro.nila.ra.service.account.AccountService;
import ro.nila.ra.service.person.PersonService;


import javax.validation.Valid;
import java.util.List;

@RestController
public class UsersApiController implements UsersApi {

    private JwtTokenProvider jwtTokenProvider;
    private AccountService accountService;
    private PersonService personService;

    public UsersApiController(
            JwtTokenProvider jwtTokenProvider,
            AccountService accountService,
            PersonService personService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.accountService = accountService;
        this.personService = personService;
    }

    @Override
    public ResponseEntity<ApiResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        JwtAuthenticationResponse jwtAuthenticationResponse = jwtTokenProvider.authenticate(signInRequest);
        return new ResponseEntity<>
                (new ApiResponse<>(true, jwtAuthenticationResponse), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        AccountDTO accountDTO = accountService.save(signUpRequest);
        return new ResponseEntity<>
                (new ApiResponse<>(true, accountDTO), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteUserById(@Valid @PathVariable Long id) {
        AccountDTO accountDTO = accountService.deleteById(id);
        return new ResponseEntity<>(new ApiResponse<>(true, accountDTO), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> getAccount(@Valid @PathVariable Long id) {
        AccountDTO accountDTO = accountService.findById(id);
        return new ResponseEntity<>(new ApiResponse<>(true, accountDTO), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> getUsers() {
        List<AccountDTO> accountDTOList = accountService.findAll();
        return new ResponseEntity<>(new ApiResponse<>(true, accountDTOList), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> getAccountByUsernameOrEmail(@Valid @RequestHeader String usernameOrEmail) {
        AccountDTO accountDTO = accountService.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        return new ResponseEntity<>(new ApiResponse<>(true, accountDTO), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> saveOrUpdatePerson(@Valid @RequestBody PersonDTO personDTO) {
        PersonDTO result = personService.upsert(personDTO);
        return new ResponseEntity<>(new ApiResponse<>(true, result), HttpStatus.CREATED);
    }
}
