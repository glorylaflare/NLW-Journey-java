package com.rocketseat.planner.link;

import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    public LinkResponse registerLink(LinkRequestPayload payload, Trip trip) {
        Link link = new Link(payload.title(), payload.url(), trip);

        this.linkRepository.save(link);
        return new LinkResponse(link.getId());
    }

    public List<LinkData> getAllLinksFromId(UUID tripId) {
        return this.linkRepository.findByTripId(tripId).stream().map(l -> new LinkData(l.getId(),
                l.getTitle(), l.getUrl())).toList();
    }
}
