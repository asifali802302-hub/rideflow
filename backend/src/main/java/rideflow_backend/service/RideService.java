package rideflow_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import rideflow_backend.entity.Ride;
import rideflow_backend.entity.RideStatus;
import rideflow_backend.entity.User;
import rideflow_backend.repository.RideRepository;
import rideflow_backend.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideService(
            RideRepository rideRepository,
            UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Ride bookRide(String authenticatedEmail, Ride ride) {
        User rider = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rider not found"));

        if (ride.getPickupLocation() == null
                || ride.getPickupLocation().isBlank()
                || ride.getDropLocation() == null
                || ride.getDropLocation().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pickup and drop locations are required");
        }

        ride.setRiderId(rider.getId());
        ride.setDriverId(null);
        ride.setStatus(RideStatus.REQUESTED);

        // Temporary fixed fare; server-side fare calculation comes next.
        ride.setFare(new BigDecimal("150.00"));

        return rideRepository.save(ride);
    }

    public List<Ride> getRidesByRiderEmail(String email) {
    User rider = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Rider not found"));

    return rideRepository.findByRiderIdOrderByBookedAtDesc(
            rider.getId());
}

    public List<Ride> getRidesByDriver(Long driverId) {
        return rideRepository.findByDriverIdOrderByBookedAtDesc(driverId);
    }
    public List<Ride> getRidesByDriverEmail(String email) {
    User driver = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Driver not found"));

    if (driver.getRole() != User.Role.DRIVER) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Only drivers can view driver rides");
    }

    return rideRepository.findByDriverIdOrderByBookedAtDesc(driver.getId());
}
    public List<Ride> getAvailableRides() {
    return rideRepository.findByStatusOrderByBookedAtAsc(
            RideStatus.REQUESTED);
}
@Transactional
public Ride acceptRide(Long rideId, String driverEmail) {

    User driver = userRepository.findByEmail(driverEmail)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Driver not found"));

    if (driver.getRole() != User.Role.DRIVER) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Only drivers can accept rides");
    }

    Ride ride = rideRepository.findByIdForUpdate(rideId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ride not found"));

    if (ride.getStatus() != RideStatus.REQUESTED) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This ride is no longer available");
    }

    ride.setDriverId(driver.getId());
    ride.setStatus(RideStatus.ACCEPTED);

    return rideRepository.save(ride);
}
}