package com.pos.be.mappers;

import com.pos.be.dto.transactions.TransactionDTO;
import com.pos.be.entity.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(source = "consignment.consignmentId", target = "consignmentId")
    TransactionDTO toDTO(Transaction transaction);

    @Mapping(source = "consignmentId", target = "consignment.consignmentId")
    Transaction toEntity(TransactionDTO transactionDTO);

}

