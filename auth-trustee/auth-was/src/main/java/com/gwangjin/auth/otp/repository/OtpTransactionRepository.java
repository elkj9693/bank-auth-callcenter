//OtpTransaction 테이블에 접근하는 DAO(데이터 접근 객체)
// JPA로 “save(), findById() 같은 DB 작업을 자동으로 제공

package com.gwangjin.auth.otp.repository;

import com.gwangjin.auth.otp.domain.OtpTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtpTransactionRepository extends JpaRepository<OtpTransaction, UUID> {
}
