package com.micro.auth.dto;

import com.generic.service.model.GenericLoggedInUserData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class GenericLoggedInUserDataImpl extends GenericLoggedInUserData {
    private UUID jwtTokenId;

    public static GenericLoggedInUserDataImpl cast(GenericLoggedInUserData genericLoggedInUserData){
        if(genericLoggedInUserData instanceof GenericLoggedInUserDataImpl){
            return (GenericLoggedInUserDataImpl) genericLoggedInUserData;
        }
        return null;
    }
}
