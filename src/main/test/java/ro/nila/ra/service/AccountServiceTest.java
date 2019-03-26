package ro.nila.ra.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ro.nila.ra.model.Account;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Account account;

    @Before
    public void init(){
        account = new Account();
        account.setUsername("test_account");
        account.setEmail("test_account@test.com");
        account.setPassword(passwordEncoder.encode("test_account"));

        accountService.save(account);
    }

    @Test
    public void test_delete_account(){

        accountService.deleteById(account.getId());

    }
}
