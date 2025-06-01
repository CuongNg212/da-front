package jwd.practice.shopservice.entity.voucher;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "voucher_user")
public class Voucher_User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer voucher_userId;

    int userId;

    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = false)
    Voucher voucher;
}
