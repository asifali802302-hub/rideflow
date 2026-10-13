package rideflow_backend.service;

import org.springframework.stereotype.Service;
import rideflow_backend.entity.Ride;
import rideflow_backend.entity.RideStatus;
import rideflow_backend.repository.RideRepository;

import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public Ride bookRide(Ride ride) {
        ride.setStatus(RideStatus.REQUESTED);
        return rideRepository.save(ride);
    }

    public List<Ride> getRidesByRider(Long riderId) {
        return rideRepository.findByRiderIdOrderByBookedAtDesc(riderId);
    }

    public List<Ride> getRidesByDriver(Long driverId) {
        return rideRepository.findByDriverIdOrderByBookedAtDesc(driverId);
    }
}