package com.cydeo.entity;

import com.cydeo.utils.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "users")
@Where(clause = "is_Deleted = false")
public class User extends BaseEntity {

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String userName;
    private String passWord;
    private boolean enabled;
    private String phone;

  //  @ManyToOne(fetch = FetchType.LAZY)    // security doesn't work with lazy
    @ManyToOne()
    @JoinColumn(name = "role_id")
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

}
