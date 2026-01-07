package ma.mundiapolis.backend.services;

import java.util.List;

import ma.mundiapolis.backend.dtos.AccountHistoryDTO;
import ma.mundiapolis.backend.dtos.AccountOperationDTO;
import ma.mundiapolis.backend.dtos.BankAccountDTO;
import ma.mundiapolis.backend.dtos.CurrentBankAccountDTO;
import ma.mundiapolis.backend.dtos.CustomerDTO;
import ma.mundiapolis.backend.dtos.SavingBankAccountDTO;
import ma.mundiapolis.backend.exceptions.BalanceNotSufficientException;
import ma.mundiapolis.backend.exceptions.BankAccountNotFoundException;
import ma.mundiapolis.backend.exceptions.CustomerNotFoundException;
public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<CustomerDTO> searchCustomers(String keyword);
}
