package name.expenses.features.budget_transfer.service;

import jakarta.ejb.Local;

import name.expenses.features.association.UpdateAssociation;
import name.expenses.features.budget_transfer.dtos.request.BudgetTransferReqDto;
import name.expenses.features.budget_transfer.dtos.request.BudgetTransferUpdateDto;
import name.expenses.features.budget_transfer.models.BudgetTransfer;
import name.expenses.globals.CrudService;

@Local
public interface BudgetTransferService extends CrudService<BudgetTransferReqDto, BudgetTransferUpdateDto, String, BudgetTransfer> ,
        UpdateAssociation<BudgetTransfer, BudgetTransferUpdateDto> {

}