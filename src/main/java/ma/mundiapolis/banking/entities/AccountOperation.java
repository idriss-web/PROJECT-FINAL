package ma.mundiapolis.banking.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOperation {
private long id;
private Date operationDate;
private double amount;
private OperationType type;
private BankAccount bankAccount;
}
