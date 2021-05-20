
public enum Planet{
    MARS("Mars", 1000),
    JUPITER("Jupiter", 2000),
    SATURN("Saturn", 3000),
    URANUS("Uranus", 4000),
    NEPTUNE("Neptune", 5000),
    PLUTO("Pluto", 6000);

    public String name;
    public int fuelRequired;

    private Planet(String name, int fuelRequired){
        this.name = name;
        this.fuelRequired = fuelRequired;
    }

    //number of days, (200 fuel per month)
    int timeToPlanet(){
        return fuelRequired/200 * 1;
    }
}