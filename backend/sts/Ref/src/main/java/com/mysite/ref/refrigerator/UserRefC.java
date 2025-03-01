package com.mysite.ref.refrigerator;

import com.mysite.ref.user.Users;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class UserRefC {
    @EmbeddedId
    private UserRefCId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @MapsId("refrigeratorId")
    @JoinColumn(name = "refrigerator_id")
    private Refrigerator refrigerator;

    private int manageLevel;
    
    public void setUserAndRefrigerator(Users user, Refrigerator refrigerator, int manageLevel) {
    	this.id = new UserRefCId(user.getUserid(),refrigerator.getRefrigeratorId());
        this.user = user;
        this.refrigerator = refrigerator;
        this.manageLevel = manageLevel;

        if (!user.getUserRefC().contains(this)) {
            user.getUserRefC().add(this);
        }
        if (!refrigerator.getUserRefC().contains(this)) {
            refrigerator.getUserRefC().add(this);
        }
    }

    public void removeUser() {
        if (this.user != null) {
            this.user.getUserRefC().remove(this);
            this.user = null;
        }
    }

    public void removeRefrigerator() {
        if (this.refrigerator != null) {
            this.refrigerator.getUserRefC().remove(this);
            this.refrigerator = null;
        }
    }
}
