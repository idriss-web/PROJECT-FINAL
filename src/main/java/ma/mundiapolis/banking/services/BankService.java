package ma.mundiapolis.banking.services;

import jakarta.transaction.Transactional;
import ma.mundiapolis.banking.entities.BankAccount;
import ma.mundiapolis.banking.entities.CurrentAccount;
import ma.mundiapolis.banking.entities.SavingAccount;
import ma.mundiapolis.banking.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BankService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    public void consulter() {

        BankAccount bankAccount = bankAccountRepository.findById("ID").orElse(null);
        if (bankAccount != null) {
            System.out.println("***********************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getCustomer().getName());

            if (bankAccount instanceof CurrentAccount) {
                System.out.println("Over Draft => " + ((CurrentAccount) bankAccount).getOverDraft());
            } else if (bankAccount instanceof SavingAccount) {
                System.out.println("Rate => " + ((SavingAccount) bankAccount).getInterestRate());
            }

            bankAccount.getAccountOperations().forEach(op -> {
                System.out.println("=================================");
                System.out.println(op.getType() + "\t" + op.getOperationDate() + "\t" + op.getAmount());
            });
        }
    }
}
