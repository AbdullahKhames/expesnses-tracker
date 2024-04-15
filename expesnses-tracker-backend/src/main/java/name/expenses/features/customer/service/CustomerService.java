package name.expenses.features.customer.service;

import jakarta.ejb.Local;

import name.expenses.features.customer.dtos.request.CustomerReqDto;
import name.expenses.features.customer.dtos.request.CustomerUpdateDto;
import name.expenses.features.customer.models.Customer;
import name.expenses.globals.CrudService;
import name.expenses.features.association.UpdateAssociation;

@Local
public interface CustomerService extends
//        CollectionAdder<Customer>,
//        CollectionRemover <Customer>,
        UpdateAssociation<Customer, CustomerUpdateDto>,
        CrudService<CustomerReqDto, CustomerUpdateDto, String, Customer> {
    Customer update(Customer customer);
}