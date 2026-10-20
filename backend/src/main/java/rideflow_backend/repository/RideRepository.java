package rideflow_backend.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import rideflow_backend.entity.Ride;
import rideflow_backend.entity.RideStatus;

import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByRiderIdOrderByBookedAtDesc(Long riderId);

    List<Ride> findByDriverIdOrderByBookedAtDesc(Long driverId);

    List<Ride> findByStatusOrderByBookedAtAsc(RideStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Ride r WHERE r.id = :rideId")
    Optional<Ride> findByIdForUpdate(@Param("rideId") Long rideId);
}