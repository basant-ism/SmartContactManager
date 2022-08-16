package com.smartcontactmanager.dao;

import java.util.List;

 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontactmanager.entities.Contact;



public interface ContactRepository extends JpaRepository<Contact,Integer>{

    @Query("from Contact as c where c.user.id=:userid")
    public Page<Contact> findContactsByUser(@Param("userid") Integer userid,Pageable pageable);
    
}
