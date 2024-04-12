package name.expenses.features.user.service;

import jakarta.ejb.Local;
import name.expenses.features.user.dtos.request.ValidAuthDto;
import name.expenses.features.user.dtos.request._2authDto;
import name.expenses.globals.responses.ResponseDto;

@Local
public interface _2authServices {


    ResponseDto add_2auth(_2authDto authDto);

    ResponseDto get_2authById(Long id);

    ResponseDto get_2authByEmail(String email);

    void remove_2authById(Long id);

    void remove_2authByEmail(String email);
    ResponseDto codeVerification(ValidAuthDto authDto);



}