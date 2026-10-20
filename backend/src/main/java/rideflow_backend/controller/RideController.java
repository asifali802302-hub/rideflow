package rideflow_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public Ride bookRide(
            @RequestBody Ride ride,
            Authentication authentication) {

        return rideService.bookRide(
                authentication.getName(), ride);
    }

    @GetMapping("/mine")
    public List<Ride> getMyRides(Authentication authentication) {
        return rideService.getRidesByRiderEmail(
                authentication.getName());
    }
 @GetMapping("/available")
@PreAuthorize("hasRole('DRIVER')")
public List<Ride> getAvailableRides() {
    return rideService.getAvailableRides();
}
@PostMapping("/{rideId}/accept")
@PreAuthorize("hasRole('DRIVER')")
public Ride acceptRide(
        @PathVariable Long rideId,
        Authentication authentication) {

    return rideService.acceptRide(
            rideId, authentication.getName());
}
@GetMapping("/driver/mine")
@PreAuthorize("hasRole('DRIVER')")
public List<Ride> getMyDriverRides(Authentication authentication) {
    return rideService.getRidesByDriverEmail(authentication.getName());
}
}