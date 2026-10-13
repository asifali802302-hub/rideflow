package rideflow_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rideflow_backend.entity.Ride;
import rideflow_backend.entity.RideStatus;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByRiderIdOrderByBookedAtDesc(Long riderId);

    List<Ride> findByDriverIdOrderByBookedAtDesc(Long driverId);

    List<Ride> findByStatusOrderByBookedAtAsc(RideStatus status);
}