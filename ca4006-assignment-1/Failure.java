public enum Failure{
    ENGINE("Engine Failure", true),
    FUEL_TANK("Fuel Tank Leak", true),
    ABORT("Mission Aborted", true),
    EXPLOSION("Rapid Unplanned Disassebly", true),
    LOST("Guidance System Misaligned", true),
    UPSIDE_DOWN("Engine pointing upside down", true),
    INSTRUMENT("Instruments sampling craft instead of planet", true),
    POWER_PLANT("Power Plant using power instead of generating it", true),
    KESSLER_SYNDROME("Suffering From Kessler Syndrome", true);

    public String name;
    public boolean recovarable;

    private Failure(String name, boolean recovarable){
        this.name = name;
        this.recovarable = recovarable;
    }
}