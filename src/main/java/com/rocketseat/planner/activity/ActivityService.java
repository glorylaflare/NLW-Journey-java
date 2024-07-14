package com.rocketseat.planner.activity;

import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public ActivityResponse registerActivity(ActivityRequestPayload payload, Trip trip) {
        Activity activity = new Activity(payload.title(), payload.occurs_at(), trip);

        this.activityRepository.save(activity);
        return new ActivityResponse(activity.getId());
    }

    public List<ActivityData> getAllActivitiesFromId(UUID tripId) {
        return this.activityRepository.findByTripId(tripId).stream().map(a -> new ActivityData(a.getId(),
                a.getTitle(), a.getOccursAt())).toList();
    }
}
