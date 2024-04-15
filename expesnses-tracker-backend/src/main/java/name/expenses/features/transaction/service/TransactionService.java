package name.expenses.features.transaction.service;

import jakarta.ejb.Local;
import name.expenses.features.transaction.dtos.request.TransactionReqDto;
import name.expenses.features.transaction.dtos.request.TransactionUpdateDto;
import name.expenses.features.transaction.models.Transaction;
import name.expenses.globals.CrudService;

@Local
public interface TransactionService extends CrudService<TransactionReqDto, TransactionUpdateDto, String, Transaction> {

}