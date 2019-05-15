package ro.nila.ra.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ro.nila.ra.model.Account;
import ro.nila.ra.service.account.AccountService;
import ro.nila.ra.service.article.ArticleService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ArticleService articleService;

    private Account account;

    @Before
    public void init() {

    }

    @Test
    public void test_delete_account() {

    }
}
