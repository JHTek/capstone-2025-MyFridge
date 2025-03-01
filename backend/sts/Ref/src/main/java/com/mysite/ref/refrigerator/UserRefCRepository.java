package com.mysite.ref.refrigerator;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRefCRepository extends JpaRepository<UserRefC, UserRefCId> {
	@Query("SELECT urc.refrigerator FROM UserRefC urc WHERE urc.user.userid = :userId")
	List<Refrigerator> findRefrigeratorsByUserId(@Param("userId") String userId);
	
}
