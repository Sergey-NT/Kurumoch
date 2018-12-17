package ru.samara.airport.www.kurumoch;

public class ObjectPlane {

    private String planeFlight;
    private String planeDirection;
    private String planeType;
    private String planeTimePlan;
    private String planeTimeFact;
    private String planeStatus;
    private String planeBaggageStatus;
    private String planeGate;
    private String planeCheckIn;
    private String planeCombination;
    private String planeRoute;
    private String planeRouteStatus;
    private String planeAirline;
    private String registrationBegin;
    private String registrationEnd;
    private String checkInStatus;
    private String boardingStatus;
    private String boardingEnd;
    private boolean planeTracking;

    public ObjectPlane(String planeFlight, String planeDirection, String planeType, String planeTimePlan, String planeTimeFact, String planeStatus, boolean planeTracking, String planeBaggageStatus, String planeGate, String planeCheckIn, String planeCombination, String planeRoute, String planeRouteStatus, String registrationBegin, String registrationEnd, String checkInStatus, String boardingEnd, String boardingStatus, String planeAirline) {
        this.planeFlight = planeFlight;
        this.planeDirection = planeDirection;
        this.planeType = planeType;
        this.planeTimePlan = planeTimePlan;
        this.planeTimeFact = planeTimeFact;
        this.planeStatus = planeStatus;
        this.planeTracking = planeTracking;
        this.planeBaggageStatus = planeBaggageStatus;
        this.planeGate = planeGate;
        this.planeCheckIn = planeCheckIn;
        this.planeCombination = planeCombination;
        this.planeRoute = planeRoute;
        this.planeRouteStatus = planeRouteStatus;
        this.planeAirline = planeAirline;
        this.registrationBegin = registrationBegin;
        this.registrationEnd = registrationEnd;
        this.checkInStatus = checkInStatus;
        this.boardingEnd = boardingEnd;
        this.boardingStatus = boardingStatus;
    }

    public String getPlaneFlight() {
        return planeFlight;
    }

    public String getPlaneDirection() {
        return planeDirection;
    }

    public String getPlaneType() {
        return planeType;
    }

    public String getPlaneTimePlan() {
        return planeTimePlan;
    }

    public String getPlaneTimeFact() {
        return planeTimeFact;
    }

    public String getPlaneStatus() {
        return planeStatus;
    }

    public String getPlaneBaggageStatus() {
        return planeBaggageStatus;
    }

    public String getPlaneGate() {
        return planeGate;
    }

    public String getPlaneCheckIn() {
        return planeCheckIn;
    }

    public String getPlaneCombination() {
        return planeCombination;
    }

    public String getPlaneRoute() {
        return planeRoute;
    }

    public String getPlaneRouteStatus() {
        return planeRouteStatus;
    }

    public String getPlaneAirline() {
        return planeAirline;
    }

    public String getRegistrationBegin() {
        return registrationBegin;
    }

    public String getRegistrationEnd() {
        return registrationEnd;
    }

    public String getCheckInStatus() {
        return checkInStatus;
    }

    public String getBoardingStatus() {
        return boardingStatus;
    }

    public String getBoardingEnd() {
        return boardingEnd;
    }

    public boolean isPlaneTracking() {
        return planeTracking;
    }

    public void setPlaneTracking(boolean planeTracking) {
        this.planeTracking = planeTracking;
    }
}