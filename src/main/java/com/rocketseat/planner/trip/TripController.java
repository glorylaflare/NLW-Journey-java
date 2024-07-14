package com.rocketseat.planner.trip;

import com.rocketseat.planner.activity.ActivityData;
import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityResponse;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.link.LinkData;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponse;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ParticipantService participantService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private LinkService linkService;

    //Método para criar viagem e gerar um UUID
    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
        Trip newTrip = new Trip(payload);
        this.tripRepository.save(newTrip);
        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    //Método para resgatar os detalhes de uma viagem através do id gerado pelo método (createTrip)
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Método para atualizar dados de uma viagem já criada pelo método (createTrip)
    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip rawTrip = trip.get();
            rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setDestination(payload.destination());

            this.tripRepository.save(rawTrip);
            return ResponseEntity.ok(rawTrip);
        }

        return ResponseEntity.notFound().build();
    }

    //Método para confirmar uma viagem, alterando o status do "isConfirmed" de *false* para *true*
    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip rawTrip = trip.get();
            rawTrip.setIsConfirmed(true);

            this.tripRepository.save(rawTrip);
            this.participantService.triggerConfirmationEmailToParticipants(id);
            return ResponseEntity.ok(rawTrip);
        }
        return ResponseEntity.notFound().build();
    }

    //Método para convidar participante através do e-mail
    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip rawTrip = trip.get();

            ParticipantCreateResponse participantCreateResponse = this.participantService.registerParticipantToEvent(payload.email(), rawTrip);

            if(rawTrip.getIsConfirmed()) {
                this.participantService.triggerConfirmationEmailToParticipant(payload.email());
            }

            return ResponseEntity.ok(participantCreateResponse);
        }
        return ResponseEntity.notFound().build();
    }

    //Método para resgatar todos os participantes convidados para a viagem
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id) {
        List<ParticipantData> participantList = this.participantService.getAllParticipantsFromEvent(id);

        return ResponseEntity.ok(participantList);
    }

    //Método para registrar atividade durante a viagem
    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip rawTrip = trip.get();

            ActivityResponse activityResponse = this.activityService.registerActivity(payload, rawTrip);

            return ResponseEntity.ok(activityResponse);
        }
        return ResponseEntity.notFound().build();
    }

    //Método para resgatar todas as atividades da viagem
    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id) {
        List<ActivityData> activityDataList = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activityDataList);
    }

    //Método para registrar link para a viagem
    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if(trip.isPresent()) {
            Trip rawTrip = trip.get();

            LinkResponse linkResponse = this.linkService.registerLink(payload, rawTrip);

            return ResponseEntity.ok(linkResponse);
        }
        return ResponseEntity.notFound().build();
    }

    //Método para resgatar todos os links da viagem
    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAlllinks(@PathVariable UUID id) {
        List<LinkData> linkDataList = this.linkService.getAllLinksFromId(id);

        return ResponseEntity.ok(linkDataList);
    }
}
