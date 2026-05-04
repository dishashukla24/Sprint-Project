package com.classicmodel.repository;

import com.classicmodel.entity.Payment;
import com.classicmodel.entity.PaymentId;
import com.classicmodel.projection.PaymentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(path = "payments", excerptProjection = PaymentProjection.class)
public interface PaymentRepository extends JpaRepository<Payment, PaymentId> {

    List<Payment> findByCustomer_CustomerNumber(Integer customerNumber);

    List<Payment> findByPaymentDate(LocalDate paymentDate);

    List<Payment> findById_CheckNumber(String checkNumber);
    long count();

}