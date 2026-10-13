package rideflow_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rideflow_backend.entity.Ride;
import rideflow_backend.service.RideService;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ride bookRide(@RequestBody Ride ride) {
        if (ride.getPickupLocation() == null
                || ride.getPickupLocation().isBlank()
                || ride.getDropLocation() == null
                || ride.getDropLocation().isBlank()
                || ride.getFare() == null
                || ride.getFare().signum() < 0
                || ride.getRiderId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid ride details");
        }

        return rideService.bookRide(ride);
    }

    @GetMapping("/rider/{riderId}")
    public List<Ride> getRidesByRider(@PathVariable Long riderId) {
        return rideService.getRidesByRider(riderId);
    }

    @GetMapping("/driver/{driverId}")
    public List<Ride> getRidesByDriver(@PathVariable Long driverId) {
        return rideService.getRidesByDriver(driverId);
    }
}