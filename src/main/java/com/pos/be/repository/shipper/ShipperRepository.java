package com.pos.be.repository.shipper;

import com.pos.be.entity.shipper.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipperRepository extends JpaRepository<Shipper, Long> {
}
