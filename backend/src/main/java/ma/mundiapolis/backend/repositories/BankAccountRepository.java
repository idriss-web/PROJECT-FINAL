package ma.mundiapolis.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.mundiapolis.backend.entities.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}
