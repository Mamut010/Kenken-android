package au.edu.jcu.cp5307.proj2.constants;

public enum Difficulty {
    Basic("Basic", 0),
    Easy("Easy", 1),
    Normal("Normal", 2),
    Medium("Medium", 3),
    Hard("Hard", 4),
    VeryHard("Very Hard", 5),
    Extreme("Extreme", 6);

    private final String label;
    private final int level;

    Difficulty(String label, int level) {
        this.label = label;
        this.level = level;
    }

    public String getLabel() {
        return label;
    }

    public int getLevel() {
        return level;
    }
}
