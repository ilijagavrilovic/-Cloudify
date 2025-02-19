package com.cloudify.v1.viri;
import com.cloudify.entities.Notification;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.HttpURLConnection;
import java.time.LocalDateTime;

import java.net.URL;
import java.util.logging.Logger;
import java.util.UUID;
import javax.inject.Inject;
import com.cloudify.beans.NotificationBean;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Path("/notifications")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotificationService {

    @Inject
    NotificationBean notificationBean;

    private static List<Notification> notifications = new ArrayList<Notification>() {{
        add(new Notification("1", "198772", "Attention: Weather Alert", "Due to bad weather, your flight is cancelled."));
        add(new Notification("2", "198773", "Flight Delay Notice", "Your flight is delayed by 2 hours."));
        add(new Notification("3", "198774", "Flight Rescheduled", "Your flight has been rescheduled to tomorrow."));
        add(new Notification("4", "198775", "New Seat Availability", "New seats are available on your flight."));
    }};

    private static final Logger LOG = Logger.getLogger(NotificationService.class.getSimpleName());

    @Operation(description = "Retrieve all notifications.", summary = "Get all notifications")
    @APIResponses({
            @APIResponse(description = "Successful retrieval of all notifications!", responseCode = "200", content = @Content(schema = @Schema(implementation = Notification.class))),
            @APIResponse(description = "No notifications found!", responseCode = "404")
    })
    @Tag(name = "Notification Service")
    @GET
    public Response getAllNotifications() {
        if (notifications.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No notifications available.")
                    .header("Access-Control-Allow-Origin", "http://localhost:4200")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                    .build();
        }
        return Response.ok(notifications)
                .header("Access-Control-Allow-Origin", "http://localhost:4200")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .build();
    }


    @Operation(description = "Retrieve the details of a specific notification.", summary = "Get an existing notification")
    @APIResponses({
            @APIResponse(description = "Successful retrieval of notification!", responseCode = "200", content = @Content(schema = @Schema(implementation = Notification.class))),
            @APIResponse(description = "Notification not found!", responseCode = "404")
    })
    @Tag(name = "Notification Service")
    @GET
    @Path("/{notificationId}")
    public Response getNotification(@PathParam("notificationId") String notificationId) {
        for (Notification notification : notifications) {
            if (notification.getNotificationId().equals(notificationId)) {
                return Response.ok(notification)
                        .header("Access-Control-Allow-Origin", "http://localhost:4200")
                        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                        .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                        .build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Notification with ID " + notificationId + " not found.")
                .header("Access-Control-Allow-Origin", "http://localhost:4200")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .build();
    }

    // Ustvari novo obvestilo
    @Operation(description = "Send a notification to users about flight-related information.", summary = "Create a new notification")
    @APIResponses({
            @APIResponse(description = "Notification successfully created!", responseCode = "201", content = @Content(schema = @Schema(implementation = Notification.class))),
            @APIResponse(description = "Invalid input!", responseCode = "404")
    })
    @Tag(name = "Notification Service")
    @POST
    public Response createNotification(Notification notificationDetails) {

        String notificationId = UUID.randomUUID().toString();

        Notification newNotification = new Notification(
                notificationId,
                notificationDetails.getUserId(),
                notificationDetails.getNotifiedUsers(),
                notificationDetails.getTextLength(), notificationDetails.getSendingTime(),
                notificationDetails.getTitle(),
                notificationDetails.getContent(),
                notificationDetails.getNotificationType());

        notificationBean.sendEmail(
                "ilijagavrilovic03@gmail.com",
                "New Notification",
                "Flight is cancelled due to bad weather"
        );

        return Response.status(201).entity(newNotification).build();
    }

    // Posodobi obstoječe obvestilo
    @Operation(description = "Modify the content or details of an existing notification.", summary = "Update an existing notification")
    @APIResponses({
            @APIResponse(description = "Notification successfully updated", responseCode = "200", content = @Content(schema = @Schema(implementation = Notification.class))),
            @APIResponse(description = " Invalid input data!", responseCode = "400"),
            @APIResponse(description = " Notification not found!", responseCode = "404")
    })
    @Tag(name = "Notification Service")
    @PUT
    @Path("/{notificationId}")
    public Response updateNotification(@PathParam("notificationId") String notificationId, Notification notificationDetails) {


        //Notification existingBooking = notificationDatabase.get(notificationId);
        Notification existingBooking = new Notification();
        boolean obstaja = true; // ako podataka sa id obstaja v bazi podataka

        if (existingBooking!=null || obstaja) {

            // TO DO
            // Posodobimo listu obavestenih korisnika

            existingBooking.setContent(notificationDetails.getContent());
            existingBooking.setTextLength(notificationDetails.getTextLength());
            existingBooking.setNotificationType(notificationDetails.getNotificationType());



            return Response.ok(existingBooking).build();
        } else {
            return Response.status(404).build();
        }
    }

    // Izbriši obstoječe obvestilo
    @Operation(description = "Remove an existing notification.", summary = "Delete a notification")
    @APIResponses({
            @APIResponse(description = "Notification successfully deleted!", responseCode = "200"),
            @APIResponse(description = "Notification not found!", responseCode = "404")
    })
    @Tag(name = "Notification Service")
    @DELETE
    @Path("/{notificationId}")
    public Response deleteNotification(@PathParam("notificationId") String notificationId) {

//        if (!notificationDatabase.containsKey(notificationId)) {
//            return Response.status(404).build();
//        }
//
//        bookingDatabase.remove(bookingId);
        return Response.ok().build();
    }

    @Operation(description = "Health check endpoint to verify the service for a specific notification is running.", summary = "Health Check for Notification ID")
    @APIResponses({
            @APIResponse(description = "Service for notification is healthy!", responseCode = "200"),
            @APIResponse(description = "Service for notification is unavailable!", responseCode = "503")
    })
    @Tag(name = "Notification Service")
    @GET
    @Path("/{notificationId}/health")
    public Response healthCheck(@PathParam("notificationId") String notificationId) {
        String targetUrl = "http://localhost:8080/v1/notifications/" + notificationId;

        boolean isHealthy = checkUrl(targetUrl);

        if (isHealthy) {
            return Response.ok("Service for notification " + notificationId + " is running and healthy!").build();
        } else {
            return Response.status(503).entity("Service for notification " + notificationId + " is unavailable!").build();
        }
    }

    private boolean checkUrl(String targetUrl) {
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (Exception e) {
            LOG.severe("Health check failed for URL: " + targetUrl + " - " + e.getMessage());
        }
        return false;
    }

}
