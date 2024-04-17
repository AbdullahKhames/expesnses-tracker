package name.expenses.features.pocket_transfer.service;

import jakarta.ejb.Local;

import name.expenses.features.pocket_transfer.dtos.request.PocketTransferReqDto;
import name.expenses.features.pocket_transfer.dtos.request.PocketTransferUpdateDto;
import name.expenses.features.pocket_transfer.models.PocketTransfer;
import name.expenses.globals.CrudService;

@Local
public interface PocketTransferService extends CrudService<PocketTransferReqDto, PocketTransferUpdateDto, String, PocketTransfer> {

}