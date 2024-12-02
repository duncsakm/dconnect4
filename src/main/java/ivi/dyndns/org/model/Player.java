package ivi.dyndns.org.model;

public class Player {
    private final String name;
    private final char disc;

    public Player(String name, char disc) {
        this.name = name;
        this.disc = disc;
    }

    public String getName() {
        return name;
    }

    public char getDisc() {
        return disc;
    }


    @Override
    public String toString() {
        return "Player{name='" + name + "', disc=" + disc + '}';
    }

}